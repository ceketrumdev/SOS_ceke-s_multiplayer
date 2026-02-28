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
/*   ScMain.java                                                            */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package menu;

import static menu.GUI.getBackArrow;
import static menu.GUI.getNavButt;
import static menu.GUI.left;
import static menu.GUI.right;

import game.GAME;
import game.VERSION;
import game.battle.state.BattleStateExiter;
import game.battle.state.BattleStateResult;
import game.battle.state.BattleState;
import game.save.GameLoader;
import init.constant.C;
import init.paths.PATHS;
import init.settings.S;
import init.sprite.UI.UI;
import menu.GUI.Button;
import snake2d.CORE;
import snake2d.CORE_STATE;
import snake2d.CORE_STATE.Constructor;
import snake2d.SPRITE_RENDERER;
import snake2d.util.datatypes.COORDINATE;
import snake2d.util.gui.GuiSection;
import snake2d.util.gui.clickable.CLICKABLE;
import snake2d.util.gui.renderable.RENDEROBJ;
import snake2d.util.misc.ACTION;
import util.gui.misc.GText;
import util.text.D;
import view.main.VIEW;
import view.menu.MenuScreenLoad;
import world.battle.spec.BATTLE_RESULT;

class ScMain implements SC {

    // ── Static bridge so com.ceke packages can trigger menu actions ────────
    private static Menu lastMenu = null;

    /** Called by MultiplayerMenu to load a save as host. */
    public static void hostLoadSave(String saveName) {
        if (lastMenu == null)
            return;
        java.nio.file.Path p = init.paths.PATHS.local().save().get(saveName + ".save");
        lastMenu.start(new game.save.GameLoader(p) {
            @Override
            public void doAfterSet() {
                /* host hook */ }
        });
    }

    /** Called by MultiplayerMenu to start world creation as host. */
    public static void hostNewGame() {
        snake2d.CORE.setCurrentState(new snake2d.CORE_STATE.Constructor() {
            @Override
            public snake2d.CORE_STATE getState() {
                return game.GAME.create();
            }
        });
    }

    private static volatile boolean pendingClientLoad = false;
    private static volatile String pendingClientSaveName = null;

    /**
     * Called by GameClient when it receives PacketWorldLoad.
     * Loads the save, or starts a new game if saveName is empty.
     * Executed on the background network thread, so we defer UI changes to
     * render().
     */
    public static void clientLoadSave(String saveName) {
        pendingClientSaveName = saveName;
        pendingClientLoad = true;
    }
    // ─────────────────────────────────────────────────────────────────────

    private final GuiSection first;
    private final GuiSection play;
    private final GuiSection load;
    private GuiSection current;
    private final RENDEROBJ.Sprite logo;
    private final Menu menu;
    private final GText version = new GText(UI.FONT().H2, VERSION.VERSION_STRING);

    ScMain(Menu menu) {

        D.t(this);
        this.menu = menu;
        lastMenu = menu;
        first = getFirst(menu);
        play = getPlay(menu);
        play.body().moveY1(first.body().y1());
        load = getLoad(menu);
        load.body().moveY1(first.body().y1());

        logo = new RENDEROBJ.Sprite(menu.res.s().logo);
        logo.body().moveX2(left.x2());
        logo.body().centerY(left);
        logo.setColor(GUI.COLORS.menu);

        current = first;

    }

    private static CharSequence ¤¤continue = "continue";
    private static CharSequence ¤¤quit = "quit";
    private static CharSequence ¤¤play = "play";
    private static CharSequence ¤¤editor = "editor";
    private static CharSequence ¤¤battle = "quick battle";
    private static CharSequence ¤¤load = "load";
    private static CharSequence ¤¤loadB = "debug battle";
    private static CharSequence ¤¤tutorial = "tutorial";
    private static CharSequence ¤¤multiplayer = "Multiplayer";

    static {
        D.ts(ScMain.class);
    }

    private GuiSection getFirst(Menu menu) {

        GuiSection current = new GuiSection();
        CLICKABLE text;

        text = getNavButt(¤¤play);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                switchNavigator(play);
            }
        });
        current.addDown(0, text);

        if (!menu.load.hasSaves()) {
            text = new Button(UI.FONT().H1.getText(¤¤tutorial)) {

                @Override
                protected void clickA() {
                    menu.switchScreen(menu.campaigns);
                }
            };
            current.addDown(8, text);

        } else {
            text = new Button(UI.FONT().H1.getText(¤¤continue)) {
                @Override
                protected void render(SPRITE_RENDERER r, float ds, boolean isActive, boolean isSelected,
                        boolean isHovered) {
                    activeSet(menu.load.hasSaves());
                    super.render(r, ds, isActive, isSelected, isHovered);
                }

                @Override
                protected void clickA() {
                    if (menu.load.hasSaves())
                        menu.load.loadSave();
                }
            };
            current.addDown(8, text);
        }

        text = getNavButt(ScOptions.¤¤name);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.switchScreen(menu.options);
            }
        });
        current.addDown(8, text);

        text = getNavButt(ScCredits.¤¤name);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.switchScreen(menu.credits);
            }
        });
        current.addDown(8, text);

        // ── CEKE MULTIPLAYER MOD ────────────────────────────────────────
        text = getNavButt(¤¤multiplayer);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                // Appel ici (au moment du clic), pas à la construction de ScMain
                // — le mod est déjà initialisé quand l'utilisateur clique
                com.ceke.multiplayer.core.client.ui.MultiplayerMenu mpMenu = com.ceke.multiplayer.MultiplayerMod
                        .getMenuInstance();
                if (mpMenu != null)
                    mpMenu.setVisible(true);
            }
        });
        current.addDown(8, text);
        // ────────────────────────────────────────────────────────────────

        text = getNavButt(¤¤quit);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                CORE.annihilate();
            }
        });
        current.addDown(8, text);

        current.body().moveX1(right.x1());
        current.body().centerY(right.y1(), right.y2());

        return current;
    }

    private GuiSection getLoad(Menu menu) {

        GuiSection current = new GuiSection();

        CLICKABLE text;

        text = getNavButt(MenuScreenLoad.¤¤name);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.switchScreen(menu.load);
            }
        });
        current.addDown(0, text);

        if (S.get().developer && PATHS.local().save().exists(BattleState.debugLoad)) {
            text = getNavButt(¤¤loadB);
            text.clickActionSet(new ACTION() {
                @Override
                public void exe() {
                    menu.start(new GameLoader(PATHS.local().save().get(BattleState.debugLoad)) {

                        @Override
                        public void doAfterSet() {
                            BattleState.setLoaded(new BattleStateExiter() {

                                @Override
                                public void afterExit(BattleStateResult res) {

                                }

                                @Override
                                public void exit(BATTLE_RESULT res, int plosses, int elosses) {
                                    CORE.setCurrentState(new CORE_STATE.Constructor() {
                                        @Override
                                        public CORE_STATE getState() {
                                            return Menu.make();
                                        }
                                    });
                                }

                            }, saveFile, true);
                        }

                    });
                }
            });
            current.addDown(8, text);
        }

        for (ScLoad l : menu.loads) {
            text = getNavButt(l.name);
            text.clickActionSet(new ACTION() {
                @Override
                public void exe() {
                    menu.switchScreen(l);
                }
            });
            if (!l.hasSaves())
                text.activeSet(false);
            current.addDown(8, text);
        }

        text = getBackArrow();
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                switchNavigator(play);
            }
        });
        current.addDown(10, text);

        current.body().moveX1(right.x1());
        current.body().centerY(right);

        return current;
    }

    private GuiSection getPlay(Menu menu) {

        GuiSection current = new GuiSection();

        CLICKABLE text;

        text = getNavButt(¤¤load);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                switchNavigator(load);
            }
        });
        current.addDown(0, text);

        text = getNavButt(ScCampaign.¤¤name);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.switchScreen(menu.campaigns);

            }
        });
        current.addDown(8, text);

        text = getNavButt(ScRandom.¤¤name);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.switchScreen(menu.sandbox);
            }
        });
        current.addDown(8, text);

        text = getNavButt(¤¤battle);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.start(new Constructor() {

                    @Override
                    public CORE_STATE getState() {
                        CORE_STATE s = GAME.create();

                        VIEW.b().editor.activate();

                        return s;
                    }
                });
            }
        });
        current.addDown(8, text);

        text = getNavButt(¤¤editor);
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                menu.start(new Constructor() {

                    @Override
                    public CORE_STATE getState() {
                        CORE_STATE s = GAME.create();

                        VIEW.world().editor.activate();

                        return s;
                    }
                });
            }
        });
        current.addDown(8, text);

        text = getBackArrow();
        text.clickActionSet(new ACTION() {
            @Override
            public void exe() {
                switchNavigator(first);
            }
        });
        current.addDown(10, text);

        current.body().moveX1(right.x1());
        current.body().centerY(right);

        return current;
    }

    private void switchNavigator(GuiSection section) {
        current = section;
        current.hover(menu.getMCoo());
    }

    @Override
    public void render(SPRITE_RENDERER r, float ds) {
        if (pendingClientLoad) {
            pendingClientLoad = false;
            String saveName = pendingClientSaveName;

            if (lastMenu != null) {
                if (saveName == null || saveName.trim().isEmpty()) {
                    // Host started a new game
                    snake2d.CORE.setCurrentState(new snake2d.CORE_STATE.Constructor() {
                        @Override
                        public snake2d.CORE_STATE getState() {
                            return game.GAME.create();
                        }
                    });
                } else {
                    // Host loaded an existing save. MP_Downloaded is already .save
                    String finalName = saveName.endsWith(".save") ? saveName : saveName + ".save";
                    java.nio.file.Path p = init.paths.PATHS.local().save().get(finalName);
                    lastMenu.start(new game.save.GameLoader(p) {
                        @Override
                        public void doAfterSet() {
                            // Hook for any client-side post-load logic
                        }
                    });
                }
            }
            return;
        }

        com.ceke.multiplayer.core.client.ui.MultiplayerMenu mpMenu = com.ceke.multiplayer.MultiplayerMod.getMenuInstance();

        boolean mpVisible = mpMenu != null && mpMenu.isVisible();

        // Suppress logo and main buttons whenever a multiplayer screen is shown
        if (!mpVisible) {
            logo.render(r, ds);
            current.render(r, ds);
        }

        // Render the multiplayer overlay (also returns true for HOST full-screen)
        if (mpVisible) {
            mpMenu.render(r, ds);
        }

        version.render(r, C.DIM().x2() - 32 - version.width(), 32);
    }

    @Override
    public boolean hover(COORDINATE mCoo) {
        com.ceke.multiplayer.core.client.ui.MultiplayerMenu mpMenu = com.ceke.multiplayer.MultiplayerMod.getMenuInstance();
        if (mpMenu != null && mpMenu.isVisible()) {
            return mpMenu.hover(mCoo);
        }
        return current.hover(mCoo);
    }

    @Override
    public boolean click() {
        com.ceke.multiplayer.core.client.ui.MultiplayerMenu mpMenu = com.ceke.multiplayer.MultiplayerMod.getMenuInstance();
        if (mpMenu != null && mpMenu.isVisible()) {
            return mpMenu.click();
        }
        return current.click();
    }

    @Override
    public boolean back(Menu menu) {
        com.ceke.multiplayer.core.client.ui.MultiplayerMenu mpMenu = com.ceke.multiplayer.MultiplayerMod.getMenuInstance();
        if (mpMenu != null && mpMenu.isVisible()) {
            mpMenu.setVisible(false);
            return true;
        }
        if (current == load) {
            switchNavigator(play);
            return true;
        }
        if (current != first) {
            switchNavigator(first);
            return true;
        }
        return false;
    }

}
