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
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author kecimis
 */

public class _4200ASuspiciousCall extends QuestHandler {

	private final static int questId = 4200;
	private final static int[] npc_ids = { 204839, 798332, 700522, 279006, 204286 };

	/*
	 * 204839 - Uikinerk 798332 - Haorunerk 700522 - Haorunerks Bag 279006 - Garkbinerk 204286 - Payrinrinerk
	 */

	public _4200ASuspiciousCall() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204839).addOnQuestStart(questId); // Uikinerk
		qe.registerQuestItem(182209097, questId); // Teleport Scroll
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204839) // Uikinerk
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			return false;
		}

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204286)// Payrinrinerk
			{
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204839)// Uikinerk
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1003);
					case SELECT_ACTION_1011:
						return sendQuestDialog(env, 1011);
					case SETPRO1:
						// Create instance
						WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300100000);
						InstanceService.registerPlayerWithInstance(newInstance, player);
						// teleport to cell in steel rake: 300100000 403.55 508.11 885.77 0
						TeleportService2.teleportTo(player, 300100000, newInstance.getInstanceId(), 403.55f, 508.11f, 885.77f);
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						return true;
				}
			}
			else if (targetId == 798332 && var == 1) // Haorunerk
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:
						playQuestMovie(env, 431);
						break;
					case SETPRO2:
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
				}
			}
			else if (targetId == 700522 && var == 2) // Haorunerks Bag, loc: 401.24 503.19 885.76 119
			{
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						updateQuestStatus(env);
					}
				}, 3000);
				return true;
			}
			else if (targetId == 279006 && var == 3)// Garkbinerk
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 2034);
					case SET_SUCCEED:
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
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

		if (id != 182209097 || qs == null)
			return HandlerResult.UNKNOWN;

		if (qs.getQuestVarById(0) != 2)
			return HandlerResult.FAILED;

		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				removeQuestItem(env, 182209097, 1);
				// teleport location(BlackCloudIsland): 400010000 3419.16 2445.43 2766.54 57
				TeleportService2.teleportTo(player, 400010000, 3419.16f, 2445.43f, 2766.54f, (byte) 57);
				qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
				updateQuestStatus(env);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}
