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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Hellboy aion4Free
 */
public class _2035TheThreeKeys extends QuestHandler {

	private final static int questId = 2035;
	private final static int[] npc_ids = { 204317, 204408, 204407 };

	public _2035TheThreeKeys() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2300, true);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() == 320050000 && qs.getQuestVarById(0) == 5) {
				qs.setQuestVar(6);
				updateQuestStatus(env);
			}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204317: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
						case SETPRO1:
							if (var == 0) {
								qs.setQuestVarById(0, 4);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
				}
					break;
				case 204408: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 4)
								return sendQuestDialog(env, 2375);
							else if (var == 6)
								return sendQuestDialog(env, 2716);
						case SELECT_ACTION_2376:
							playQuestMovie(env, 78);
							break;
						case SETPRO5:
							if (var == 4) {
								if (!giveQuestItem(env, 182204012, 1))
									return true;
								qs.setQuestVarById(0, 5);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
						case CHECK_USER_HAS_QUEST_ITEM:
							if (var == 6) {
								if (QuestService.collectItemCheck(env, true)) {
									removeQuestItem(env, 182204012, 1);
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									return sendQuestDialog(env, 10000);
								}
								else
									return sendQuestDialog(env, 10001);
							}

					}
				}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204407) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
