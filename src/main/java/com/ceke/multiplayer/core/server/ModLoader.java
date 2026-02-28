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
/*   ModLoader.java                                                           */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                           */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                                 */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                                 */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server;

import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.gui.misc.GBox;
import view.keyboard.KEYS;
import com.ceke.multiplayer.core.server.rules.GameRule;

/**
 * Manages the currently active GameMod and delegates SCRIPT_INSTANCE calls to
 * its rules.
 */
public class ModLoader {

    private static GameMod activeMod;

    public static void setActiveMod(GameMod mod) {
        activeMod = mod;
    }

    public static GameMod getActiveMod() {
        return activeMod;
    }

    public static void update(double ds) {
        // Drive the join-overlay timer countdown (ContinueMode.TIMER)
        com.ceke.multiplayer.core.client.ui.JoinOverlayManager.update(ds);

        // If we are playing as a client, tell the host we finished loading on the first
        // few ticks
        MultiplayerSession session = MultiplayerSession.instance();
        if (session != null && session.getGameClient() != null && session.getGameClient().isConnected()) {
            session.getGameClient().checkJoinFinished();
        }

        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.update(ds);
        }
    }

    public static void hover(COORDINATE mCoo, boolean mouseHasMoved) {
        if (com.ceke.multiplayer.core.client.ui.JoinOverlayManager.isActive())
            return;
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.hover(mCoo, mouseHasMoved);
        }
    }

    public static void render(Renderer r, float ds) {
        if (com.ceke.multiplayer.core.client.ui.JoinOverlayManager.isActive()) {
            com.ceke.multiplayer.core.client.ui.JoinOverlayManager.render(r, ds);
        }

        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.render(r, ds);
        }
    }

    public static void mouseClick(MButt button) {
        // Forward click to the join overlay (dismiss in CLICK mode)
        if (com.ceke.multiplayer.core.client.ui.JoinOverlayManager.isActive()) {
            com.ceke.multiplayer.core.client.ui.JoinOverlayManager.onClick();
            return; // block game input while overlay is shown
        }
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.mouseClick(button);
        }
    }

    public static void keyPush(KEYS key) {
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.keyPush(key);
        }
    }

    public static void hoverTimer(double mouseTimer, GBox text) {
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.hoverTimer(mouseTimer, text);
        }
    }

    public static void save(FilePutter file) {
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.save(file);
        }
    }

    public static void load(FileGetter file) {
        if (activeMod == null)
            return;
        for (GameRule rule : activeMod.getRules()) {
            rule.load(file);
        }
    }
}
