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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Xitanium
 */
public class _1414OperationWindmill extends QuestHandler {

	private final static int questId = 1414;

	public _1414OperationWindmill() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203989).addOnQuestStart(questId); // Tumblusen
		qe.registerQuestNpc(203989).addOnTalkEvent(questId);
		qe.registerQuestNpc(700175).addOnTalkEvent(questId); // Old Gear
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 203989) // Tumblusen
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else if (env.getDialogId() == DialogAction.QUEST_ACCEPT_1.id()) {
					if (giveQuestItem(env, 182201349, 1))
						return sendQuestStartDialog(env);
					else
						return true;
				}
				else
					return sendQuestStartDialog(env);
			}

		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			return sendQuestEndDialog(env);
		}

		else if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			switch (targetId) {
				case 700175: { // Old Gear
					if (qs.getQuestVarById(0) == 0 && env.getDialog() == DialogAction.USE_OBJECT) {
						qs.setQuestVar(1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						removeQuestItem(env, 182201349, 1);
						return true;
					}
				}
			}
		}
		return false;
	}
}
