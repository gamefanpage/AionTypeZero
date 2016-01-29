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
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Dune11
 */
public class _2094TheSecretofAdmaStronghold extends QuestHandler {

	private final static int questId = 2094;
	private final static int[] npc_ids = { 205150, 205192, 205155, 730164, 205191, 204057 };

	public _2094TheSecretofAdmaStronghold() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(214700).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = { 2092, 2093, 2054 };
		return defaultOnZoneMissionEndEvent(env, quests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2091, 2092, 2093, 2054 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		final Npc npc = (Npc) env.getVisibleObject();

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204057)
				return sendQuestEndDialog(env);
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
					return true;
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 205192) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
					else if (var == 2)
						return sendQuestDialog(env, 1693);
					else if (var == 3)
						return sendQuestDialog(env, 2034);
					return true;
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case CHECK_USER_HAS_QUEST_ITEM:
					if (var == 2) {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 10001);
						}
						else
							return sendQuestDialog(env, 10008);
					}
				case SETPRO4:
					if (var == 3) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
					}
			}
		}
		else if (targetId == 205155) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 5)
						return sendQuestDialog(env, 2716);
				case SETPRO6:
					if (var == 5) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 730164) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 6) {
						QuestService.addNewSpawn(220050000, 1, 205191, npc.getX(), npc.getY(), npc.getZ(), (byte) 0);
						npc.getController().scheduleRespawn();
						npc.getController().onDelete();
						return true;
					}
			}
		}
		else if (targetId == 205191) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 6) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
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

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId == 214700) {
			if (var == 4) {
				qs.setQuestVarById(0, var + 1);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

}
