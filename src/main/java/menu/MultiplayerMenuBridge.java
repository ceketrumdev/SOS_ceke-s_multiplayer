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
/*   MultiplayerMenuBridge.java                                             */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package menu;

import snake2d.util.gui.clickable.CLICKABLE;

/**
 * Bridge class in the 'menu' package so it can access the package-private
 * menu.GUI class.
 *
 * Songs of Syx's menu.GUI is declared without 'public', making it
 * package-private.
 * This bridge exposes only the parts we need from our mod's
 * com.ceke.multiplayer.ui classes.
 */
public final class MultiplayerMenuBridge {

    private MultiplayerMenuBridge() {
    }

    /**
     * Creates a standard nav-button using the game's menu style.
     */
    public static CLICKABLE navButton(String text) {
        return GUI.getNavButt(text);
    }
}
