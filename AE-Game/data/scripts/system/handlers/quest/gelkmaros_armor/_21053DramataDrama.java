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

package quest.gelkmaros_armor;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author zhkchi
 *
 */
public class _21053DramataDrama extends QuestHandler {

	private final static int questId = 21053;

	public _21053DramataDrama() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799320).addOnQuestStart(questId);
		qe.registerQuestNpc(799320).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()){
			if (targetId == 799320) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012: {
						return sendQuestDialog(env, 1012);
					}
					case ASK_QUEST_ACCEPT: {
						return sendQuestDialog(env, 4);
					}
					case QUEST_ACCEPT_1: {
						return sendQuestStartDialog(env);
					}
					case QUEST_REFUSE_1: {
						return sendQuestDialog(env, 1004);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 799320) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_ACTION_2034: {
						return sendQuestDialog(env, 2034);
					}
					case CHECK_USER_HAS_QUEST_ITEM: {
						return checkQuestItems(env, var, var, true, 5, 2716);
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}

				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799320) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
