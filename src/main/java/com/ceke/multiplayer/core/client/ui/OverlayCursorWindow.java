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
/*   OverlayCursorWindow.java                                               */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

/**
 * No longer used.
 * The remote cursor is rendered directly by MultiplayerMod.render() via
 * snake2d.
 * This class is kept as an empty stub to avoid compilation errors if referenced
 * elsewhere.
 */
public final class OverlayCursorWindow {
    private OverlayCursorWindow() {
    }

    public static void start() {
        /* no-op */ }

    public static void stop() {
        /* no-op */ }

    public static void requestRepaint() {
        /* no-op */ }

    public static void setPlayerLabel(String label) {
        /* no-op */ }
}
