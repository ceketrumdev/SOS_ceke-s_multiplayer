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
/*   GlobalCursorInjector.java                                              */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import com.ceke.multiplayer.core.server.sync.CursorSyncManager;
import com.ceke.multiplayer.core.server.network.GameClient;

/**
 * No longer used.
 * The remote cursor is now rendered directly by MultiplayerMod.render()
 * using the snake2d renderer and the game's tile/pixel projection APIs.
 */
public final class GlobalCursorInjector {
    private GlobalCursorInjector() {
    }

    public static void inject() {
        /* no-op */ }

    public static void dispose() {
        /* no-op */ }
}
