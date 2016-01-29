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

package quest.heiron;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Go to Draupnir Cave in Asmodae and get Blue Balaur Blood (186000035) (2) and Balaur Rainbow Scales (186000036) (5)
 * for Brosia (204601). Go to Brosia to choose your reward.
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _1687TheTigrakiAgreement extends QuestHandler {

	private final static int questId = 1687;
	private int rewardGroup;

	public _1687TheTigrakiAgreement() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204601).addOnQuestStart(questId);
		qe.registerQuestNpc(204601).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 204601) { // Brosia
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (targetId == 204601) { // Brosia
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case CHECK_USER_HAS_QUEST_ITEM: {
						long collect1 = player.getInventory().getItemCountByItemId(186000035);
						long collect2 = player.getInventory().getItemCountByItemId(186000036);
						if (collect1 >= 2 && collect2 >= 5) {
							removeQuestItem(env, 186000035, 2);
							removeQuestItem(env, 186000036, 5);
							return sendQuestDialog(env, 1352); // choose your reward
						}
						else
							return sendQuestDialog(env, 1097);
					}
					case FINISH_DIALOG:
						return defaultCloseDialog(env, var, var);
					case SETPRO10: {
						rewardGroup = 0;
						return defaultCloseDialog(env, var, var, true, true, 0); // reward 1
					}
					case SETPRO20: {
						rewardGroup = 1;
						return defaultCloseDialog(env, var, var, true, true, 1); // reward 2
					}
					case SETPRO30: {
						rewardGroup = 2;
						return defaultCloseDialog(env, var, var, true, true, 2); // reward 3
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204601) { // Brosia
				return sendQuestEndDialog(env, rewardGroup);
			}
		}
		return false;
	}
}
