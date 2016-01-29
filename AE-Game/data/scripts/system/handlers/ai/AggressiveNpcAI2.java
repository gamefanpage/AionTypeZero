/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ai;

import java.util.ArrayList;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.handler.AggroEventHandler;
import org.typezero.gameserver.ai2.handler.CreatureEventHandler;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author ATracer
 */
@AIName("aggressive")
public class AggressiveNpcAI2 extends GeneralNpcAI2 {

	@Override
	protected void handleCreatureSee(Creature creature) {
		CreatureEventHandler.onCreatureSee(this, creature);
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		CreatureEventHandler.onCreatureMoved(this, creature);
	}

	@Override
	protected void handleCreatureAggro(Creature creature) {
        if (canThink()) {
			AggroEventHandler.onAggro(this, creature);
        }
	}

	@Override
	protected boolean handleGuardAgainstAttacker(Creature attacker) {
		return AggroEventHandler.onGuardAgainstAttacker(this, attacker);
	}

    /**
     * NPC calls for help. NPCs in range of distance are going aggressive to
     * first target of caller
     *
     * @param distance (meters)
     */
    protected void callForHelp(int distance) {
        Creature firstTarget = getAggroList().getMostHated();
        for (VisibleObject object : getKnownList().getKnownObjects().values()) {
            if (object instanceof Npc && isInRange(object, distance)) {
                Npc npc = (Npc) object;
                if ((npc != null) && !npc.getLifeStats().isAlreadyDead()) {
                    npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, firstTarget);
                }
            }
        }
    }
    /**
     * returns a random target from npc's known-list
     *
     * @return (Player)
     */
    protected Player getRandomTarget() {
        List<Player> players = new ArrayList<Player>();
        for (Player player : getKnownList().getKnownPlayers().values()) {
            if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 50)) {
                players.add(player);
            }
        }
        if (players.isEmpty()) {
            return null;
        }
        return players.get(Rnd.get(players.size()));
    }
}
