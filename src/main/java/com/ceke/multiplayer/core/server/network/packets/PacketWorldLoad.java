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
/*   PacketWorldLoad.java                                                   */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Sent by the host to the client immediately after a valid handshake.
 * Contains the compressed (zip) byte array of the host's current save folder.
 * This guarantees the client starts with the exact same world state.
 */
public class PacketWorldLoad {

    /** The zipped contents of the host's save directory. */
    public byte[] saveZipBytes;

    /** KryoNet requires a no-arg constructor. */
    public PacketWorldLoad() {
    }

    public PacketWorldLoad(byte[] saveZipBytes) {
        this.saveZipBytes = saveZipBytes;
    }
}
