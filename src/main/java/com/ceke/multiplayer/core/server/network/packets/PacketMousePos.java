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
/*   PacketMousePos.java                                                    */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network.packets;

/**
 * Sent by both host and client to share the mouse cursor position.
 * Coordinates are precise WORLD PIXELS (not screen pixels, nor world tiles).
 */
public class PacketMousePos {
    public float x;
    public float y;

    public PacketMousePos() {
    }

    public PacketMousePos(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
