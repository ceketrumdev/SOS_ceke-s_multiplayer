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
/*   GameMod.java                                                           */
/*                                                                            */
/*   By: ceketrum <ferrando.ryan.mickael@gmail.com>                         */
/*                                                                            */
/*   Created: 2026/02/28 15:02:29 by ceketrum                               */
/*   Updated: 2026/02/28 15:02:29 by ceketrum                               */
/*                                                                            */
/* ************************************************************************** */

package com.ceke.multiplayer.core.server;

import com.ceke.multiplayer.core.server.rules.GameRule;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a distinct multiplayer game mode (e.g. Co-op, PvP, Trade-only).
 * A GameMod defines basic properties and provides a list of active Rules.
 */
public abstract class GameMod {

    protected final List<GameRule> rules = new ArrayList<>();

    public abstract String getName();

    public abstract int getMaxPlayers();

    public List<GameRule> getRules() {
        return rules;
    }
}
