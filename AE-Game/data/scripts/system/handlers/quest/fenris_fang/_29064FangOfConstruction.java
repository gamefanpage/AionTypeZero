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

package quest.fenris_fang;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Cheatkiller
 */
public class _29064FangOfConstruction extends QuestHandler {

	private final static int questId = 29064;

	public _29064FangOfConstruction() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 798452, 204075, 204053 };
		qe.registerQuestNpc(204053).addOnQuestStart(questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204053) {
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798452:
					switch (dialog) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 1352);
						case SETPRO1:
						if (!giveQuestItem(env, 152209288, 1))
							return true;
						qs.setQuestVarById(0, 1);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
					break;
				case 204075:
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1 && player.getInventory().getItemCountByItemId(182213239) >= 1 && player.getInventory().getItemCountByItemId(186000085) >= 1) {
								return sendQuestDialog(env, 2375);
							}
						}
						case CHECK_USER_HAS_QUEST_ITEM_SIMPLE: {
							if (player.getInventory().getItemCountByItemId(182213239) >= 1 && player.getInventory().getItemCountByItemId(186000085) >= 1) {
								removeQuestItem(env, 182213239, 1);
								removeQuestItem(env, 186000085, 1);
								return defaultCloseDialog(env, 1, 1, true, false);
							}
						}
				  }
					break;
			 }
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204075) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
