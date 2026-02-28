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
/*   MultiplayerMod.java                                                    */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import com.ceke.multiplayer.core.server.sync.CursorSyncManager;
import com.ceke.multiplayer.core.client.ui.MultiplayerMenu;
import com.ceke.multiplayer.core.server.MultiplayerSession;
import script.SCRIPT;
import snake2d.MButt;
import snake2d.Renderer;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.gui.misc.GBox;
import util.info.INFO;
import view.keyboard.KEYS;
import view.main.VIEW;
import view.subview.GameWindow;

import java.util.logging.Logger;

/**
 * Mod entry point for Ceke Multiplayer.
 */
public final class MultiplayerMod implements SCRIPT {

    private static final Logger LOG = Logger.getLogger(MultiplayerMod.class.getName());

    private final INFO info = new INFO("Ceke Multiplayer", "Adds co-op multiplayer to Songs of Syx.");

    @Override
    public CharSequence name() {
        return info.name;
    }

    @Override
    public CharSequence desc() {
        return info.desc;
    }

    @Override
    public boolean forceInit() {
        return true;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public SCRIPT_INSTANCE createInstance() {
        return new Instance();
    }

    private static MultiplayerMenu staticMenu = null;

    public static synchronized MultiplayerMenu getMenuInstance() {
        if (staticMenu == null) {
            try {
                staticMenu = new MultiplayerMenu();
            } catch (Exception e) {
                LOG.warning("[MultiplayerMod] Failed to create MultiplayerMenu: " + e);
            }
        }
        return staticMenu;
    }

    // -----------------------------------------------------------------------

    public static final class Instance implements SCRIPT_INSTANCE {

        public Instance() {
            // Par défaut, charger le mode Co-op
            com.ceke.multiplayer.core.server.ModLoader.setActiveMod(new com.ceke.multiplayer.core.client.gamemods.coop.CoopGameMod());
        }

        @Override
        public void update(double ds) {
            com.ceke.multiplayer.core.server.ModLoader.update(ds);
        }

        @Override
        public void hover(COORDINATE mCoo, boolean mouseHasMoved) {
            com.ceke.multiplayer.core.server.ModLoader.hover(mCoo, mouseHasMoved);
        }

        @Override
        public void render(Renderer r, float ds) {
            com.ceke.multiplayer.core.server.ModLoader.render(r, ds);
        }

        @Override
        public void mouseClick(MButt button) {
            com.ceke.multiplayer.core.server.ModLoader.mouseClick(button);
        }

        @Override
        public void keyPush(KEYS key) {
            com.ceke.multiplayer.core.server.ModLoader.keyPush(key);
        }

        @Override
        public void hoverTimer(double mouseTimer, GBox text) {
            com.ceke.multiplayer.core.server.ModLoader.hoverTimer(mouseTimer, text);
        }

        @Override
        public void save(FilePutter file) {
            com.ceke.multiplayer.core.server.ModLoader.save(file);
        }

        @Override
        public void load(FileGetter file) {
            com.ceke.multiplayer.core.server.ModLoader.load(file);
        }
    }
}
