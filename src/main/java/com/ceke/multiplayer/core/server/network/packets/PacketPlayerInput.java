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
/*   PacketPlayerInput.java                                                 */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Represents a player action / input sent from the client to the master server.
 *
 * The server is authoritative: it receives these inputs, validates them,
 * applies them to the game state, and broadcasts the result to all clients.
 */
public class PacketPlayerInput {

    /** Type of input action. Keep this a byte/int for efficient serialization. */
    public byte type;

    /** World tile coordinates targeted by this input (if applicable). */
    public int tileX;
    public int tileY;

    /** Extra integer payload (e.g. building ID, unit ID). */
    public int payload;

    // --- Input types ---
    /** Left-click on a tile. */
    public static final byte TYPE_CLICK_TILE = 1;
    /** Right-click / cancel. */
    public static final byte TYPE_RIGHT_CLICK = 2;
    /** Key press — payload holds the key code. */
    public static final byte TYPE_KEY_PRESS = 3;

    public PacketPlayerInput() {
    }

    public PacketPlayerInput(byte type, int tileX, int tileY, int payload) {
        this.type = type;
        this.tileX = tileX;
        this.tileY = tileY;
        this.payload = payload;
    }
}
