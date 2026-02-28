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
/*   MainMenuInjector.java                                                  */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import menu.MultiplayerMenuBridge;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import view.main.VIEW;
import com.ceke.multiplayer.core.server.network.GameClient;
import com.ceke.multiplayer.core.server.network.HostServer;

import java.lang.reflect.Field;
import java.util.logging.Logger;

/**
 * Injects a "Multiplayer" button into the game's main menu nav section.
 *
 * Since menu.GUI is package-private we use MultiplayerMenuBridge (which lives
 * in
 * package menu) to create styled buttons.
 *
 * The game's main menu is built by menu.ScMain which puts its nav GuiSection in
 * a field called "first". We locate it via reflection on the Menu object held
 * by VIEW.
 */
public final class MainMenuInjector {

    private static final Logger LOG = Logger.getLogger(MainMenuInjector.class.getName());

    private MainMenuInjector() {
    }

    public static void inject(MultiplayerMenu multiMenu) {
        try {
            CLICKABLE mpBtn = MultiplayerMenuBridge.navButton("Multiplayer");
            mpBtn.clickActionSet(() -> multiMenu.setVisible(true));

            GuiSection navSection = findNavSection();
            if (navSection != null) {
                navSection.addDown(8, mpBtn);
                LOG.info("[MainMenuInjector] Multiplayer button injected.");
            } else {
                LOG.warning("[MainMenuInjector] Nav section not found — button not injected.");
            }
        } catch (Exception e) {
            LOG.severe("[MainMenuInjector] Injection failed: " + e.getMessage());
        }
    }

    /**
     * Locates the main menu's nav GuiSection by traversing VIEW → Menu →
     * ScMain.first.
     * Returns null if any step fails (graceful degradation).
     */
    private static GuiSection findNavSection() {
        try {
            // VIEW stores the menu state via a field or method called "menu"
            Object viewObj = VIEW.class;
            Object menuObj = null;

            // Try VIEW.menu() method first
            try {
                menuObj = VIEW.class.getMethod("menu").invoke(null);
            } catch (Exception ignored) {
            }

            // Fall back: scan VIEW's static fields for a 'menu'-typed reference
            if (menuObj == null) {
                for (Field f : VIEW.class.getDeclaredFields()) {
                    f.setAccessible(true);
                    Object val = f.get(null);
                    if (val != null && val.getClass().getName().contains("Menu")) {
                        menuObj = val;
                        break;
                    }
                }
            }
            if (menuObj == null)
                return null;

            // ScMain is stored in the Menu object; its 'first' field is the nav GuiSection
            String[] subFields = { "first", "main", "current", "nav", "play" };
            for (Field f : menuObj.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object val = f.get(menuObj);
                if (val instanceof GuiSection) {
                    return (GuiSection) val;
                }
            }
        } catch (Exception e) {
            LOG.warning("[MainMenuInjector] Reflection failed: " + e.getMessage());
        }
        return null;
    }
}
