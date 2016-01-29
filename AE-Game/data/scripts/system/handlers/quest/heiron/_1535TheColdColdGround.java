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
 * @author Rolandas
 */
public class _1535TheColdColdGround extends QuestHandler {

	private final static int questId = 1535;

	public _1535TheColdColdGround() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204580).addOnQuestStart(questId);
		qe.registerQuestNpc(204580).addOnTalkEvent(questId);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId != 204580)
			return false;

		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (env.getDialog() == DialogAction.QUEST_SELECT)
				return sendQuestDialog(env, 4762);
			else
				return sendQuestStartDialog(env);
		}

		if (qs.getStatus() == QuestStatus.START) {
			boolean abexSkins = player.getInventory().getItemCountByItemId(182201818) > 4;
			boolean worgSkins = player.getInventory().getItemCountByItemId(182201819) > 2;
			boolean karnifSkins = player.getInventory().getItemCountByItemId(182201820) > 0;

			switch (env.getDialog()) {
				case USE_OBJECT:
				case QUEST_SELECT:
					if (abexSkins || worgSkins || karnifSkins)
						return sendQuestDialog(env, 1352);
				case SETPRO1:
					if (abexSkins) {
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
					break;
				case SETPRO2:
					if (worgSkins) {
						qs.setQuestVarById(0, 2);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 6);
					}
					break;
				case SETPRO3:
					if (karnifSkins) {
						qs.setQuestVarById(0, 3);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 7);
					}
					break;
			}
			return sendQuestDialog(env, 1693);
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				removeQuestItem(env, 182201818, 5);
				return sendQuestEndDialog(env);
			}
			else if (var == 2) {
				// add Greater Mana Potion x 5
				if (!giveQuestItem(env, 162000010, 5)) {
					// check later
					qs.setStatus(QuestStatus.START);
					updateQuestStatus(env);
				}
				else {
					removeQuestItem(env, 182201819, 3);
				}
				sendQuestEndDialog(env);
				return true;
			}
			else if (var == 3) {
				// add Greater Life Serum x 5
				if (!giveQuestItem(env, 162000015, 5)) {
					// check later
					qs.setStatus(QuestStatus.START);
					updateQuestStatus(env);
				}
				else {
					removeQuestItem(env, 182201820, 1);
				}
				sendQuestEndDialog(env);
				return true;
			}
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
		}
		return false;
	}
}
