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

package quest.gelkmaros;

import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author VladimirZ
 */
public class _21033ExorcisingInfisto extends QuestHandler {

	private final static int questId = 21033;

	public _21033ExorcisingInfisto() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npcs = { 799256, 204734 };
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		qe.registerQuestNpc(799256).addOnQuestStart(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		if (sendQuestNoneDialog(env, 799256, 182207829, 1))
			return true;

		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.START) {
			if (env.getTargetId() == 204734) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1352);
					case SETPRO1:
						return defaultCloseDialog(env, 0, 1, true, false, 182207830, 1, 182207829, 1);
				}
			}
		}
		return sendQuestRewardDialog(env, 799256, 2375);
	}
}
