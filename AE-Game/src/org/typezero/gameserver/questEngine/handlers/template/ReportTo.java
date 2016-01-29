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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author MrPoke Like: Sleeping on the Job quest.
 * @modified Rolandas
 */
public class ReportTo extends QuestHandler {

	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final int itemId;

	/**
	 * @param id
	 * @param startNpcIds
	 * @param endNpcIds
	 * @param itemId2
	 */
	public ReportTo(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, int itemId) {
		super(questId);
		startNpcs.addAll(startNpcIds);
		startNpcs.remove(0);
		if (endNpcIds != null) {
			endNpcs.addAll(endNpcIds);
			endNpcs.remove(0);
		}
		this.itemId = itemId;
	}

	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		iterator = endNpcs.iterator();
		while (iterator.hasNext()) {
			int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1011);
					}
					case QUEST_ACCEPT_1:
					case QUEST_ACCEPT_SIMPLE: {
						if (itemId != 0) {
							if (giveQuestItem(env, itemId, 1)) {
								return sendQuestStartDialog(env);
							}
							return false;
						}
						else {
							return sendQuestStartDialog(env);
						}
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (startNpcs.contains(targetId)) {
				if (dialog == DialogAction.FINISH_DIALOG) {
					return sendQuestSelectionDialog(env);
				}
			}
			else if (endNpcs.contains(targetId)) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_QUEST_REWARD: {
						if (itemId != 0) {
							if (player.getInventory().getItemCountByItemId(itemId) < 1) {
								return sendQuestSelectionDialog(env);
							}
						}
						removeQuestItem(env, itemId, 1);
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
