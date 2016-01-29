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

package quest.beluslan;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Rhys2002
 */
public class _2056ThawingKurngalfberg extends QuestHandler {

	private final static int questId = 2056;
	private final static int[] npc_ids = { 204753, 790016, 730036, 279000 };

	public _2056ThawingKurngalfberg() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestItem(182204313, questId);
		qe.registerQuestItem(182204314, questId);
		qe.registerQuestItem(182204315, questId);
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
		return defaultOnLvlUpEvent(env, 2500, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204753) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					int[] questItems = { 182204313, 182204314, 182204315 };
					return sendQuestEndDialog(env, questItems);
				}
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204753) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
					else if (var == 1)
						return sendQuestDialog(env, 2375);
				case SELECT_ACTION_1012:
					playQuestMovie(env, 242);
					break;
				case SELECT_ACTION_2376:
					if (QuestService.collectItemCheck(env, false))
						return sendQuestDialog(env, 2376);
					else
						return sendQuestDialog(env, 2461);
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
				case SETPRO5:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 790016) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 2034);
				case SELECT_ACTION_2035:
					if (var == 1 && player.getInventory().getItemCountByItemId(182204315) != 1) {
						if (giveQuestItem(env, 182204315, 1))
							return sendQuestDialog(env, 2035);
						else
							return true;
					}
					else
						return sendQuestDialog(env, 2120);
			}
		}
		else if (targetId == 730036) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SELECT_ACTION_1353:
					if (var == 1 && player.getInventory().getItemCountByItemId(182204313) != 1) {
						if (giveQuestItem(env, 182204313, 1))
							return sendQuestDialog(env, 1353);
						else
							return true;
					}
					else
						return sendQuestDialog(env, 1438);
			}
		}
		else if (targetId == 279000) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1693);
				case SELECT_ACTION_1694:
					if (var == 1 && player.getInventory().getItemCountByItemId(182204314) != 1) {
						if (giveQuestItem(env, 182204314, 1))
							return sendQuestDialog(env, 1694);
						else
							return true;
					}
					else
						return sendQuestDialog(env, 1779);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (!player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2056")))
			return HandlerResult.FAILED;

		if (id != 182204313 && qs.getQuestVarById(0) == 2 || id != 182204314 && qs.getQuestVarById(0) == 3
			|| id != 182204315 && qs.getQuestVarById(0) == 4)
			return HandlerResult.UNKNOWN;

		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 2000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				if (qs.getQuestVarById(0) == 2) {
					playQuestMovie(env, 243);
					removeQuestItem(env, id, 1);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
				}
				else if (qs.getQuestVarById(0) == 3) {
					playQuestMovie(env, 244);
					removeQuestItem(env, id, 1);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
				}
				else if (qs.getQuestVarById(0) == 4 && qs.getStatus() != QuestStatus.COMPLETE
					&& qs.getStatus() != QuestStatus.NONE) {
					removeQuestItem(env, id, 1);
					playQuestMovie(env, 245);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
				}
			}
		}, 2000);
		return HandlerResult.SUCCESS;
	}
}
