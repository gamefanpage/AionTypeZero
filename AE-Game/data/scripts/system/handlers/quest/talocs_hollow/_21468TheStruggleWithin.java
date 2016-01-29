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

package quest.talocs_hollow;

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
public class _21468TheStruggleWithin extends QuestHandler {

	private final static int questId = 21468;

	public _21468TheStruggleWithin() {
		super(questId);
	}

	public void register() {
		qe.registerQuestSkill(9832, questId);
		qe.registerQuestSkill(9833, questId);
		qe.registerQuestSkill(9834, questId);
		qe.registerQuestNpc(799526).addOnQuestStart(questId);
		qe.registerQuestNpc(799503).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799526) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799503) {
				switch (dialog) {
					case USE_OBJECT: {
						return sendQuestDialog(env, 10002);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onUseSkillEvent(QuestEnv env, int skillUsedId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var1 = qs.getQuestVarById(1);
			int var2 = qs.getQuestVarById(2);
			int var3 = qs.getQuestVarById(3);
			if(skillUsedId == 9832) {
				if(var1 < 10) {
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
			else if(skillUsedId == 9833) {
				if(var2 < 5) {
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
			else if(skillUsedId == 9834) {
				if(var3 < 3) {
					qs.setQuestVarById(3, var3 + 1);
					updateQuestStatus(env);
					reward(qs, env);
				}
			}
		}
		return false;
	}

	private void reward(QuestState qs, QuestEnv env) {
		if(qs.getQuestVarById(1) == 10 && qs.getQuestVarById(2) == 5 && qs.getQuestVarById(3) == 3) {
			qs.setQuestVarById(1, 0);
			qs.setQuestVarById(2, 0);
			qs.setQuestVarById(3, 0);
			qs.setStatus(QuestStatus.REWARD);
			updateQuestStatus(env);
		}
	}
}
