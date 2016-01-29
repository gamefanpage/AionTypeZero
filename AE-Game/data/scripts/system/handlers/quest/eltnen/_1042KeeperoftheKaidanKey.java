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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Rhys2002 edited by xaerolt
 * @reworked vlog
 */
public class _1042KeeperoftheKaidanKey extends QuestHandler {

	private final static int questId = 1042;

	public _1042KeeperoftheKaidanKey() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npc_ids = { 203989, 203901, 730342 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestItem(182201026, questId);
		qe.addHandlerSideQuestDrop(questId, 730342, 182201026, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 212025, 182201026, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 212029, 182201026, 1, 100);
		qe.addHandlerSideQuestDrop(questId, 212033, 182201026, 1, 100);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203989) { // Tumblusen
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
					}
					case SELECT_ACTION_1012: {
						playQuestMovie(env, 185);
						return sendQuestDialog(env, 1012);
					}
					case SETPRO1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 730342) { // Strong Document Box
				if (dialog == DialogAction.USE_OBJECT) {
					if (var == 1) {
						return true; // loot;
					}
				}
			}
			else if (targetId == 203901) { // Telemachus
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					}
					case CHECK_USER_HAS_QUEST_ITEM: {
						return checkQuestItems(env, 2, 2, true, 5, 1438); // reward
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) { // Telemachus
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false)); // 2
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 1040);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1300, 1040 };
		return defaultOnLvlUpEvent(env, quests, true);
	}
}
