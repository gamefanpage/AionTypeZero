/**
 * This file is part of aion-engine <aion-engine.com>
 *
 *  aion-engine is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  aion-engine is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser Public License
 *  along with aion-engine.  If not, see <http://www.gnu.org/licenses/>.
 */

package quest.altgard;

import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author HellBoy
 */
public class _2230AFriendlyWager extends QuestHandler {

	private final static int questId = 2230;

	public _2230AFriendlyWager() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203621).addOnQuestStart(questId);
		qe.registerQuestNpc(203621).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		QuestState qs = env.getPlayer().getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (env.getTargetId() == 203621) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case ASK_QUEST_ACCEPT:
						return sendQuestDialog(env, 4);
					case QUEST_ACCEPT_1:
						return sendQuestDialog(env, 1003);
					case QUEST_REFUSE_1:
						return sendQuestDialog(env, 1004);
					case SETPRO1:
						if (QuestService.startQuest(env)) {
							QuestService.questTimerStart(env, 1800);
							return true;
						}
						else
							return false;
				}
			}
		}
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.START) {
			if (env.getTargetId() == 203621) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 2375);
					case CHECK_USER_HAS_QUEST_ITEM:
						if (var == 0) {
							if (QuestService.collectItemCheck(env, true)) {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								QuestService.questTimerEnd(env);
								return sendQuestDialog(env, 5);
							}
							else
								return sendQuestDialog(env, 2716);
						}
				}
			}
		}
		return sendQuestRewardDialog(env, 203621, 0);
	}
}
