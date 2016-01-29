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

package quest.theobomos;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Hellboy Aion4Free
 */
public class _1093TheCalydonRuins extends QuestHandler {

	private final static int questId = 1093;
	private final static int[] npc_ids = { 798155, 203784, 798176, 700391, 700392, 700393, 798212 };

	public _1093TheCalydonRuins() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182208013, questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1091, true);
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
			if (targetId == 798155) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 798155) {
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
			}
		}
		else if (targetId == 203784) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SETPRO2:
					if (var == 1) {
						if (!giveQuestItem(env, 182208013, 1))
							return true;
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 798176) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 1693);
				case SELECT_ACTION_1694:
					playQuestMovie(env, 365);
					break;
				case SETPRO3:
					if (var == 2) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 798212) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 7)
						return sendQuestDialog(env, 3398);
					if (var == 8)
						return sendQuestDialog(env, 3739);
				case SETPRO4:
					if (var == 7) {
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case SET_SUCCEED:
					if (var == 8) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case CHECK_USER_HAS_QUEST_ITEM:
					if (var == 7) {
						if (QuestService.collectItemCheck(env, true)) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							return sendQuestDialog(env, 3739);
						}
						else
							return sendQuestDialog(env, 10001);
					}
			}
		}
		else if (targetId == 700391) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 4) {
						if (!giveQuestItem(env, 182208014, 1))
							return false;
						qs.setQuestVarById(0, 5);
						updateQuestStatus(env);
					}
			}
		}
		else if (targetId == 700392) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 5) {
						if (!giveQuestItem(env, 182208015, 1))
							return false ;
						qs.setQuestVarById(0, 6);
						updateQuestStatus(env);
					}
			}
		}
		else if (targetId == 700393) {
			switch (env.getDialog()) {
				case USE_OBJECT:
					if (var == 6) {
						if (!giveQuestItem(env, 182208016, 1)) {
							return false;
						}
						qs.setQuestVarById(0, 7);
						updateQuestStatus(env);
					}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();

		if (id != 182208013)
			return HandlerResult.UNKNOWN;

		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getQuestVarById(0) != 3)
			return HandlerResult.UNKNOWN;

		qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
		updateQuestStatus(env);
		return HandlerResult.SUCCESS;
	}
}
