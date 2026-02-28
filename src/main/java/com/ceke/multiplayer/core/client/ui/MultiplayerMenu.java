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
/*   MultiplayerMenu.java                                                   */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.ui;

import com.ceke.multiplayer.core.server.MultiplayerSession;
import com.ceke.multiplayer.core.server.network.GameClient;
import com.ceke.multiplayer.core.server.network.HostServer;
import com.ceke.multiplayer.core.server.sync.SaveSyncManager;
import init.constant.C;
import init.paths.PATHS;
import init.sprite.UI.UI;
import menu.MultiplayerMenuBridge;
import menu.ScMainBridge;
import snake2d.SPRITE_RENDERER;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import util.gui.misc.GText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Multiplayer lobby — state-machine UI.
 *
 * States:
 * HIDDEN – not rendered at all, main menu shown normally
 * MAIN – "Host a Game" | "Join a Game" | "< Back"
 * HOST – full-screen: saves list + "+ New Game" + "< Back" (bottom-right)
 * JOIN – IP:port text field + "Connect" + "< Back"
 */
public final class MultiplayerMenu {

    public enum State {
        HIDDEN, MAIN, HOST, JOIN
    }

    private static final Logger LOG = Logger.getLogger(MultiplayerMenu.class.getName());

    private State state = State.HIDDEN;

    // ── Shared IP buffer for the JOIN screen ──────────────────────────────
    private final StringBuilder ipBuffer = new StringBuilder();

    // ── MAIN screen ───────────────────────────────────────────────────────
    private final GuiSection mainSection;

    // ── HOST screen ───────────────────────────────────────────────────────
    private final GuiSection hostSection;

    // ── JOIN screen ───────────────────────────────────────────────────────
    private final IpInputField ipField;
    private final GuiSection joinSection;

    // ─────────────────────────────────────────────────────────────────────
    public MultiplayerMenu() {
        mainSection = buildMain();
        hostSection = buildHost();
        ipField = new IpInputField(ipBuffer, 300, 32);
        joinSection = buildJoin();
    }

    // ══════════════════════════════ BUILDING ════════════════════════════

    private GuiSection buildMain() {
        GuiSection s = new GuiSection();

        // ── Host Section ──────────────────────────────────────────────────
        GText title = new GText(UI.FONT().H2, 20);
        title.clear().add("Select a Game Mod to Host:");
        s.addDown(0, title);

        CLICKABLE host = MultiplayerMenuBridge.navButton("Co-op Mode");
        host.clickActionSet(() -> {
            com.ceke.multiplayer.core.server.ModLoader.setActiveMod(new com.ceke.multiplayer.core.client.gamemods.coop.CoopGameMod());
            setState(State.HOST);
        });
        s.addDown(8, host);

        // ── Join Section ──────────────────────────────────────────────────
        GText joinTitle = new GText(UI.FONT().H2, 20);
        joinTitle.clear().add("Or Join an Existing Game:");
        s.addDown(32, joinTitle);

        CLICKABLE join = MultiplayerMenuBridge.navButton("Join Game");
        join.clickActionSet(() -> {
            ipBuffer.setLength(0);
            setState(State.JOIN);
        });
        s.addDown(8, join);

        // ── Back Button ───────────────────────────────────────────────────
        CLICKABLE back = MultiplayerMenuBridge.navButton("< Back");
        back.clickActionSet(() -> setState(State.HIDDEN));
        s.addDown(32, back);

        return s;
    }

    private GuiSection buildHost() {
        GuiSection s = new GuiSection();

        // ── Header label ──────────────────────────────────────────────────
        GText title = new GText(UI.FONT().H2, 20);
        title.clear().add("Select a world to host:");
        s.addDown(0, title);

        // ── Save list ─────────────────────────────────────────────────────
        List<String> saves = listSaveNames();
        if (saves.isEmpty()) {
            GText noSave = new GText(UI.FONT().H2, 20);
            noSave.clear().add("No saves found");
            s.addDown(8, noSave);
        } else {
            for (String saveName : saves) {
                CLICKABLE btn = MultiplayerMenuBridge.navButton(saveName);
                btn.clickActionSet(() -> loadSave(saveName));
                s.addDown(4, btn);
            }
        }

        // ── + New Game ────────────────────────────────────────────────────
        CLICKABLE newGame = MultiplayerMenuBridge.navButton("+ New Game");
        newGame.clickActionSet(this::startNewGame);
        s.addDown(12, newGame);

        // ── < Back ────────────────────────────────────────────────────────
        CLICKABLE back = MultiplayerMenuBridge.navButton("< Back");
        back.clickActionSet(() -> setState(State.MAIN));
        s.addDown(12, back);

        return s;
    }

    private GuiSection buildJoin() {
        GuiSection s = new GuiSection();

        GText hint = new GText(UI.FONT().H2, 35);
        hint.clear().add("Click  \"Connect\"  to enter IP");
        s.addDown(0, hint);

        GText hint2 = new GText(UI.FONT().H2, 35);
        hint2.clear().add("Format:  IP  or  IP:port  (default 26565)");
        s.addDown(4, hint2);

        // Show the last typed address (display only)
        GText ipDisplay = new GText(UI.FONT().H2, 40);
        ipDisplay.clear().add(ipBuffer.length() > 0 ? ipBuffer.toString() : "(no address entered yet)");
        s.addDown(12, ipDisplay);

        CLICKABLE connect = MultiplayerMenuBridge.navButton("Connect");
        connect.clickActionSet(() -> {
            // Open a Swing dialog — the only way to get text input without a keyPush
            // callback
            String result = javax.swing.JOptionPane.showInputDialog(
                    null,
                    "Enter host IP (or IP:port):",
                    "Join Multiplayer Game",
                    javax.swing.JOptionPane.PLAIN_MESSAGE);
            if (result != null && !result.trim().isEmpty()) {
                ipBuffer.setLength(0);
                ipBuffer.append(result.trim());
                MultiplayerSession.instance().joinGame(result.trim());
                setState(State.HIDDEN);
            }
        });
        s.addDown(12, connect);

        CLICKABLE back = MultiplayerMenuBridge.navButton("< Back");
        back.clickActionSet(() -> setState(State.MAIN));
        s.addDown(8, back);

        return s;
    }

    // ══════════════════════════════ GAME ACTIONS ════════════════════════

    private void loadSave(String saveName) {
        LOG.info("[MultiplayerMenu] Loading save: " + saveName);
        MultiplayerSession.instance().startHost(saveName);
        setState(State.HIDDEN);
        ScMainBridge.hostLoadSave(saveName);
    }

    private void startNewGame() {
        LOG.info("[MultiplayerMenu] Starting new game as host.");
        MultiplayerSession.instance().startHost("");
        setState(State.HIDDEN);
        ScMainBridge.hostNewGame();
    }

    // ══════════════════════════════ RENDER / INPUT ══════════════════════

    /**
     * Called by menu.ScMain.render() every frame when this menu is visible.
     *
     * @return {@code true} if the HOST full-screen state is active — ScMain
     *         should suppress the logo and main menu buttons.
     */
    public boolean render(SPRITE_RENDERER r, float ds) {
        if (state == State.HIDDEN)
            return false;

        GuiSection active = activeSection();
        if (active != null) {
            active.body().centerX(C.DIM());
            active.body().centerY(C.DIM());
            active.render(r, ds);
        }

        return state == State.HOST;
    }

    public boolean hover(snake2d.util.datatypes.COORDINATE mCoo) {
        GuiSection active = activeSection();
        return active != null && active.hover(mCoo);
    }

    public boolean click() {
        GuiSection active = activeSection();
        return active != null && active.click();
    }

    /**
     * Called from menu.ScMain when a key is pressed.
     * Forwards character typing to the IP field on the JOIN screen.
     */
    public void onChar(char c, int keyCode) {
        if (state == State.JOIN) {
            ipField.onKey(c, keyCode);
        }
    }

    // ══════════════════════════════ HELPERS ═════════════════════════════

    private GuiSection activeSection() {
        return switch (state) {
            case MAIN -> mainSection;
            case HOST -> hostSection;
            case JOIN -> joinSection;
            default -> null;
        };
    }

    private void setState(State ns) {
        state = ns;
        LOG.info("[MultiplayerMenu] State → " + ns);
    }

    /** True whenever any multiplayer screen is shown (including HOST). */
    public boolean isVisible() {
        return state != State.HIDDEN;
    }

    /** True only when the HOST full-screen is active. */
    public boolean isHostScreen() {
        return state == State.HOST;
    }

    public void setVisible(boolean v) {
        setState(v ? State.MAIN : State.HIDDEN);
    }

    /** @deprecated Kept for source compatibility only. */
    @Deprecated
    public JoinGameDialog joinDialog() {
        return null;
    }

    // ── Get save folder names ─────────────
    private static List<String> listSaveNames() {
        List<String> names = new ArrayList<>();
        try {
            // PATHS.local().save() points directly to
            // AppData/Roaming/songsofsyx/saves/saves/
            java.nio.file.Path base = PATHS.local().save().get("");
            if (base != null) {
                File savesRoot = base.toFile();
                File[] saves = savesRoot.listFiles(f -> f.isFile() && f.getName().endsWith(".save"));
                if (saves != null) {
                    for (File save : saves) {
                        String n = save.getName();
                        n = n.substring(0, n.length() - 5); // remove .save
                        names.add(n);
                    }
                }
            }
        } catch (Exception e) {
            // Fallback: scan AppData directly
            String appdata = System.getenv("APPDATA");
            if (appdata != null) {
                File savesRoot = new File(appdata, "songsofsyx/saves/saves");
                File[] saves = savesRoot.listFiles(f -> f.isFile() && f.getName().endsWith(".save"));
                if (saves != null) {
                    for (File s : saves) {
                        String n = s.getName();
                        n = n.substring(0, n.length() - 5);
                        names.add(n);
                    }
                }
            }
        }
        return names;
    }
}
