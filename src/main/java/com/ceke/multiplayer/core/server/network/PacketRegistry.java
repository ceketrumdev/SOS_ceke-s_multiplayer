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
/*   PacketRegistry.java                                                    */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server.network;

import com.ceke.multiplayer.core.server.network.packets.PacketChat;
import com.ceke.multiplayer.core.server.network.packets.PacketGameState;
import com.ceke.multiplayer.core.server.network.packets.PacketHandshake;
import com.ceke.multiplayer.core.server.network.packets.PacketMousePos;
import com.ceke.multiplayer.core.server.network.packets.PacketPlayerInput;
import com.ceke.multiplayer.core.server.network.packets.PacketWorldLoad;
import com.ceke.multiplayer.core.server.network.packets.PacketJoinStarted;
import com.ceke.multiplayer.core.server.network.packets.PacketJoinProgress;
import com.ceke.multiplayer.core.server.network.packets.PacketJoinFinished;
import com.ceke.multiplayer.core.server.network.packets.PacketOverlayClear;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * All packet types that can be sent over the network.
 * KryoNet requires all classes to be registered before use.
 */
public final class PacketRegistry {

    private PacketRegistry() {
    }

    /** Registers all packet classes with a KryoNet endpoint (Client or Server). */
    public static void register(com.esotericsoftware.kryonet.EndPoint endPoint) {
        var kryo = endPoint.getKryo();

        // ── Java primitive/array types used inside packets ─────────────────
        kryo.register(byte[].class);
        kryo.register(int[].class);
        kryo.register(float[].class);
        kryo.register(java.util.ArrayList.class);

        // ── Packet types ───────────────────────────────────────────────────
        kryo.register(PacketMousePos.class);
        kryo.register(PacketPlayerInput.class);
        kryo.register(PacketGameState.class);
        kryo.register(PacketChat.class);
        kryo.register(PacketHandshake.class);
        kryo.register(PacketWorldLoad.class);

        // Join Overlay sync
        kryo.register(PacketJoinStarted.class);
        kryo.register(PacketJoinProgress.class);
        kryo.register(PacketJoinFinished.class);
        kryo.register(PacketOverlayClear.class);
    }
}
