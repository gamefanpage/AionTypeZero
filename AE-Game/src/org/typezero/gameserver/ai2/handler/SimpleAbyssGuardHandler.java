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

package org.typezero.gameserver.ai2.handler;

import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.geo.GeoService;

/**
 * @author Rolandas
 *
 */
public class SimpleAbyssGuardHandler {

	public static void onCreatureMoved(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	/**
	 * @param npcAI
	 * @param creature
	 */
	public static void onCreatureSee(NpcAI2 npcAI, Creature creature) {
		checkAggro(npcAI, creature);
	}

	protected static void checkAggro(NpcAI2 ai, Creature creature) {
		if (!(creature instanceof Npc)) {
			CreatureEventHandler.checkAggro(ai, creature);
			return;
		}

		Npc owner = ai.getOwner();
		if (creature.getLifeStats().isAlreadyDead() || !owner.canSee(creature))
			return;

		Npc npc = ((Npc) creature);
		if (!npc.isEnemy(creature)
				|| npc.getLevel() < 2)
			return;

		// Creatures which are under attack not handled
		if (creature.getTarget() != null)
			return;

		if (!owner.getActiveRegion().isMapRegionActive())
			return;

		if (!ai.isInState(AIState.FIGHT)
				&& (MathUtil.isIn3dRange(owner, creature, owner.getObjectTemplate().getAggroRange()))) {
			if (GeoService.getInstance().canSee(owner, creature)) {
				if (!ai.isInState(AIState.RETURNING))
					ai.getOwner().getMoveController().storeStep();
				ai.onCreatureEvent(AIEventType.CREATURE_AGGRO, creature);
			}
		}
	}

}
