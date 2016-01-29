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

package quest.poeta;

import org.typezero.gameserver.model.EmotionId;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author MrPoke
 */
public class _1005BarringtheGate extends QuestHandler {

	private final static int questId = 1005;

	public _1005BarringtheGate() {
		super(questId);
	}

	@Override
	public void register() {
		int[] talkNpcs = { 203067, 203081, 790001, 203085, 203086, 700080, 700081, 700082, 700083 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int id : talkNpcs)
			qe.registerQuestNpc(id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203067) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SETPRO1:
						if (var == 0) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203081) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case SETPRO2:
						if (var == 1) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 790001) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 2)
							return sendQuestDialog(env, 1693);
					case SETPRO3:
						if (var == 2) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203085) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 3)
							return sendQuestDialog(env, 2034);
					case SETPRO4:
						if (var == 3) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 203086) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 4)
							return sendQuestDialog(env, 2375);
					case SETPRO5:
						if (var == 4) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							sendQuestSelectionDialog(env);
							return true;
						}
				}
			}
			else if (targetId == 700081) {
				if (var == 5) {
					destroy(6, env);
					return false;
				}
			}
			else if (targetId == 700082) {
				if (var == 6) {
					destroy(7, env);
					return false;
				}
			}
			else if (targetId == 700083) {
				if (var == 7) {
					destroy(8, env);
					return false;
				}
			}
			else if (targetId == 700080) {
				if (var == 8) {
					destroy(-1, env);
					return false;
				}
			}

		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203067) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
                            		playQuestMovie(env, 171);
					return sendQuestDialog(env, 2716);
				}
				else
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = { 1100, 1001, 1002, 1003, 1004 };
		return defaultOnZoneMissionEndEvent(env, quests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1100, 1001, 1002, 1003, 1004 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	private void destroy(final int var, final QuestEnv env) {
		final int targetObjectId = env.getVisibleObject().getObjectId();

		final Player player = env.getPlayer();
		PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 1));
		PacketSendUtility
			.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0, targetObjectId), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
					return;
				PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000, 0));
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0, targetObjectId),
					true);
				sendEmotion(env, player, EmotionId.STAND, true);
				QuestState qs = player.getQuestStateList().getQuestState(questId);
				if (var != -1)
					qs.setQuestVarById(0, var);
				else {
					playQuestMovie(env, 21);
					qs.setStatus(QuestStatus.REWARD);
				}
				updateQuestStatus(env);
			}
		}, 3000);
	}
}
