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

package quest.brusthonin;

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
 * @author Hellboy Aion4Free
 * @modified Gigi
 */
public class _2092GravesoftheRedSkyLegion extends QuestHandler {

	private final static int questId = 2092;
	private final static int[] npc_ids = { 205150, 205188, 700394, 205190, 205208, 205214, 205213, 205212, 205210, 205209 };

	public _2092GravesoftheRedSkyLegion() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(214402).addOnKillEvent(questId);
		qe.registerQuestNpc(214403).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 2091, true);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() != QuestStatus.START)
			return false;
		switch (targetId) {
			case 214402:
			case 214403:
				if (var >= 6 && var < 20) {
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return true;
				}
				else if (var == 20) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205150) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 205150) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SELECT_ACTION_1012:
					playQuestMovie(env, 395);
					break;
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205188) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 1693);
				case SETPRO3:
					if (var == 2) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205190) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3)
						return sendQuestDialog(env, 2034);
					if (var == 4)
						return sendQuestDialog(env, 2375);
				case SETPRO4:
					if (var == 3) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case SETPRO5:
					if (var == 4) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case CHECK_USER_HAS_QUEST_ITEM:
					if (var == 4) {
						if (QuestService.collectItemCheck(env, true)) {
							if (!giveQuestItem(env, 182209009, 1))
								return true;
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10000);
						}
						else
							return sendQuestDialog(env, 10001);
					}
			}
		}
		else if (targetId == 205208) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2717);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205209) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2802);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205210) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2887);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205212) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2972);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205213) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 3058);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205214) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 3143);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						removeQuestItem(env, 182209009, 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 700394) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					return useQuestObject(env, 1, 2, false, 0); // 2
			}
		}
		return false;
	}
}
