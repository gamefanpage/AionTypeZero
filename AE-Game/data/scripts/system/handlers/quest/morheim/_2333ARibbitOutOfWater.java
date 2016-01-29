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

package quest.morheim;

import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.questEngine.task.QuestTasks;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.zone.ZoneName;


/**
 * @author Cheatkiller
 *
 */
public class _2333ARibbitOutOfWater extends QuestHandler {

	private final static int questId = 2333;

	public _2333ARibbitOutOfWater() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(798084).addOnQuestStart(questId);
		qe.registerQuestNpc(798084).addOnTalkEvent(questId);
		qe.registerQuestNpc(701147).addOnTalkEvent(questId);
		qe.registerQuestItem(182204130, questId);
		qe.registerAddOnLostTargetEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798084) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 798084) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(var == 0)
						return sendQuestDialog(env, 1011);
					else if(var == 1)
						return sendQuestDialog(env, 1352);
				}
				else if(dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
				  return checkQuestItems(env, 0, 1, false, 10000, 10001);
				}
				else if(dialog == DialogAction.SETPRO2) {
					Npc debrie = (Npc)QuestService.spawnQuestNpc(player.getWorldId(), player.getInstanceId(), 204416, player.getX(), player.getY(), player.getZ(), (byte) 8);
					WalkManager.startWalking((NpcAI2) debrie.getAi2());
					debrie.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
					PacketSendUtility.broadcastPacket(debrie, new SM_EMOTION(debrie, EmotionType.START_EMOTE2, 0, debrie.getObjectId()));
					player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, debrie, ZoneName.get("DF2_ITEMUSEAREA_Q2333")));
				  return defaultCloseDialog(env, 1, 2);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798084) {
				return sendQuestEndDialog(env);
			}
		}

	 return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 0, false, 182204131, 1, 0, 0));
		}
	 return HandlerResult.FAILED;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 2) {
				qs.setQuestVar(1);
				updateQuestStatus(env);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 2, true);
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 1, false); // 0
	}
}
