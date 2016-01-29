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
public class _2394ADyingWish extends QuestHandler {

	private final static int questId = 2394;

	public _2394ADyingWish() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(204343).addOnQuestStart(questId);
		qe.registerQuestNpc(204343).addOnTalkEvent(questId);
		qe.registerQuestNpc(204381).addOnTalkEvent(questId);
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
			if (targetId == 204343) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204381) {
				if (dialog == DialogAction.QUEST_SELECT) {
						return sendQuestDialog(env, 1011);
				}
				else if (dialog == DialogAction.SETPRO1) {
					Npc orlan = (Npc)QuestService.spawnQuestNpc(player.getWorldId(), player.getInstanceId(), 790021, player.getX(), player.getY(), player.getZ(), (byte) 8);
					WalkManager.startWalking((NpcAI2) orlan.getAi2());
					orlan.getAi2().onCreatureEvent(AIEventType.FOLLOW_ME, player);
					PacketSendUtility.broadcastPacket(orlan, new SM_EMOTION(orlan, EmotionType.START_EMOTE2, 0, orlan.getObjectId()));
					player.getController().addTask(TaskId.QUEST_FOLLOW, QuestTasks.newFollowingToTargetCheckTask(env, orlan, ZoneName.get("HALABANA_HOT_SPRINGS_220020000")));
					return defaultCloseDialog(env, 0, 1);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204343) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				else {
					return sendQuestEndDialog(env);
				}
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
			if (var == 1) {
				qs.setQuestVar(0);
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
			if (var == 1) {
				qs.setQuestVar(0);
				updateQuestStatus(env);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 1, true);
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 1, 0, false); // 0
	}
}
