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

package com.ceke.multiplayer.core.client.ui;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import menu.MultiplayerMenuBridge;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;

import java.util.logging.Logger;

/**
 * Pop-up for the client to enter the host IP address.
 */
public final class JoinGameDialog extends GuiSection {

    private static final Logger LOG = Logger.getLogger(JoinGameDialog.class.getName());

    private final StringBuilder ipBuffer = new StringBuilder("127.0.0.1");
    private boolean visible = false;

    public JoinGameDialog(MultiplayerMenu parent) {
        buildUI(parent);
    }

    private void buildUI(MultiplayerMenu parent) {
        IpInputField ipField = new IpInputField(ipBuffer, 280, 28);
        addDown(4, ipField);

        CLICKABLE connectBtn = MultiplayerMenuBridge.navButton("Connect");
        connectBtn.clickActionSet(() -> {
            String ip = ipBuffer.toString().trim();
            if (!ip.isEmpty()) {
                try {
                    // Validate IP address format / resolution
                    java.net.InetAddress.getByName(ip);

                    visible = false;
                    MultiplayerSession.instance().joinGame(ip);
                    LOG.info("[JoinGameDialog] Joining: " + ip);
                } catch (Exception e) {
                    LOG.warning("[JoinGameDialog] Invalid IP Address entered: " + ip);
                }
            }
        });
        addDown(8, connectBtn);

        CLICKABLE cancelBtn = MultiplayerMenuBridge.navButton("Cancel");
        cancelBtn.clickActionSet(() -> visible = false);
        addDown(4, cancelBtn);
    }

    public boolean isVisible() {
        return visible;
    }

    public void show() {
        visible = true;
    }
}
