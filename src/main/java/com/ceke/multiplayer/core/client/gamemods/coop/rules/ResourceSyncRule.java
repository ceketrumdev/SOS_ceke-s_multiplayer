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

package com.ceke.multiplayer.core.client.gamemods.coop.rules;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import com.ceke.multiplayer.core.server.network.packets.PacketSyncResources;
import com.ceke.multiplayer.core.server.rules.GameRule;
import settlement.main.SETT;
import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Periodically reads the exact resource amounts from the host's stockpiles,
 * haulers, exports, and imports, then broadcasts them to clients to overwrite
 * their local arrays. This ensures the UI perfectly tracks the host's
 * resources.
 */
public class ResourceSyncRule implements GameRule {
    private static final Logger LOG = Logger.getLogger(ResourceSyncRule.class.getName());

    private double timer = 0.0;

    @Override
    public void update(double ds) {
        MultiplayerSession session = MultiplayerSession.instance();
        if (!session.isActive() || !session.isHost() || session.getHostServer() == null)
            return;

        if (SETT.ROOMS() == null || SETT.ROOMS().STOCKPILE == null)
            return;

        // Sync approximately every 1 game-second
        timer += ds;
        if (timer > 1.0) {
            timer = 0.0;
            sendSyncPacket(session);
        }
    }

    private void sendSyncPacket(MultiplayerSession session) {
        PacketSyncResources pkt = new PacketSyncResources();
        try {
            // TallyData internal array is called "ams"
            pkt.stockpileAms = getArray(SETT.ROOMS().STOCKPILE.tally().amount, "ams");
            pkt.stockpileReservedAms = getArray(SETT.ROOMS().STOCKPILE.tally().amountReserved, "ams");

            pkt.haulerAms = getArray(SETT.ROOMS().HAULER.tally.amount, "ams");
            pkt.haulerReservedAms = getArray(SETT.ROOMS().HAULER.tally.amountReserved, "ams");

            // RMapInt internal array is called "data"
            pkt.exportAms = getArray(SETT.ROOMS().EXPORT.tally.amount, "data");
            pkt.importAms = getArray(SETT.ROOMS().IMPORT.tally.amount, "data");

            session.getHostServer().broadcastResourceSync(pkt);
        } catch (Exception e) {
            LOG.warning("[ResourceSyncRule] Failed to read resource arrays: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Called by the GameClient listener when a PacketSyncResources is received.
     */
    public static void applyRemoteResources(PacketSyncResources pkt) {
        if (SETT.ROOMS() == null || SETT.ROOMS().STOCKPILE == null)
            return;

        try {
            if (pkt.stockpileAms != null)
                setArray(SETT.ROOMS().STOCKPILE.tally().amount, "ams", pkt.stockpileAms);
            if (pkt.stockpileReservedAms != null)
                setArray(SETT.ROOMS().STOCKPILE.tally().amountReserved, "ams", pkt.stockpileReservedAms);

            if (pkt.haulerAms != null)
                setArray(SETT.ROOMS().HAULER.tally.amount, "ams", pkt.haulerAms);
            if (pkt.haulerReservedAms != null)
                setArray(SETT.ROOMS().HAULER.tally.amountReserved, "ams", pkt.haulerReservedAms);

            if (pkt.exportAms != null)
                setArray(SETT.ROOMS().EXPORT.tally.amount, "data", pkt.exportAms);
            if (pkt.importAms != null)
                setArray(SETT.ROOMS().IMPORT.tally.amount, "data", pkt.importAms);
        } catch (Exception e) {
            LOG.warning("[ResourceSyncRule] Failed to apply remote resources: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Helper to access private arrays via Reflection. */
    private static int[] getArray(Object target, String fieldName) throws Exception {
        if (target == null)
            return null;
        Field field = getFieldAcrossHierarchy(target.getClass(), fieldName);
        field.setAccessible(true);
        int[] original = (int[]) field.get(target);
        if (original != null) {
            return original.clone(); // Clone for safety against concurrent modification
        }
        return null;
    }

    /** Helper to overwrite private arrays via Reflection. */
    private static void setArray(Object target, String fieldName, int[] newData) throws Exception {
        if (target == null || newData == null)
            return;
        Field field = getFieldAcrossHierarchy(target.getClass(), fieldName);
        field.setAccessible(true);
        int[] localData = (int[]) field.get(target);

        if (localData != null && localData.length == newData.length) {
            System.arraycopy(newData, 0, localData, 0, newData.length);
        }
    }

    private static Field getFieldAcrossHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }
}
