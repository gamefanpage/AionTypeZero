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

package ai.instance.beshmundirTemple;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.skillengine.SkillEngine;


/**
 * @author Luzien
 *
 */
@AIName("templeSoul")
public class SacrificialSoulAI2 extends GeneralNpcAI2 {

	private Npc boss;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		AI2Actions.useSkill(this, 18901);
		this.setStateIfNot(AIState.FOLLOWING);
		boss = getPosition().getWorldMapInstance().getNpc(216263);
		if (boss != null && !NpcActions.isAlreadyDead(boss)) {
			AI2Actions.targetCreature(this, boss);
			getMoveController().moveToTargetObject();
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (creature.getEffectController().hasAbnormalEffect(18959)) {
			getMoveController().abortMove();
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	protected void handleMoveArrived() {
		if (boss != null && !NpcActions.isAlreadyDead(boss)) {
			SkillEngine.getInstance().getSkill(getOwner(), 18960, 55, boss).useNoAnimationSkill();
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	public boolean canThink() {
		return false;
	}
}
