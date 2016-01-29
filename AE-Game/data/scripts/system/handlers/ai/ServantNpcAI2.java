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

import java.util.concurrent.Future;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.NpcObjectType;
import org.typezero.gameserver.model.skill.NpcSkillEntry;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
@AIName("servant")
public class ServantNpcAI2 extends GeneralNpcAI2 {

	@Override
	public void think() {
		// servants are not thinking
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getCreator() != null) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					if (getOwner().getNpcObjectType() != NpcObjectType.TOTEM)
						AI2Actions.targetCreature(ServantNpcAI2.this, (Creature) getCreator().getTarget());
					else
						AI2Actions.targetSelf(ServantNpcAI2.this);
					healOrAttack();
				}
			}, 200);
		}
	}


	private void healOrAttack() {
		if (skillId == 0) {
			NpcSkillEntry npcSkill = getSkillList().getRandomSkill();
			if (npcSkill == null)
				return;
			skillId = npcSkill.getSkillId();
		}
		int duration = getOwner().getNpcObjectType() == NpcObjectType.TOTEM ? 3000 : 5000;
		Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				getOwner().getController().useSkill(skillId, 1);
			}
		}, 1000, duration);
		getOwner().getController().addTask(TaskId.SKILL_USE, task);
	}

	@Override
	public boolean isMoveSupported() {
		return false;
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

}
