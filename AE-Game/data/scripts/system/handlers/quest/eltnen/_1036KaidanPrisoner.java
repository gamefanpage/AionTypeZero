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

package quest.eltnen;

import org.typezero.gameserver.model.TeleportAnimation;
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
 * @author Rhys2002
 * @Fixed Ritsu
 */
public class _1036KaidanPrisoner extends QuestHandler {

	private final static int questId = 1036;
	private final static int[] npc_ids = { 203904, 204045, 204003, 204004, 204020, 203901 };

	public _1036KaidanPrisoner() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1300, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901){
				removeQuestItem(env, 182201005, 1);
				return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 203904) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204045) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SELECT_ACTION_1354:
					if (var == 1)
						playQuestMovie(env, 32);
					break;
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						TeleportService2.teleportTo(player, 210020000, 1357f, 2566f, 279.6f, (byte) 89, TeleportAnimation.BEAM_ANIMATION);
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204003) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 1693);
					else if (var == 3 && QuestService.collectItemCheck(env, true))
						return sendQuestDialog(env, 2034);
					else
						return sendQuestDialog(env, 2120);
				case SETPRO3:
					if (var == 2) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case SETPRO4:
					if (var == 3) {
						playQuestMovie(env, 50);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204004) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 4)
						return sendQuestDialog(env, 2375);
				case SETPRO5:
					if (var == 4) {
						if (!giveQuestItem(env, 182201004, 1))
							return true;
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204020) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2716);
				case SELECT_ACTION_2717:
					removeQuestItem(env, 182201004, 1);
				case SETPRO5:
					if (var == 5) {
						if (!giveQuestItem(env, 182201005, 1))
							return true;
						qs.setQuestVarById(0, var + 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		return false;
	}
}
