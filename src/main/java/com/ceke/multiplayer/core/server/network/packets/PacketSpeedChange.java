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
/*   PacketSpeedChange.java                                                 */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 19:08:26 by ceketrum                               */
/*   Updated: 2026/02/28 19:08:26 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Sent by any player when they change the game speed.
 *
 * Flow:
 * CLIENT changes speed → sends this to HOST
 * HOST validates → applies locally → re-broadcasts to all OTHER clients
 * HOST changes speed → broadcasts directly to all clients
 *
 * {@code speed} maps to {@code game.GAME.SPEED.speedTarget()} values:
 * 0 = paused, 1 = normal, 2 = fast, 3 = fastest
 */
public class PacketSpeedChange {

    /** Target game speed (0–3). */
    public int speed;

    /** KryoNet requires a no-arg constructor. */
    public PacketSpeedChange() {
    }

    public PacketSpeedChange(int speed) {
        this.speed = speed;
    }
}
