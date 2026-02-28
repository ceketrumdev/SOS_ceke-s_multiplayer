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
/*   JoinOverlayManager.java                                                */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 18:55:21 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import game.GAME;
import init.sprite.UI.UI;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.color.OPACITY;
import snake2d.util.sprite.SPRITE;
import util.gui.misc.GBox;
import util.gui.misc.GText;

/**
 * Renders a semi-transparent overlay on the HOST while a new player is joining.
 *
 * <h3>Background</h3>
 * The full-screen background is drawn by rendering a single-pixel SPRITE
 * created from {@link COLOR#BLACK100} stretched to cover the whole screen.
 * This is reliable because we directly reference a named engine constant
 * rather than generating a palette colour by an unpredictable seed.
 *
 * <h3>Continue mode (HOST side)</h3>
 * The HOST overlay dismisses automatically (immediately) as soon as
 * {@link #deactivate()} is called – i.e. when {@code PacketJoinFinished}
 * arrives from the joining client. No host click is required.
 *
 * <h3>Continue mode (CLIENT / joining-player side)</h3>
 * The joining player has their own overlay (activated via
 * {@link #activateClientWaiting()}). It waits according to
 * {@link #continueMode}:
 * <ul>
 * <li>{@link ContinueMode#CLICK} – player clicks anywhere → sends
 * {@code PacketJoinFinished} to the host.</li>
 * <li>{@link ContinueMode#TIMER} – auto-sends after
 * {@link #autoDelaySecs} seconds.</li>
 * </ul>
 * This controls the {@code clientReadyCallback} that is set by
 * {@link com.ceke.multiplayer.core.server.network.GameClient}.
 */
public class JoinOverlayManager {

    // ── Continue mode ─────────────────────────────────────────────────────

    /**
     * Controls how the joining-player overlay dismisses on the CLIENT side.
     * Can be changed at runtime from an in-game settings panel.
     */
    public enum ContinueMode {
        /** Joining player must click to confirm they are ready (default). */
        CLICK,
        /** Auto-dismiss after {@link JoinOverlayManager#autoDelaySecs} seconds. */
        TIMER
    }

    /**
     * How the client-side overlay is dismissed. Default:
     * {@link ContinueMode#CLICK}.
     */
    public static ContinueMode continueMode = ContinueMode.CLICK;

    /**
     * Seconds before auto-dismiss in {@link ContinueMode#TIMER} mode.
     * Ignored in CLICK mode.
     */
    public static int autoDelaySecs = 0;

    // ── Callback set by GameClient when it wants to know the player is ready ─

    /**
     * Called when the client player confirms they are ready (click or timer).
     * GameClient sets this to {@code GameClient::markJoinFinished}.
     */
    private static Runnable clientReadyCallback = null;

    public static void setClientReadyCallback(Runnable callback) {
        clientReadyCallback = callback;
    }

    // ── Internal state ────────────────────────────────────────────────────

    /** HOST overlay: shown while a player is loading in. */
    private static boolean isHostOverlayActive = false;

    /**
     * CLIENT overlay: shown after the world loaded, waiting for the player
     * to click "Continue" before sending PacketJoinFinished.
     */
    private static boolean isClientOverlayActive = false;
    private static boolean clientWaitingForContinue = false; // CLICK mode
    private static double clientTimerRemaining = 0.0; // TIMER mode

    private static String currentStatus = "Waiting for player...";
    private static float currentProgress = 0f;

    // ── Lazy-initialised colour / sprites ────────────────────────────────

    /**
     * Full-screen background colour (dark, lazy-initialised).
     * Rendered directly via
     * {@link COLOR#render(SPRITE_RENDERER, int, int, int, int)}
     * — the same approach used in MouseSyncRule for the remote cursor crosshair.
     */
    private static COLOR bgColor = null;

    /** Accent-coloured fill for the progress bar. */
    private static SPRITE barSprite = null;

    private static void lazyInit() {
        if (bgColor == null) {
            // generateUnique(seed, count, bright=false) → dark palette colour
            bgColor = COLOR.generateUnique(0, 1, false)[0];
        }
        if (barSprite == null) {
            COLOR barColor = COLOR.generateUnique(777, 1, true)[0];
            barSprite = GBox.tmp.createColored(barColor);
        }
    }

    // ── HOST overlay API ──────────────────────────────────────────────────

    /**
     * Activates the HOST-side overlay (called from HostServer on handshake).
     */
    public static void activate(String playerName) {
        isHostOverlayActive = true;
        currentStatus = playerName + " is joining...";
        currentProgress = 0f;
        if (GAME.SPEED != null) {
            GAME.SPEED.speedSet(0);
        }
    }

    /** Progress update relayed from the joining client. */
    public static void updateProgress(String status, float progress) {
        currentStatus = status;
        currentProgress = progress;
    }

    /**
     * Dismisses the HOST-side overlay immediately.
     * Called when {@code PacketJoinFinished} is received from the joining client.
     * The host does NOT need to click anything.
     */
    public static void deactivate() {
        isHostOverlayActive = false;
        currentProgress = 0f;
    }

    /** @return true if the HOST overlay is currently shown. */
    public static boolean isActive() {
        return isHostOverlayActive || isClientOverlayActive || isReconnecting;
    }

    // ── CLIENT overlay API ────────────────────────────────────────────────

    /**
     * Called by GameClient when the world is loaded and the game is running,
     * to show the "Ready – click to continue" overlay on the joining client.
     *
     * Instead of sending PacketJoinFinished immediately, GameClient calls this
     * and registers a callback. The overlay waits for click/timer, then fires the
     * callback which sends PacketJoinFinished.
     */
    public static void activateClientWaiting() {
        isClientOverlayActive = true;
        currentStatus = "World loaded! Ready to play.";
        currentProgress = 1f;

        if (continueMode == ContinueMode.TIMER && autoDelaySecs > 0) {
            clientTimerRemaining = autoDelaySecs;
            clientWaitingForContinue = false;
        } else {
            // CLICK mode (or TIMER with 0s → treat as CLICK)
            clientWaitingForContinue = true;
            clientTimerRemaining = 0.0;
        }
    }

    /**
     * Called from ModLoader.mouseClick() when the overlay is active.
     * In CLICK mode on the client, confirms the player is ready.
     */
    public static void onClick() {
        if (isClientOverlayActive && clientWaitingForContinue) {
            fireClientReady();
        }
        // HOST overlay: host cannot dismiss their own overlay by clicking.
    }

    // ── CLIENT reconnect API ──────────────────────────────────────────────

    private static boolean isReconnecting = false;
    private static long reconnectEndTimeMillis = 0;

    public static void activateReconnecting() {
        isReconnecting = true;
        reconnectEndTimeMillis = System.currentTimeMillis() + 30000; // 30 seconds timeout
        currentStatus = "Connection lost! Waiting for server...";
        currentProgress = 0f;

        // Ensure game is paused
        if (game.GAME.SPEED != null) {
            game.GAME.SPEED.speedSet(0);
        }
    }

    public static void deactivateReconnecting() {
        isReconnecting = false;
    }

    public static boolean isReconnectingMode() {
        return isReconnecting;
    }

    // ── Internal helpers & Loop ───────────────────────────────────────────

    /**
     * Called from ModLoader.update(ds) every frame.
     * Drives the TIMER countdown on the client overlay,
     * and the reconnection fallback timeout.
     */
    public static void update(double ds) {
        if (isClientOverlayActive) {
            if (continueMode == ContinueMode.TIMER && clientTimerRemaining > 0.0) {
                clientTimerRemaining -= ds;
                if (clientTimerRemaining <= 0.0) {
                    fireClientReady();
                }
            }
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────

    private static void fireClientReady() {
        isClientOverlayActive = false;
        clientWaitingForContinue = false;
        clientTimerRemaining = 0.0;
        if (clientReadyCallback != null) {
            clientReadyCallback.run();
            clientReadyCallback = null;
        }
    }

    // ── Rendering ─────────────────────────────────────────────────────────

    /**
     * Called from ModLoader.render() every frame.
     * Renders whichever overlay is currently active.
     */
    public static void render(snake2d.Renderer r, float ds) {
        boolean showHost = isHostOverlayActive;
        boolean showClient = isClientOverlayActive;
        boolean showReconnect = isReconnecting;

        if (!showHost && !showClient && !showReconnect)
            return;

        if (showReconnect) {
            long remainingMillis = reconnectEndTimeMillis - System.currentTimeMillis();
            if (remainingMillis <= 0) {
                isReconnecting = false;
                com.ceke.multiplayer.core.server.MultiplayerSession.instance().stopAll();
                menu.ScMainBridge.returnToMenu();
                return;
            }
            float remainingSecs = remainingMillis / 1000f;
            currentProgress = 1f - (remainingSecs / 30f);
            currentStatus = "Connection lost! Waiting for server... " + (int) Math.ceil(remainingSecs) + "s";
        }

        // Keep the game paused while the host overlay is displayed
        if (showHost && game.GAME.SPEED != null && game.GAME.SPEED.speedTarget() > 0) {
            game.GAME.SPEED.speedSet(0);
        }

        int w = init.constant.C.DIM().x2();
        int h = init.constant.C.DIM().y2();

        SPRITE_RENDERER sr = (SPRITE_RENDERER) r;
        lazyInit();

        // ── Solid dark background ─────────────────────────────────────────
        // Use COLOR.render(sr, x1, x2, y1, y2) directly — the same approach
        // used in MouseSyncRule for cursor colours. This always produces a
        // solid filled rectangle without needing a SPRITE wrapper.
        // COLOR.unbind() before/after is required by the engine to flush state.
        COLOR.unbind();
        r.setOpacity(OPACITY.O75);
        bgColor.render(sr, 0, w, 0, h);
        r.setNormalOpacity();
        COLOR.unbind();

        // ── Status text (centered) ────────────────────────────────────────
        GText text = new GText(UI.FONT().H1, currentStatus);
        int textX = w / 2 - text.width() / 2;
        int textY = h / 2 - text.height() / 2 - 20;
        text.render(r, textX, textY);

        // ── Progress bar ──────────────────────────────────────────────────
        int barW = 300;
        int barH = 8;
        int barX = w / 2 - barW / 2;
        int barY = textY + text.height() + 12;
        int fillW = currentProgress > 0f
                ? (int) (barW * Math.min(1f, currentProgress))
                : 0;

        // Border
        GBox.tmp.render(sr, barX - 1, barY - 1, barX + barW + 1, barY + barH + 1);

        // Fill
        if (fillW > 0 && barSprite != null) {
            r.setOpacity(OPACITY.O100);
            barSprite.render(sr, barX, barY, barX + fillW, barY + barH);
            r.setNormalOpacity();
        }

        // ── Percentage label ──────────────────────────────────────────────
        String pct = String.format("%.0f%%", currentProgress * 100f);
        GText pText = new GText(UI.FONT().H2, pct);
        int pY = barY + barH + 8;
        pText.render(r, w / 2 - pText.width() / 2, pY);

        // ── Client footer (click/timer prompt) ────────────────────────────
        if (showClient) {
            String footer;
            if (clientWaitingForContinue) {
                footer = "Click anywhere to continue";
            } else {
                int secsLeft = (int) Math.ceil(clientTimerRemaining);
                footer = "Starting in " + secsLeft + "s...";
            }
            GText fText = new GText(UI.FONT().H2, footer);
            fText.render(r, w / 2 - fText.width() / 2, pY + pText.height() + 16);
        }
    }
}
