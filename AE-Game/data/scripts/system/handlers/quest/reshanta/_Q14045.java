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

package quest.reshanta;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Rhys2002
 * @reworked vlog
 */
public class _Q14045 extends QuestHandler {

	private final static int questId = 14045;
	private boolean killed = false;

	public _Q14045() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 278506, 279023, 278643 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(214102).addOnKillEvent(questId);
		for (int npc : npcs) {
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 278506: { // Tellus
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SELECT_ACTION_1013: {
							playQuestMovie(env, 272);
							break;
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 279023: { // Agemonerk
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(57001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 57001, 0));
							return defaultCloseDialog(env, 1, 2); // 2
						}
					}
					break;
				}
				case 278643: { // Raithor
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 3) {
								if (killed) {
									return sendQuestDialog(env, 2034);
								}
							}
						}
						case SETPRO3: {
							if (var == 2) {
								QuestService.addNewSpawn(400010000, player.getInstanceId(), 214102, 2344.32f, 1789.96f, 2258.88f,
									(byte) 86);
								QuestService.addNewSpawn(400010000, player.getInstanceId(), 214102, 2344.51f, 1786.01f, 2258.88f,
									(byte) 52);
								return defaultCloseDialog(env, 2, 3); // 3
							}
						}
						case SETPRO4: {
							if (var == 3) {
								player.setState(CreatureState.FLIGHT_TELEPORT);
								player.unsetState(CreatureState.ACTIVE);
								player.setFlightTeleportId(58001);
								PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 58001, 0));
								qs.setQuestVarById(0, 12);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 279023) { // Agemonerk
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
		if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (env.getTargetId() == 214102) {
				if (var == 3) {
					killed = true;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env, 14040);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 14040, 14041 };
		return defaultOnLvlUpEvent(env, quests, true);
	}
}
