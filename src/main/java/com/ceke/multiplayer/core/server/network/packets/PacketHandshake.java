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
/*   PacketHandshake.java                                                   */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Handshake packet — first thing sent by the client upon connecting.
 * The host validates it; mismatches (e.g. wrong mod version) cause rejection.
 */
public class PacketHandshake {
    public String playerName;
    public String modVersion;
    public String activeMod;

    public PacketHandshake() {
    }

    public PacketHandshake(String playerName, String modVersion, String activeMod) {
        this.playerName = playerName;
        this.modVersion = modVersion;
        this.activeMod = activeMod;
    }
}
