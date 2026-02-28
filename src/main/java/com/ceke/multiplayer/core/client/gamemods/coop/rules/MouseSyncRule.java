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
/*   MouseSyncRule.java                                                     */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.gamemods.coop.rules;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import com.ceke.multiplayer.core.server.rules.GameRule;
import com.ceke.multiplayer.core.server.sync.CursorSyncManager;
import snake2d.Renderer;
import snake2d.SPRITE_RENDERER;
import snake2d.util.color.COLOR;
import snake2d.util.datatypes.COORDINATE;
import view.main.VIEW;
import view.subview.GameWindow;

/**
 * Handles synchronizing and rendering the remote player's mouse cursor.
 */
public class MouseSyncRule implements GameRule {

    @Override
    public void hover(COORDINATE mCoo, boolean mouseHasMoved) {
        if (!MultiplayerSession.instance().isActive())
            return;
        try {
            GameWindow gw = VIEW.s().getWindow();
            float worldX = (float) gw.pixel().x();
            float worldY = (float) gw.pixel().y();
            CursorSyncManager.updateLocalCursor(worldX, worldY);
        } catch (Exception e) {
            // Outside map area or view not ready
        }
    }

    @Override
    public void render(Renderer r, float ds) {
        if (!MultiplayerSession.instance().isActive())
            return;

        float rx = CursorSyncManager.getRemoteX();
        float ry = CursorSyncManager.getRemoteY();
        if (rx < 0 || ry < 0)
            return; // no data yet

        try {
            GameWindow gw = VIEW.s().getWindow();

            int camX1 = gw.pixels().x1();
            int camY1 = gw.pixels().y1();
            int camX2 = gw.pixels().x2();
            int camY2 = gw.pixels().y2();

            int viewX1 = gw.view().x1();
            int viewY1 = gw.view().y1();
            int viewX2 = gw.view().x2();
            int viewY2 = gw.view().y2();

            if (viewX2 - viewX1 == 0 || viewY2 - viewY1 == 0)
                return;

            double scaleX = (double) (camX2 - camX1) / (double) (viewX2 - viewX1);
            double scaleY = (double) (camY2 - camY1) / (double) (viewY2 - viewY1);

            int screenX = (int) (((rx - camX1) / scaleX) + viewX1);
            int screenY = (int) (((ry - camY1) / scaleY) + viewY1);

            if (screenX < -128 || screenX > 4096 || screenY < -128 || screenY > 3072)
                return;

            SPRITE_RENDERER sr = (SPRITE_RENDERER) r;
            COLOR.unbind();

            int h = 10;
            int t = 1;

            COLOR.WHITE25.render(sr, screenX - h - 1, screenX + h + 1, screenY - t - 1, screenY + t + 1);
            COLOR.WHITE25.render(sr, screenX - t - 1, screenX + t + 1, screenY - h - 1, screenY + h + 1);

            COLOR.RED100.render(sr, screenX - h, screenX + h, screenY - t, screenY + t);
            COLOR.RED100.render(sr, screenX - t, screenX + t, screenY - h, screenY + h);
            COLOR.unbind();
        } catch (ClassCastException cc) {
        } catch (Throwable t) {
        }
    }
}
