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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
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

/**
 * Find the place marked on the map (182201728).
 *
 * @author Balthazar
 * @reworked vlog
 */
public class _1561TheMisersMap extends QuestHandler {

	private final static int questId = 1561;

	public _1561TheMisersMap() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(700188).addOnTalkEvent(questId);
		qe.registerQuestItem(182201728, questId);
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		env.setQuestId(questId);
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000,
				0, 0), true);
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
						1, 0), true);
					removeQuestItem(env, id, 1);
					QuestService.startQuest(env);
					//sendQuestDialog(env, 4);
				}
			}, 3000);
			return HandlerResult.SUCCESS;
		}
		return HandlerResult.UNKNOWN;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 700188) { // Jewel Box
				if (var == 0) {
					if (env.getDialog() == DialogAction.QUEST_SELECT || env.getDialog() == DialogAction.USE_OBJECT) {
						return sendQuestDialog(env, 2375);
					}
					else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
						return defaultCloseDialog(env, 0, 0, true, true);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 700188) { // Jewel Box
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
