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

package quest.beluslan;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * Talk with Chieftain Akagitan (204787). Talk with Delris (204784). Find Glaciont the Hardy (213730). Kill all the Ice
 * Petrahulks: Glaciont the Hardy (213730) (1), Frostfist (213788) (1), Iceback (213789) (1), Chillblow (213790) (1),
 * Snowfury (213791) (1). Talk with Chieftain Akagitan.
 *
 * @author VladimirZ
 * @reworked vlog
 */
public class _2057GlacionttheHardy extends QuestHandler {

	private final static int questId = 2057;
	private final static int[] npc_ids = { 204787, 204784 };
	private final static int[] mob_ids = { 213730, 213788, 213789, 213790, 213791 };

	public _2057GlacionttheHardy() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182204316, questId); // Fire Bomb
		for (int mob_id : mob_ids)
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 2056);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2500, 2056 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204787) { // Chieftain Akagitan
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204787) { // Chieftain Akagitan
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						playQuestMovie(env, 246);
						return sendQuestDialog(env, 1012);
					}
					case SETPRO1: {
						playQuestMovie(env, 246);
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 204784) { // Delris
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case SETPRO2: {
						playQuestMovie(env, 247);
						return defaultCloseDialog(env, 1, 2, 182204316, 1, 0, 0); // 2
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			int targetId = env.getTargetId();
			if (var == 3) {
				int var1 = qs.getQuestVarById(1);
				int var2 = qs.getQuestVarById(2);
				int var3 = qs.getQuestVarById(3);
				int var4 = qs.getQuestVarById(4);
				int var5 = qs.getQuestVarById(5);
				int vars[] = { var1, var2, var3, var4, var5 };
				int allDead = 0;

				if (targetId == 213730 && var1 == 0) { // Glaciont the Hardy
					for (int var0 : vars) {
						if (var0 == 1) {
							allDead++;
						}
					}
					if (allDead == 4) {
						qs.setQuestVar(var);
						qs.setStatus(QuestStatus.REWARD); // reward
						updateQuestStatus(env);
						return true;
					}
					else {
						changeQuestStep(env, 0, 1, false, 1); // 1: 1
						return true;
					}
				}
				else if (targetId == 213788 && var2 == 0) { // Frostfist
					for (int var0 : vars) {
						if (var0 == 1) {
							allDead++;
						}
					}
					if (allDead == 4) {
						qs.setQuestVar(var);
						qs.setStatus(QuestStatus.REWARD); // reward
						updateQuestStatus(env);
						return true;
					}
					else {
						changeQuestStep(env, 0, 1, false, 2); // 2: 1
						return true;
					}
				}
				else if (targetId == 213789 && var3 == 0) { // Iceback
					for (int var0 : vars) {
						if (var0 == 1) {
							allDead++;
						}
					}
					if (allDead == 4) {
						qs.setQuestVar(var);
						qs.setStatus(QuestStatus.REWARD); // reward
						updateQuestStatus(env);
						return true;
					}
					else {
						changeQuestStep(env, 0, 1, false, 3); // 3: 1
						return true;
					}
				}
				else if (targetId == 213790 && var4 == 0) { // Chillblow
					for (int var0 : vars) {
						if (var0 == 1) {
							allDead++;
						}
					}
					if (allDead == 4) {
						qs.setQuestVar(var);
						qs.setStatus(QuestStatus.REWARD); // reward
						updateQuestStatus(env);
						return true;
					}
					else {
						changeQuestStep(env, 0, 1, false, 4); // 4: 1
						return true;
					}
				}
				else if (targetId == 213791 && var5 == 0) { // Snowfury
					for (int var0 : vars) {
						if (var0 == 1) {
							allDead++;
						}
					}
					if (allDead == 4) {
						qs.setQuestVar(var);
						qs.setStatus(QuestStatus.REWARD); // reward
						updateQuestStatus(env);
						return true;
					}
					else {
						changeQuestStep(env, 0, 1, false, 5); // 5: 1
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2057"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false, 248)); // 3
		}
		return HandlerResult.FAILED;
	}
}
