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
/*   MultiplayerSession.java                                                */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server;

import com.ceke.multiplayer.core.server.network.GameClient;
import com.ceke.multiplayer.core.server.network.HostServer;
import com.ceke.multiplayer.core.server.sync.CursorSyncManager;
import com.ceke.multiplayer.core.client.ui.GlobalCursorInjector;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Central session manager — tracks whether we are currently hosting or
 * connected as client,
 * and provides clean start/stop helpers used by the UI.
 *
 * Access via {@link #instance()}.
 */
public final class MultiplayerSession {

    private static final Logger LOG = Logger.getLogger(MultiplayerSession.class.getName());
    private static final MultiplayerSession INSTANCE = new MultiplayerSession();

    public enum Role {
        NONE, HOST, CLIENT
    }

    private Role role = Role.NONE;
    private HostServer hostServer;
    private GameClient gameClient;

    private String localPlayerName = "Player";

    private MultiplayerSession() {
    }

    public static MultiplayerSession instance() {
        return INSTANCE;
    }

    // -----------------------------------------------------------------------
    // Host
    // -----------------------------------------------------------------------

    /**
     * Starts hosting a new cooperative session.
     * Must be called after a save is loaded or a new game has been started.
     *
     * @param saveName the save that was loaded (tells the client what to load).
     *                 Pass empty string for a new-game host.
     */
    public void startHost(String saveName) {
        if (role != Role.NONE)
            stopAll();
        hostServer = new HostServer();
        hostServer.setSaveName(saveName);
        try {
            hostServer.start();
            role = Role.HOST;
            GlobalCursorInjector.inject(); // Start the AWT cursor overlay
            LOG.info("[MultiplayerSession] Now hosting on port " + HostServer.PORT);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "[MultiplayerSession] Failed to start host server", e);
            hostServer = null;
        }
    }

    /** @deprecated use {@link #startHost(String)} */
    @Deprecated
    public void startHost() {
        startHost("");
    }

    // -----------------------------------------------------------------------
    // Client / Join
    // -----------------------------------------------------------------------

    /**
     * Connects to an existing host session.
     *
     * @param hostIp IP address (or hostname) of the host machine.
     */
    public void joinGame(String hostIp) {
        if (role != Role.NONE)
            stopAll();
        gameClient = new GameClient(localPlayerName, "1.0.0");
        try {
            gameClient.connect(hostIp, 5000);
            role = Role.CLIENT;
            GlobalCursorInjector.inject(); // Start the AWT cursor overlay
            LOG.info("[MultiplayerSession] Joined " + hostIp);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "[MultiplayerSession] Failed to connect to " + hostIp, e);
            gameClient = null;
        }
    }

    // -----------------------------------------------------------------------
    // Stop
    // -----------------------------------------------------------------------

    /** Terminates any active session (host or client). */
    public void stopAll() {
        GlobalCursorInjector.dispose(); // Hide the AWT cursor overlay
        if (hostServer != null) {
            hostServer.stop();
            hostServer = null;
        }
        if (gameClient != null) {
            gameClient.disconnect();
            gameClient = null;
        }
        role = Role.NONE;
        LOG.info("[MultiplayerSession] Session ended.");
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public Role getRole() {
        return role;
    }

    public boolean isHost() {
        return role == Role.HOST;
    }

    public boolean isClient() {
        return role == Role.CLIENT;
    }

    public boolean isActive() {
        return role != Role.NONE;
    }

    public HostServer getHostServer() {
        return hostServer;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    public String getLocalPlayerName() {
        return localPlayerName;
    }

    public void setLocalPlayerName(String name) {
        this.localPlayerName = name;
    }
}
