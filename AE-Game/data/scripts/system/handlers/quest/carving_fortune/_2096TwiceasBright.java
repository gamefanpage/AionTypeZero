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

package quest.carving_fortune;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Go to Pandaemonium and talk with Cavalorn (204206).<br>
 * Meet with Kasir (204207) in the Hidden Library.<br>
 * Go to Ishalgen and talk with Munin (203550).
 *
 * @author Manu72
 * @reworked vlog
 */
public class _2096TwiceasBright extends QuestHandler {

	private final static int questId = 2096;

	public _2096TwiceasBright() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204206).addOnTalkEvent(questId);
		qe.registerQuestNpc(204207).addOnTalkEvent(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 204206: { // Cavalorn
					if (dialog == DialogAction.QUEST_SELECT && var == 0) {
						return sendQuestDialog(env, 1011);
					}
					else if (dialog == DialogAction.SETPRO1) {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					break;
				}
				case 204207: { // Kasir
					if (dialog == DialogAction.QUEST_SELECT && var == 1) {
						return sendQuestDialog(env, 1352);
					}
					else if (dialog == DialogAction.SETPRO2) {
						return defaultCloseDialog(env, 1, 2); // 2
					}
					break;
				}
				case 203550: { // Munin
					if (dialog == DialogAction.QUEST_SELECT && var == 2) {
						changeQuestStep(env, 2, 2, true); // reward
						return sendQuestDialog(env, 1693);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203550) { // Munin
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 2007, 2022, 2041, 2094, 2061, 2076 };
		return defaultOnLvlUpEvent(env, quests, false);
	}
}
