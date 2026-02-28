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
/*   GameRule.java                                                          */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.rules;

import snake2d.MButt;
import snake2d.Renderer;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.file.FileGetter;
import snake2d.util.file.FilePutter;
import util.gui.misc.GBox;
import view.keyboard.KEYS;

/**
 * Represents a specific rule or logic component within a GameMod.
 * Rules are called by the ModLoader in the same phases as the main
 * SCRIPT_INSTANCE.
 */
public interface GameRule {

    default void update(double ds) {
    }

    default void hover(COORDINATE mCoo, boolean mouseHasMoved) {
    }

    default void render(Renderer r, float ds) {
    }

    default void mouseClick(MButt button) {
    }

    default void keyPush(KEYS key) {
    }

    default void hoverTimer(double mouseTimer, GBox text) {
    }

    default void save(FilePutter file) {
    }

    default void load(FileGetter file) {
    }
}
