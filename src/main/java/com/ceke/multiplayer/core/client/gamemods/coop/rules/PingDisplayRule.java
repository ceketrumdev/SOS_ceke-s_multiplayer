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
import com.ceke.multiplayer.core.server.rules.GameRule;
import snake2d.SPRITE_RENDERER;
import snake2d.Renderer;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;
import init.sprite.UI.UI;
import view.main.VIEW;
import java.lang.reflect.Field;
import java.util.logging.Logger;

public class PingDisplayRule implements GameRule {
    private static final Logger LOG = Logger.getLogger(PingDisplayRule.class.getName());

    private GuiSection rightPanel = null;
    private final GText text;
    private int frameCounter = 0;
    private boolean searchFailed = false;

    public PingDisplayRule() {
        text = new GText(UI.FONT().S, "Ping: --- ms");
    }

    @Override
    public void update(double ds) {
        if (rightPanel != null || searchFailed || VIEW.world() == null || VIEW.world().uiManager == null)
            return;

        try {
            Field intersField = null;
            Class<?> clazz = VIEW.world().uiManager.getClass();
            while (clazz != null && clazz != Object.class) {
                try {
                    intersField = clazz.getDeclaredField("inters");
                    break;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }

            if (intersField == null)
                return;
            intersField.setAccessible(true);
            Iterable<?> inters = (Iterable<?>) intersField.get(VIEW.world().uiManager);

            Object uiPanelTop = null;
            if (inters != null) {
                for (Object obj : inters) {
                    if (obj != null && obj.getClass().getSimpleName().equals("UIPanelTop")) {
                        uiPanelTop = obj;
                        break;
                    }
                }
            }

            if (uiPanelTop != null) {
                Field rightField = null;
                Class<?> rClazz = uiPanelTop.getClass();
                while (rClazz != null && rClazz != Object.class) {
                    try {
                        rightField = rClazz.getDeclaredField("right");
                        break;
                    } catch (NoSuchFieldException e) {
                        rClazz = rClazz.getSuperclass();
                    }
                }

                if (rightField != null) {
                    rightField.setAccessible(true);
                    rightPanel = (GuiSection) rightField.get(uiPanelTop);
                    LOG.info("[PingDisplayRule] Found UIPanelTop bounds for Ping rendering");
                } else {
                    LOG.warning("[PingDisplayRule] Found UIPanelTop but no 'right' field.");
                    searchFailed = true;
                }
            }
        } catch (Exception e) {
            LOG.warning("[PingDisplayRule] Failed to find UI components: " + e.getMessage());
            searchFailed = true;
        }
    }

    @Override
    public void render(Renderer renderer, float ds) {
        if (rightPanel == null || VIEW.world() == null)
            return;

        frameCounter++;
        if (frameCounter >= 60) {
            frameCounter = 0;
            MultiplayerSession session = MultiplayerSession.instance();
            if (session.isClient() && session.getGameClient() != null && session.getGameClient().isConnected()) {
                int ping = session.getGameClient().getPing();
                text.clear().add("Ping: ").add(ping).add("ms");
            } else {
                text.clear().add("Ping: --- ms");
            }
        }

        SPRITE_RENDERER r = (SPRITE_RENDERER) renderer;
        // Position it well to the left of the 'rightPanel', around where the 'left'
        // panel typically ends or just floating between them
        int x = rightPanel.body().x1() - text.width() - 170;
        int y = rightPanel.body().y1() + (rightPanel.body().height() - text.height()) / 2 + 5;

        text.render(r, x, y);
    }
}
