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

package quest.daevation;

import org.typezero.gameserver.model.PlayerClass;
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
public class _1989ASagesTeachings extends QuestHandler {

	private final static int questId = 1989;

	public _1989ASagesTeachings() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203771).addOnQuestStart(questId);
		qe.registerQuestNpc(203771).addOnTalkEvent(questId);
		qe.registerQuestNpc(203704).addOnTalkEvent(questId);
		qe.registerQuestNpc(203705).addOnTalkEvent(questId);
		qe.registerQuestNpc(203706).addOnTalkEvent(questId);
		qe.registerQuestNpc(203707).addOnTalkEvent(questId);
		qe.registerQuestNpc(801214).addOnTalkEvent(questId);
		qe.registerQuestNpc(801215).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203771) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			PlayerClass playerClass = player.getCommonData().getPlayerClass();
			switch (targetId) {
				case 203704:// Boreas
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.GLADIATOR || playerClass == PlayerClass.TEMPLAR)
								return sendQuestDialog(env, 1352);
							else
								return sendQuestDialog(env, 1438);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203705:// Jumentis
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.ASSASSIN || playerClass == PlayerClass.RANGER)
								return sendQuestDialog(env, 1693);
							else
								return sendQuestDialog(env, 1779);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203706:// Charna
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.SORCERER || playerClass == PlayerClass.SPIRIT_MASTER)
								return sendQuestDialog(env, 2034);
							else
								return sendQuestDialog(env, 2120);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203707:// Thrasymedes
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.CLERIC || playerClass == PlayerClass.CHANTER)
								return sendQuestDialog(env, 2375);
							else
								return sendQuestDialog(env, 2461);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 801214:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.GUNNER || playerClass == PlayerClass.RIDER)
								return sendQuestDialog(env, 2548);
							else
								return sendQuestDialog(env, 2568);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 801215:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (playerClass == PlayerClass.BARD)
								return sendQuestDialog(env, 2633);
							else
								return sendQuestDialog(env, 2653);
						case SETPRO1:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
				case 203771:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 2716);
							else if (var == 2)
								return sendQuestDialog(env, 3057);
							else if (var == 3) {
								if (player.getCommonData().getDp() < 4000)
									return sendQuestDialog(env, 3484);
								else
									return sendQuestDialog(env, 3398);
							}
							else if (var == 4) {
								if (player.getCommonData().getDp() < 4000)
									return sendQuestDialog(env, 3825);
								else
									return sendQuestDialog(env, 3739);
							}
						case SELECT_QUEST_REWARD:
							if (var == 3) {
								playQuestMovie(env, 105);
								player.getCommonData().setDp(0);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							else if (var == 4) {
								playQuestMovie(env, 105);
								player.getCommonData().setDp(0);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 5);
							}
							else
								return this.sendQuestEndDialog(env);
						case SETPRO2:
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 3057);
						case SETPRO4:
							qs.setQuestVarById(0, 3);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						case SETPRO5:
							qs.setQuestVarById(0, 4);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
					}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203771)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
