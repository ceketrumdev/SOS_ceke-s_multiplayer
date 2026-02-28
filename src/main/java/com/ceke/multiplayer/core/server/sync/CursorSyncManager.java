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
/*   CursorSyncManager.java                                                 */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.sync;

import com.ceke.multiplayer.core.server.network.packets.PacketMousePos;

/**
 * Manages cursor positions in precise WORLD PIXEL coordinates (sub-tile
 * accuracy).
 *
 * <p>
 * localX/Y = exact pixel coordinates of OUR cursor in the world (from
 * gw.mouse().x()/y())
 * <p>
 * remoteX/Y = exact pixel coordinates of the OTHER player's cursor (received
 * from network)
 *
 * <p>
 * All fields are volatile for thread-safety.
 */
public final class CursorSyncManager {

    private CursorSyncManager() {
    }

    // World pixel coordinates (precise float values)
    private static volatile float localX = -1f;
    private static volatile float localY = -1f;
    private static volatile float remoteX = -1f;
    private static volatile float remoteY = -1f;

    // -----------------------------------------------------------------------
    // Local cursor — updated each frame from hover()
    // -----------------------------------------------------------------------

    /**
     * Store the current hovered world pixel (called from SCRIPT_INSTANCE.hover()).
     */
    public static void updateLocalCursor(float worldX, float worldY) {
        localX = worldX;
        localY = worldY;
    }

    /**
     * @deprecated kept for binary compat — prefer updateLocalCursor(float,float)
     */
    @Deprecated
    public static void updateLocalCursorNormalized() {
        /* no-op */ }

    // -----------------------------------------------------------------------
    // Packet helpers
    // -----------------------------------------------------------------------

    /** Returns a packet with the current local world pixel position. */
    public static PacketMousePos getLocalCursorPacket() {
        return new PacketMousePos(localX, localY);
    }

    // -----------------------------------------------------------------------
    // Remote cursor — set from the network thread
    // -----------------------------------------------------------------------

    /** Called when a remote cursor packet arrives. Safe to call from any thread. */
    public static void setRemoteCursor(float worldX, float worldY) {
        remoteX = worldX;
        remoteY = worldY;
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public static float getRemoteX() {
        return remoteX;
    }

    public static float getRemoteY() {
        return remoteY;
    }

    public static float getLocalX() {
        return localX;
    }

    public static float getLocalY() {
        return localY;
    }
}
