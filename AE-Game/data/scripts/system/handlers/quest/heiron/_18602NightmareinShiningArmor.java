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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Gigi
 */
public class _18602NightmareinShiningArmor extends QuestHandler {

	private final static int questId = 18602;
	private final static int[] npc_ids = { 205229, 700939 };

	public _18602NightmareinShiningArmor() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterWorld(questId);
		qe.registerOnDie(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		qe.registerQuestNpc(205229).addOnQuestStart(questId);
		qe.registerQuestNpc(217005).addOnKillEvent(questId);
		qe.registerQuestNpc(700939).addOnAtDistanceEvent(questId);
		qe.registerOnMovieEndQuest(454, questId);
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.getWorldId() != 300230000) {
				int var = qs.getQuestVarById(0);
				if (var > 0) {
					changeQuestStep(env, var, 0, false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var > 0) {
				changeQuestStep(env, var, 0, false);
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			return defaultOnKillEvent(env, 217005, 3, true);
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		if (movieId != 454)
			return false;
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		changeQuestStep(env, 1, 2, false);
		return true;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 205229) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 205229) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else if (env.getDialog() == DialogAction.SETPRO1) {
                    if (!player.getPortalCooldownList().isPortalUseDisabled(300230000)) {
                        WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300230000);
                        InstanceService.registerPlayerWithInstance(newInstance, player);
                        TeleportService2.teleportTo(player, 300230000, newInstance.getInstanceId(), 244.98566f, 244.14162f,
                                189.52058f, (byte) 30);
                        changeQuestStep(env, 0, 1, false); // 1
                    } else
                        PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_STATE);

                    return closeDialogWindow(env);
				}
			}
			else if (targetId == 700939) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					if (var == 2) {
						return sendQuestDialog(env, 1693);
					}
				}
				else if (env.getDialog() == DialogAction.SETPRO3) {
					return defaultCloseDialog(env, 2, 3);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205229) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onAtDistanceEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && (qs.getStatus() == QuestStatus.START || qs.getStatus() == QuestStatus.COMPLETE)) {
			if (!player.getEffectController().hasAbnormalEffect(19288)) {
				SkillEngine.getInstance().applyEffectDirectly(19288, player, player, 0);
				return true;
			}
		}
		return false;
	}
}
