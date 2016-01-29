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

package quest.pandaemonium;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Cheatkiller
 *
 */
public class _2925AHeartfeltConfession extends QuestHandler {

	private final static int questId = 2925;

	public _2925AHeartfeltConfession() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(204261).addOnQuestStart(questId);
		qe.registerQuestNpc(204261).addOnTalkEvent(questId);
		qe.registerQuestNpc(204235).addOnTalkEvent(questId);
		qe.registerQuestNpc(204127).addOnTalkEvent(questId);
		qe.registerQuestNpc(204193).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204261) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 204235) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 0) {
						if(!player.getEquipment().getEquippedItemsByItemId(110100288).isEmpty())
							return sendQuestDialog(env, 1011);
						else
							return sendQuestDialog(env, 1097);
					}
					else if(qs.getQuestVarById(0) == 4) {
						return sendQuestDialog(env, 2375);
					}
				}
				else if (dialog == DialogAction.SETPRO1) {
					return defaultCloseDialog(env, 0, 1);
				}
				else if (dialog == DialogAction.SETPRO5) {
					return defaultCloseDialog(env, 4, 5);
				}
			}
			else if (targetId == 204261) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 1)
						return sendQuestDialog(env, 1352);
					else if(qs.getQuestVarById(0) == 5)
						return sendQuestDialog(env, 2716);
				}
				else if (dialog == DialogAction.SETPRO2) {
					removeQuestItem(env, 110100288, 1);
					return defaultCloseDialog(env, 1, 2);
				}
				else if (dialog == DialogAction.SELECT_ACTION_2717) {
					changeQuestStep(env, 5, 5, true);
					return sendQuestDialog(env, 10002);
				}
			}
			else if (targetId == 204127) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 2)
						return sendQuestDialog(env, 1693);
				}
				else if (dialog == DialogAction.SETPRO3) {
					return defaultCloseDialog(env, 2, 3);
				}
			}
			else if (targetId == 204193) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 3)
						return sendQuestDialog(env, 2034);
				}
				else if (dialog == DialogAction.SETPRO4) {
					return defaultCloseDialog(env, 3, 4);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204261) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
