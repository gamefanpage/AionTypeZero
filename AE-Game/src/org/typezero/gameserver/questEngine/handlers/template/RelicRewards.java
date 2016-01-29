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

package org.typezero.gameserver.questEngine.handlers.template;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 * @author Bobobear
 */
public class RelicRewards extends QuestHandler {

	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final int relicVar1;
	private final int relicVar2;
	private final int relicVar3;
	private final int relicVar4;
	private int relicCount;

	/**
	 * @param questId
	 * @param startNpcId
	 * @param relicVar1
	 * @param relicVar2
	 * @param relicVar3
	 * @param relicVar4
	 */
	public RelicRewards(int questId, List<Integer> startNpcIds, int relicVar1, int relicVar2, int relicVar3, int relicVar4, int relicCount) {
		super(questId);
		this.startNpcs.addAll(startNpcIds);
		this.startNpcs.remove(0);
		this.questId = questId;
		this.relicVar1 = relicVar1;
		this.relicVar2 = relicVar2;
		this.relicVar3 = relicVar3;
		this.relicVar4 = relicVar4;
		this.relicCount = relicCount;
	}

	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.contains(targetId)) {
				switch (env.getDialog()) {
					case EXCHANGE_COIN: {
						if (player.getCommonData().getLevel() >= 30) {
							if ((player.getInventory().getItemCountByItemId(relicVar1) > 0)
								|| (player.getInventory().getItemCountByItemId(relicVar2) > 0)
								|| (player.getInventory().getItemCountByItemId(relicVar3) > 0)
								|| (player.getInventory().getItemCountByItemId(relicVar4) > 0)) {
								QuestService.startQuest(env);
								return sendQuestDialog(env, 1011);
							}
							else
								return sendQuestDialog(env, 3398);
						}
						else
							return sendQuestDialog(env, 3398);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (startNpcs.contains(targetId)) {
				if(relicCount == 0) relicCount = 1;
				switch (env.getDialog()) {
					case USE_OBJECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1011:
						if (player.getInventory().getItemCountByItemId(relicVar1) >= relicCount) {
							removeQuestItem(env, relicVar1, relicCount);
							qs.setQuestVar(1);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 5);
						}
						else
							return sendQuestDialog(env, 1009);
					case SELECT_ACTION_1352:
						if (player.getInventory().getItemCountByItemId(relicVar2) >= relicCount) {
							removeQuestItem(env, relicVar2, relicCount);
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 6);
						}
						else
							return sendQuestDialog(env, 1009);
					case SELECT_ACTION_1693:
						if (player.getInventory().getItemCountByItemId(relicVar3) >= relicCount) {
							removeQuestItem(env, relicVar3, relicCount);
							qs.setQuestVar(3);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 7);
						}
						else
							return sendQuestDialog(env, 1009);
					case SELECT_ACTION_2034:
						if (player.getInventory().getItemCountByItemId(relicVar4) >= relicCount) {
							removeQuestItem(env, relicVar4, relicCount);
							qs.setQuestVar(4);
							qs.setStatus(QuestStatus.REWARD);
							qs.setCompleteCount(0);
							updateQuestStatus(env);
							return sendQuestDialog(env, 8);
						}
						else
							return sendQuestDialog(env, 1009);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (startNpcs.contains(targetId)) {
				int var = qs.getQuestVarById(0);
				switch (env.getDialog()) {
					case USE_OBJECT:
						if (var == 1)
							return sendQuestDialog(env, 5);
						else if (var == 2)
							return sendQuestDialog(env, 6);
						else if (var == 3)
							return sendQuestDialog(env, 7);
						else if (var == 4)
							return sendQuestDialog(env, 8);
					case SELECTED_QUEST_NOREWARD:
						QuestService.finishQuest(env, qs.getQuestVars().getQuestVars() - 1);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
				}
			}
		}
		return false;
	}
}
