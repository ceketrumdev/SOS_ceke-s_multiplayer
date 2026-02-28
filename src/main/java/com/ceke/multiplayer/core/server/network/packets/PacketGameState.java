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
/*   PacketGameState.java                                                   */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Sent from the host to clients every server tick.
 * Contains a compressed snapshot of the world state that clients need to
 * reconcile with.
 *
 * NOTE: Initially this is a stub. Full delta-compression of the game state is
 * complex
 * and will be built incrementally. Start with tick number and future fields.
 */
public class PacketGameState {

    /**
     * Monotonically increasing tick counter. Clients use this to detect missed
     * ticks.
     */
    public long tick;

    /**
     * Serialized game state delta.
     * For now left as a byte array placeholder; the sync system will fill this.
     */
    public byte[] data;

    public PacketGameState() {
    }

    public PacketGameState(long tick, byte[] data) {
        this.tick = tick;
        this.data = data;
    }
}
