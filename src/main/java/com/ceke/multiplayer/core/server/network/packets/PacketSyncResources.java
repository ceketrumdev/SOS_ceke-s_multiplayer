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
/*   JoinGameDialog.java                                                    */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Sent by the host to synchronize the exact resource quantities to clients.
 * This covers the internal arrays for the stockpiles, haulers, exports, and
 * imports.
 */
public class PacketSyncResources {
    public int[] stockpileAms;
    public int[] stockpileReservedAms;
    public int[] haulerAms;
    public int[] haulerReservedAms;
    public int[] haulerSpace;
    public int[] haulerSpaceReserved;
    public int[] exportAms;
    public int[] exportPromised;
    public int[] importAms;

    public PacketSyncResources() {
    }
}
