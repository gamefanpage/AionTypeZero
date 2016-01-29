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
package quest.sanctum;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Put the Inactivated Surkana inside the Balaur Material Converter (730212). Talk with Shoshinerk (798357).
 *
 * @author Bobobear
 */

public class _3920TheSecretOfSurkana extends QuestHandler {
	private final static int questId = 3920;

	public _3920TheSecretOfSurkana() {
		super(questId);
	}

	@Override
	public void register() {
	    qe.registerQuestNpc(798357).addOnQuestStart(questId); //Shoshinerk
		qe.registerQuestNpc(798357).addOnTalkEvent(questId); //Shoshinerk
		qe.registerQuestNpc(730212).addOnTalkEvent(questId); //Balaur Material Converter
		qe.registerQuestItem(182206073, questId);
		qe.registerQuestItem(182206074, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798357) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1011);
					}
					case QUEST_ACCEPT_1: {
						giveQuestItem(env, 182206073, 1);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 730212: { // Balaur Material Converter
					switch (dialog) {
						case USE_OBJECT: {
							if ((var == 0) && player.getInventory().getItemCountByItemId(182206073) > 0) {
								return useQuestObject(env, 0, 1, true, 0, 182206074, 1, 182206073, 1, 0, false);
							}
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798357) { // Shoshinerk
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
