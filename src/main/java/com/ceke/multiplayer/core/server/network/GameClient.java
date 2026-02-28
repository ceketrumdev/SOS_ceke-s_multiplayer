/* ************************************************************************** */
/*                                                                            */
/*       ::::::::  :::::::::: :::    ::: ::::::::::                           */
/*     :+:    :+: :+:        :+:   :+:  :+:                                   */
/*    +:+        +:+        +:+  +:+   +:+                                    */
/*   +#+        +#++:++#   +#++:++    +#++:++#                                */
/*  +#+        +#+        +#+  +#+   +#+                                      */
/* #+#    #+# #+#        #+#   #+#  #+#                                       */
/* ########  ########## ###    ### ##########                                 */
/*                                                                            */
/*   GameClient.java                                                        */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network;

import com.ceke.multiplayer.core.server.network.packets.*;
import com.ceke.multiplayer.core.server.sync.CursorSyncManager;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Game Client — connects to the host's {@link HostServer}.
 *
 * Responsibilities:
 * - Maintain the TCP+UDP connection to the host on port
 * {@link HostServer#PORT}.
 * - Send local player inputs (via {@link #sendInput}) to the authoritative
 * host.
 * - Send local cursor position every cursor-tick via UDP.
 * - Apply incoming {@link PacketGameState} ticks to reconcile local game state.
 */
public final class GameClient {

    private static final Logger LOG = Logger.getLogger(GameClient.class.getName());

    /** Allow up to 10MB for transferring the zipped save game */
    private static final int WRITE_BUFFER = 10_000_000;
    private static final int OBJECT_BUFFER = 10_000_000;

    /** How often (ms) the client sends its cursor position. */
    private static final int CURSOR_SEND_INTERVAL_MS = 50; // ~20 Hz

    private final Client client;
    private final ScheduledExecutorService cursorExecutor;

    private volatile boolean connected = false;

    private final String playerName;
    private final String modVersion;

    public GameClient(String playerName, String modVersion) {
        this.playerName = playerName;
        this.modVersion = modVersion;

        client = new Client(WRITE_BUFFER, OBJECT_BUFFER);
        PacketRegistry.register(client);

        cursorExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ceke-multiplayer-cursor-sender");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Connects to the given host IP on {@link HostServer#PORT}.
     * Blocking for up to {@code timeoutMs} ms.
     */
    public void connect(String hostIp, int timeoutMs) throws IOException {
        client.start();
        PacketRegistry.register(client);
        registerListeners();
        client.connect(timeoutMs, hostIp, HostServer.PORT, HostServer.PORT);
        connected = true;

        // Send handshake immediately
        client.sendTCP(new PacketHandshake(playerName, modVersion, ""));

        // Start periodic cursor updates
        cursorExecutor.scheduleAtFixedRate(this::sendCursorPosition,
                0, CURSOR_SEND_INTERVAL_MS, TimeUnit.MILLISECONDS);

        LOG.info("[GameClient] Connected to " + hostIp + ":" + HostServer.PORT);
    }

    /** Disconnects from the host. */
    public void disconnect() {
        if (!connected)
            return;
        connected = false;
        cursorExecutor.shutdownNow();
        client.stop();
        LOG.info("[GameClient] Disconnected.");
    }

    /**
     * Sends a player input packet to the host over TCP (reliable delivery).
     * Called from the game-side code whenever the local player performs an action.
     */
    public void sendInput(PacketPlayerInput input) {
        if (!connected)
            return;
        client.sendTCP(input);
    }

    private boolean joinFinishedSent = false;

    /**
     * Called by the local game client once the world view is fully initialized and
     * ready.
     */
    public void markJoinFinished() {
        if (!connected || joinFinishedSent)
            return;
        joinFinishedSent = true;
        LOG.info("[GameClient] Sending PacketJoinFinished to Host.");
        client.sendTCP(new com.ceke.multiplayer.core.server.network.packets.PacketJoinFinished());
    }

    /**
     * Called periodically in the render/update loop by ModLoader to know when to
     * trigger markJoinFinished.
     */
    public void checkJoinFinished() {
        if (!connected || joinFinishedSent)
            return;

        // Wait until GAME state is constructed and updating
        if (game.GAME.s() != null && game.GAME.updateI() > 5) {
            markJoinFinished();
        }
    }

    /** Sends a chat message to the host. */
    public void sendChat(String message) {
        if (!connected)
            return;
        client.sendTCP(new PacketChat(playerName, message));
    }

    // -----------------------------------------------------------------------
    // Internal
    // -----------------------------------------------------------------------

    /** Periodic task: reads the local cursor and sends it over UDP. */
    private void sendCursorPosition() {
        if (!connected)
            return;
        try {
            PacketMousePos pos = CursorSyncManager.getLocalCursorPacket();
            client.sendTCP(pos);
        } catch (Exception e) {
            LOG.log(Level.FINE, "[GameClient] Cursor send failed", e);
        }
    }

    private void registerListeners() {
        client.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                LOG.info("[GameClient] Successfully connected to host.");
            }

            @Override
            public void disconnected(Connection connection) {
                connected = false;
                CursorSyncManager.setRemoteCursor(0f, 0f);
                LOG.info("[GameClient] Lost connection to host.");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacketHandshake hs) {
                    LOG.info("[GameClient] Received handshake back from host. Active mod: " + hs.activeMod);
                    if (hs.activeMod != null && hs.activeMod.equals("Co-op")) {
                        com.ceke.multiplayer.core.server.ModLoader
                                .setActiveMod(new com.ceke.multiplayer.core.client.gamemods.coop.CoopGameMod());
                        LOG.info("[GameClient] Initialized " + hs.activeMod + " mod from host's handshake.");
                    }
                }

                if (object instanceof PacketWorldLoad pwl) {
                    if (pwl.saveZipBytes == null || pwl.saveZipBytes.length == 0) {
                        LOG.info("[GameClient] Host started a new game. Loading empty world.");
                        menu.ScMainBridge.clientLoadSave("");
                    } else {
                        LOG.info("[GameClient] Received world save zip (" + pwl.saveZipBytes.length + " bytes).");
                        try {
                            String appdata = System.getenv("APPDATA");
                            java.nio.file.Path targetFile = java.nio.file.Paths.get(appdata, "songsofsyx", "saves",
                                    "saves", "MP_Downloaded.save");
                            com.ceke.multiplayer.core.server.sync.SaveSyncManager.unzipSaveFile(pwl.saveZipBytes, targetFile);
                            LOG.info("[GameClient] Save unzipped. Triggering game load...");
                            menu.ScMainBridge.clientLoadSave("MP_Downloaded");
                        } catch (java.io.IOException e) {
                            LOG.log(Level.SEVERE, "[GameClient] Failed to load downloaded save", e);
                        }
                    }
                }

                if (object instanceof com.ceke.multiplayer.core.server.network.packets.PacketJoinStarted p) {
                    LOG.info("[GameClient] Server reports player joining: " + p.joiningPlayerName);
                    com.ceke.multiplayer.core.client.ui.JoinOverlayManager.activate(p.joiningPlayerName);
                }

                if (object instanceof com.ceke.multiplayer.core.server.network.packets.PacketJoinProgress p) {
                    com.ceke.multiplayer.core.client.ui.JoinOverlayManager.updateProgress(p.statusText, p.progressPercent);
                }

                if (object instanceof com.ceke.multiplayer.core.server.network.packets.PacketOverlayClear) {
                    LOG.info("[GameClient] Server cleared join overlay.");
                    com.ceke.multiplayer.core.client.ui.JoinOverlayManager.deactivate();
                }

                // Temporary logging of input loopback (to verify it works)
                if (object instanceof PacketPlayerInput) {
                    // This means the host echoed an input to us, or we are the host.
                }

                if (object instanceof PacketGameState state) {
                    // TODO: deserialize state.data and reconcile with local game
                    LOG.fine("[GameClient] Received tick #" + state.tick);
                }

                if (object instanceof PacketMousePos mp) {
                    // Host sent its cursor — render it on our screen
                    CursorSyncManager.setRemoteCursor(mp.x, mp.y);
                    LOG.fine("[GameClient] Received Host Cursor: x=" + mp.x + ", y=" + mp.y);
                }

                if (object instanceof PacketChat chat) {
                    LOG.info("[GameClient] Chat from '" + chat.senderName + "': " + chat.message);
                    // TODO: display in in-game chat overlay
                }
            }
        });
    }

    public boolean isConnected() {
        return connected;
    }
}
