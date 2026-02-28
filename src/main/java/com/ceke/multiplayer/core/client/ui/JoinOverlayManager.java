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
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import game.GAME;
import init.sprite.UI.UI;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.DIR;
import snake2d.util.gui.renderable.RENDEROBJ;
import util.gui.misc.GText;

/**
 * Renders a semi-transparent overlay and prevents interaction when a new player
 * is joining.
 */
public class JoinOverlayManager {

    private static boolean isOverlayActive = false;
    private static String currentStatus = "Waiting for player...";
    private static float currentProgress = 0f;

    public static void activate(String playerName) {
        isOverlayActive = true;
        currentStatus = playerName + " is joining...";
        currentProgress = 0f;
        // Pause the game exactly when the overlay activates
        if (GAME.SPEED != null) {
            GAME.SPEED.speedSet(0);
        }
    }

    public static void updateProgress(String status, float progress) {
        currentStatus = status;
        currentProgress = progress;
    }

    public static void deactivate() {
        isOverlayActive = false;
    }

    public static boolean isActive() {
        return isOverlayActive;
    }

    /**
     * Should be called in the main render loop to draw the overlay if active.
     */
    public static void render(snake2d.Renderer r, float ds) {
        if (!isOverlayActive)
            return;

        // Force pause while overlay is active
        if (game.GAME.SPEED != null && game.GAME.SPEED.speedTarget() > 0) {
            game.GAME.SPEED.speedSet(0);
        }

        // Darken the whole screen
        int w = snake2d.Engine.viewWidth();
        int h = snake2d.Engine.viewHeight();

        // Draw a semi-transparent black rectangle over the whole screen
        r.setOpacity(snake2d.util.color.OPACITY.O50);
        COLOR.BLACK.render(r, 0, w, 0, h, 0);
        r.setNormalOpacity();

        GText text = new GText(UI.FONT().H1, currentStatus);
        text.color(COLOR.WHITE);

        int textX = w / 2 - text.width() / 2;
        int textY = h / 2 - text.height() / 2;

        text.render(r, textX, textY);

        // Progress text
        String pct = String.format("%.0f%%", currentProgress * 100f);
        GText pText = new GText(UI.FONT().H2, pct);
        pText.color(COLOR.WHITE);
        pText.render(r, w / 2 - pText.width() / 2, textY + text.height() + 10);
    }
}
