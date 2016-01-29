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

package quest.heiron;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rhys2002
 */
public class _1057CreatingaMonster extends QuestHandler {

	private final static int questId = 1057;
	private final static int[] npc_ids = { 204502, 204619, 700218, 700279, 204500 };

	public _1057CreatingaMonster() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		qe.registerQuestNpc(700219).addOnKillEvent(questId);
		qe.registerQuestNpc(212211).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 1056);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1500, 1056 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204500) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					int[] questItems = { 182201616 };
					return sendQuestEndDialog(env, questItems);
				}
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204502) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
					else if (var == 3)
						return sendQuestDialog(env, 2034);
				case SELECT_ACTION_2036:
					if (var == 3)
						playQuestMovie(env, 190);
					return false;
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case SETPRO4:
					if (var == 3) {
						removeQuestItem(env, 182201616, 1);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204619) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 700218 && qs.getQuestVarById(0) == 2) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				return sendQuestDialog(env, 1693);
			}
			else if (env.getDialog() == DialogAction.SETPRO3) {
				if (!giveQuestItem(env, 182201616, 1))
					return false;
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
				changeQuestStep(env, 2, 3, false); // 3
				return true;
			}
		}
		else if (targetId == 700279 && qs.getQuestVarById(0) == 9) {
			if (env.getDialog() == DialogAction.USE_OBJECT) {
				return useQuestObject(env, 9, 9, true, false); // reward
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 310050000 && qs.getQuestVarById(0) == 4) {
				qs.setQuestVar(5);
				updateQuestStatus(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId == 700219 && qs.getQuestVarById(0) < 8) {
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			updateQuestStatus(env);
		}
		else if (targetId == 212211 && qs.getQuestVarById(0) == 8) {
			qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
			updateQuestStatus(env);
		}
		return false;
	}
}
