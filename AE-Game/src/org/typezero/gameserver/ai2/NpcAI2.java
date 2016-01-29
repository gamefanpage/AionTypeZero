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

package org.typezero.gameserver.ai2;

import org.typezero.gameserver.ai2.handler.ActivateEventHandler;
import org.typezero.gameserver.ai2.handler.DiedEventHandler;
import org.typezero.gameserver.ai2.handler.ShoutEventHandler;
import org.typezero.gameserver.ai2.handler.SpawnEventHandler;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.ai2.poll.NpcAIPolls;
import org.typezero.gameserver.configs.main.AIConfig;
import org.typezero.gameserver.controllers.attack.AggroList;
import org.typezero.gameserver.controllers.effect.EffectController;
import org.typezero.gameserver.controllers.movement.NpcMoveController;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.skill.NpcSkillList;
import org.typezero.gameserver.model.stats.container.NpcLifeStats;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.knownlist.KnownList;

/**
 * @author ATracer
 */
@AIName("npc")
public class NpcAI2 extends AITemplate {

	@Override
	public Npc getOwner() {
		return (Npc) super.getOwner();
	}

	protected NpcTemplate getObjectTemplate() {
		return getOwner().getObjectTemplate();
	}

	protected SpawnTemplate getSpawnTemplate() {
		return getOwner().getSpawn();
	}

	protected NpcLifeStats getLifeStats() {
		return getOwner().getLifeStats();
	}

	protected Race getRace() {
		return getOwner().getRace();
	}

	protected TribeClass getTribe() {
		return getOwner().getTribe();
	}

	protected EffectController getEffectController() {
		return getOwner().getEffectController();
	}

	protected KnownList getKnownList() {
		return getOwner().getKnownList();
	}

	protected AggroList getAggroList() {
		return getOwner().getAggroList();
	}

	protected NpcSkillList getSkillList() {
		return getOwner().getSkillList();
	}

	protected VisibleObject getCreator() {
		return getOwner().getCreator();
	}

	/**
	 * DEPRECATED as movements will be processed as commands only from ai
	 */
	protected NpcMoveController getMoveController() {
		return getOwner().getMoveController();
	}

	protected int getNpcId() {
		return getOwner().getNpcId();
	}

	protected int getCreatorId() {
		return getOwner().getCreatorId();
	}

	protected boolean isInRange(VisibleObject object, int range) {
		return MathUtil.isIn3dRange(getOwner(), object, range);
	}

	@Override
	protected void handleActivate() {
		ActivateEventHandler.onActivate(this);
	}

	@Override
	protected void handleDeactivate() {
		ActivateEventHandler.onDeactivate(this);
	}

	@Override
	protected void handleSpawned() {
		SpawnEventHandler.onSpawn(this);
	}

	@Override
	protected void handleRespawned() {
		SpawnEventHandler.onRespawn(this);
	}

	@Override
	protected void handleDespawned() {
		if (poll(AIQuestion.CAN_SHOUT))
			ShoutEventHandler.onBeforeDespawn(this);
		SpawnEventHandler.onDespawn(this);
	}

	@Override
	protected void handleDied() {
		DiedEventHandler.onSimpleDie(this);
	}

	@Override
	protected void handleMoveArrived() {
		if (!poll(AIQuestion.CAN_SHOUT) || getSpawnTemplate().getWalkerId() == null)
			return;
		ShoutEventHandler.onReachedWalkPoint(this);
	}

	@Override
	protected void handleTargetChanged(Creature creature) {
		super.handleMoveArrived();
		if (!poll(AIQuestion.CAN_SHOUT))
			return;
		ShoutEventHandler.onSwitchedTarget(this, creature);
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return NpcAIPolls.shouldDecay(this);
			case SHOULD_RESPAWN:
				return NpcAIPolls.shouldRespawn(this);
			case SHOULD_REWARD:
				return AIAnswers.POSITIVE;
			case CAN_SHOUT:
				return isMayShout() ? AIAnswers.POSITIVE : AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}

	@Override
	public boolean isMayShout() {
		// temp fix, we shouldn't rely on it because of inheritance
		if (AIConfig.SHOUTS_ENABLE)
			return getOwner().mayShout(0);
		return false;
	}

	public boolean isMoveSupported() {
		return getOwner().getGameStats().getMovementSpeedFloat() > 0 && !this.isInSubState(AISubState.FREEZE);
	}

}
