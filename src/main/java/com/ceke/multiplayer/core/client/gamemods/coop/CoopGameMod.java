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
/*   CoopGameMod.java                                                       */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.client.gamemods.coop;

import com.ceke.multiplayer.core.server.GameMod;
import com.ceke.multiplayer.core.client.gamemods.coop.rules.MouseSyncRule;

public class CoopGameMod extends GameMod {

    public CoopGameMod() {
        // Register the rules for this specific game mode
        rules.add(new MouseSyncRule());
    }

    @Override
    public String getName() {
        return "Co-op";
    }

    @Override
    public int getMaxPlayers() {
        return 2;
    }
}
