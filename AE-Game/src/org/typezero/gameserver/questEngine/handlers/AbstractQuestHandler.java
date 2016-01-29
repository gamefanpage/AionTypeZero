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

package org.typezero.gameserver.questEngine.handlers;

import java.util.List;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.quest.QuestItems;
import org.typezero.gameserver.model.templates.rewards.BonusType;
import org.typezero.gameserver.questEngine.model.QuestActionType;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * The methods will be overridden in concrete quest handlers
 *
 * @author vlog
 */
public abstract class AbstractQuestHandler {

	public abstract void register();

	public boolean onDialogEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onEnterWorldEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onEnterZoneEvent(QuestEnv questEnv, ZoneName zoneName) {
		return false;
	}

	public boolean onLeaveZoneEvent(QuestEnv questEnv, ZoneName zoneName) {
		return false;
	}

	public HandlerResult onItemUseEvent(QuestEnv questEnv, Item item) {
		return HandlerResult.UNKNOWN;
	}

	public boolean onHouseItemUseEvent(QuestEnv env) {
		return false;
	}

	public boolean onGetItemEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onUseSkillEvent(QuestEnv questEnv, int skillId) {
		return false;
	}

	public boolean onKillEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onAttackEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onLvlUpEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return false;
	}

	public boolean onDieEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onLogOutEvent(QuestEnv env) {
		return false;
	}

	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return false;
	}

	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return false;
	}

	public boolean onMovieEndEvent(QuestEnv questEnv, int movieId) {
		return false;
	}

	public boolean onQuestTimerEndEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onInvisibleTimerEndEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onPassFlyingRingEvent(QuestEnv questEnv, String flyingRing) {
		return false;
	}

	public boolean onKillRankedEvent(QuestEnv env) {
		return false;
	}

	public boolean onKillInWorldEvent(QuestEnv env) {
		return false;
	}

	public boolean onFailCraftEvent(QuestEnv env, int itemId) {
		return false;
	}

	public boolean onEquipItemEvent(QuestEnv env, int itemId) {
		return false;
	}

	public boolean onCanAct(QuestEnv env, QuestActionType questEventType, Object... objects) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(env.getQuestId());
		return qs != null && qs.getStatus() == QuestStatus.START;
	}

	public boolean onAddAggroListEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onAtDistanceEvent(QuestEnv questEnv) {
		return false;
	}

	public boolean onEnterWindStreamEvent(QuestEnv questEnv, int worldId) {
		return false;
	}

	public boolean rideAction(QuestEnv questEnv, int rideItemId) {
		return false;
	}

	public boolean onDredgionRewardEvent(QuestEnv env) {
		return false;
	}

	public HandlerResult onBonusApplyEvent(QuestEnv env, BonusType bonusType, List<QuestItems> rewardItems) {
		return HandlerResult.UNKNOWN;
	}

	public boolean onProtectEndEvent(QuestEnv env) {
		return false;
	}

	public boolean onProtectFailEvent(QuestEnv env) {
		return false;
	}
}
