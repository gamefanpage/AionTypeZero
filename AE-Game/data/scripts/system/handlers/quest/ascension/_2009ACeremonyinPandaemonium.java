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

package quest.ascension;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author MrPoke
 */
public class _2009ACeremonyinPandaemonium extends QuestHandler {

	private final static int questId = 2009;

	public _2009ACeremonyinPandaemonium() {
		super(questId);
	}

	@Override
	public void register() {
		if (CustomConfig.ENABLE_SIMPLE_2NDCLASS)
			return;
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
		qe.registerQuestNpc(204182).addOnTalkEvent(questId);
		qe.registerQuestNpc(204075).addOnTalkEvent(questId);
		qe.registerQuestNpc(204080).addOnTalkEvent(questId);
		qe.registerQuestNpc(204081).addOnTalkEvent(questId);
		qe.registerQuestNpc(204082).addOnTalkEvent(questId);
		qe.registerQuestNpc(204083).addOnTalkEvent(questId);
		qe.registerQuestNpc(801220).addOnTalkEvent(questId);
		qe.registerQuestNpc(801221).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203550) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SETPRO1:
						if (var == 0) {
							qs.setQuestVar(1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
							TeleportService2.teleportTo(player, 120010000, 1685f, 1400f, 195f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
							return true;
						}
				}
			}
			else if (targetId == 204182) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 1)
							return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						if (var == 1) {
							playQuestMovie(env, 121);
							return false;
						}
					case SETPRO2:
						return defaultCloseDialog(env, 1, 2); // 2
				}
			}
			else if (targetId == 204075) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 2)
							return sendQuestDialog(env, 1693);
					case SELECT_ACTION_1694:
						if (var == 2) {
							playQuestMovie(env, 122);
							return false;
						}
					case SETPRO3:
						if (var == 2) {
							PlayerClass playerClass = PlayerClass.getStartingClassFor(player.getCommonData().getPlayerClass());
							if (playerClass == PlayerClass.WARRIOR)
								qs.setQuestVar(10);
							else if (playerClass == PlayerClass.SCOUT)
								qs.setQuestVar(20);
							else if (playerClass == PlayerClass.MAGE)
								qs.setQuestVar(30);
							else if (playerClass == PlayerClass.PRIEST)
								qs.setQuestVar(40);
							else if (playerClass == PlayerClass.ENGINEER)
								qs.setQuestVar(50);
							else if (playerClass == PlayerClass.ARTIST)
								qs.setQuestVar(60);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204080 && var == 10) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2034);
					case 1009:
						return sendQuestDialog(env, 5);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 0)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 204081 && var == 20) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2375);
					case 1009:
						return sendQuestDialog(env, 6);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 1)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 204082 && var == 30) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 2716);
					case 1009:
						return sendQuestDialog(env, 7);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 2)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 204083 && var == 40) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3057);
					case 1009:
						return sendQuestDialog(env, 8);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 3)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 801220 && var == 50) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3398);
					case 1009:
						return sendQuestDialog(env, 45);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 4)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
			else if (targetId == 801221 && var == 60) {
				switch (env.getDialogId()) {
					case -1:
						return sendQuestDialog(env, 3739);
					case 1009:
						return sendQuestDialog(env, 46);
					case 8:
					case 9:
					case 10:
					case 11:
					case 12:
					case 13:
					case 14:
					case 15:
					case 16:
					case 18:
						if (QuestService.finishQuest(env, 5)) {
							return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2008);
	}
}
