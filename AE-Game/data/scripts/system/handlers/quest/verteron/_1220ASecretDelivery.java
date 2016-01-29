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

package quest.verteron;

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
 * @author Balthazar
 */
public class _1220ASecretDelivery extends QuestHandler {

	private final static int questId = 1220;

	public _1220ASecretDelivery() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203172).addOnQuestStart(questId);
		qe.registerQuestNpc(203172).addOnTalkEvent(questId);
		qe.registerQuestNpc(798004).addOnTalkEvent(questId);
		qe.registerQuestNpc(205240).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203172) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798004: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1352);
						}
						case SETPRO1: {
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
					}
				}
				case 205240: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 2375);
						}
						case SELECT_QUEST_REWARD: {
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestEndDialog(env);
						}
						default:
							return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205240) {
				switch (env.getDialog()) {
					case SELECT_QUEST_REWARD: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
