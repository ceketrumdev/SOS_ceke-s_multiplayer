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
/*   ScMainBridge.java                                                      */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package menu;

/**
 * Public bridge so com.ceke.multiplayer packages can trigger menu actions
 * that live inside the package-private {@code menu} package.
 *
 * The actual work is delegated to {@code ScMain} which sets the static
 * {@code lastMenu} reference when it is constructed.
 */
public final class ScMainBridge {

    private ScMainBridge() {
    }

    /**
     * Load a save as multiplayer host.
     *
     * @param saveName the save directory name (relative to the game's save root)
     */
    public static void hostLoadSave(String saveName) {
        ScMain.hostLoadSave(saveName);
    }

    /** Launch new-world creation as multiplayer host. */
    public static void hostNewGame() {
        ScMain.hostNewGame();
    }

    /**
     * Load a save as multiplayer client (no server started).
     * Called by {@link com.ceke.multiplayer.network.GameClient} when the host
     * sends a {@link com.ceke.multiplayer.network.packets.PacketWorldLoad}.
     *
     * @param saveName path returned by the host (may be empty for a new-game world)
     */
    public static void clientLoadSave(String saveName) {
        ScMain.clientLoadSave(saveName);
    }

    /** Forces the client back to the main menu (e.g., on connection timeout). */
    public static void returnToMenu() {
        snake2d.CORE.setCurrentState(new snake2d.CORE_STATE.Constructor() {
            @Override
            public snake2d.CORE_STATE getState() {
                return menu.Menu.make();
            }
        });
    }
}
