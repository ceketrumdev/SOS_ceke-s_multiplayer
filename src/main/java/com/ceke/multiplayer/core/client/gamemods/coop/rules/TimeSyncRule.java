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
/*   TimeSyncRule.java                                                      */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 19:08:26 by ceketrum                               */
/*   Updated: 2026/02/28 19:08:26 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.gamemods.coop.rules;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import com.ceke.multiplayer.core.server.network.packets.PacketSpeedChange;
import com.ceke.multiplayer.core.server.rules.GameRule;

/**
 * Detects local game-speed changes and propagates them to all other players.
 *
 * <h3>Flow</h3>
 * <ul>
 * <li><b>HOST</b>: detects a local speed change → broadcasts
 * {@link PacketSpeedChange} to all connected clients.</li>
 * <li><b>CLIENT</b>: detects a local speed change → sends
 * {@link PacketSpeedChange} to the host, which validates it and
 * re-broadcasts to all other clients.</li>
 * </ul>
 *
 * <h3>Loop prevention</h3>
 * When a remote speed is applied locally (via
 * {@link #applyRemoteSpeed(int)}), {@link #lastKnownSpeed} is updated
 * immediately so the next {@link #update} call does not see a "new" change
 * and re-send the packet.
 */
public class TimeSyncRule implements GameRule {

    /**
     * Last speed value we sent or applied. Initialised to -1 so the first
     * observed speed is always broadcast (lets newly joining clients catch up).
     */
    private static volatile int lastKnownSpeed = -1;

    /**
     * Called by the network listener (host or client) when a remote
     * {@link PacketSpeedChange} is received. Applies the speed locally
     * and updates {@link #lastKnownSpeed} to prevent the change from being
     * re-sent on the next update tick.
     *
     * @param speed the speed value from the remote packet
     */
    public static void applyRemoteSpeed(int speed) {
        if (game.GAME.SPEED == null)
            return;
        lastKnownSpeed = speed;
        game.GAME.SPEED.speedSet(speed);
    }

    // ── GameRule ────────────────────────────────────────────────────────────

    @Override
    public void update(double ds) {
        if (game.GAME.SPEED == null || game.GAME.s() == null)
            return;

        int currentSpeed = (int) game.GAME.SPEED.speedTarget();

        // Nothing changed — nothing to send
        if (currentSpeed == lastKnownSpeed)
            return;

        lastKnownSpeed = currentSpeed;

        MultiplayerSession session = MultiplayerSession.instance();
        if (!session.isActive())
            return;

        PacketSpeedChange pkt = new PacketSpeedChange(currentSpeed);

        if (session.isHost() && session.getHostServer() != null) {
            // Host changed speed → push to all clients
            session.getHostServer().broadcastSpeedChange(pkt);
        } else if (session.isClient() && session.getGameClient() != null
                && session.getGameClient().isConnected()) {
            // Client changed speed → send to host for relay
            session.getGameClient().sendSpeedChange(pkt);
        }
    }
}
