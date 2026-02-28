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
/*   IpInputField.java                                                      */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import util.gui.misc.GText;
import init.sprite.UI.UI;

/**
 * A single-line text input field for typing the host IP address.
 * Keyboard input is handled by the parent SCRIPT_INSTANCE via keyPush.
 */
public final class IpInputField extends GuiSection {

    private static final int MAX_LENGTH = 64;
    private final StringBuilder buffer;
    private final GText displayText;

    public IpInputField(StringBuilder buffer, int width, int height) {
        this.buffer = buffer;
        body().setDim(width, height);
        this.displayText = new GText(UI.FONT().H2, MAX_LENGTH + 2);
    }

    /** Called by the owning JoinGameDialog when a key is pressed. */
    public boolean onKey(char key, int code) {
        if (code == java.awt.event.KeyEvent.VK_BACK_SPACE) {
            if (buffer.length() > 0)
                buffer.deleteCharAt(buffer.length() - 1);
            return true;
        }
        if (key >= 32 && key < 127 && buffer.length() < MAX_LENGTH) {
            buffer.append(key);
            return true;
        }
        return false;
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        // Render typed text with blinking cursor
        String cursor = (System.currentTimeMillis() % 1000 < 500) ? "|" : " ";
        displayText.clear().add(buffer).add(cursor);
        displayText.render(r, body().x1() + 4, body().y1() + 4);
    }
}
