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

package quest;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * This is a template for everyone, who will write a new quest handler<br>
 * It's only example. All IDs should be changed to the from your quest<br>
 * And use only needed events<br>
 * The main things are the structure and using default methods from the QuestHandler class<br>
 * - First the quest status<br>
 * - Then the target ID<br>
 * - Then DialogAction
 *
 * @author vlog
 */
public class _9999QuestHandlerTemplate extends QuestHandler {

	private static final int questId = 9999;

	public _9999QuestHandlerTemplate() {
		super(questId);
	}

	@Override
	public void register() {
		// register needed events here
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		// If this is a mission, the qs should be != null and you will not need this
		if (qs == null || qs.canRepeat()) {
			if (targetId == 000000) { // Viktor Logwin
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011); // can be different
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 111111: { // Oliver
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							}
							else if (var == 4) {
								return sendQuestDialog(env, 1693);
							}
							else if (var == 5) {
								return sendQuestDialog(env, 2034);
							}
						}
						case SELECT_ACTION_1353: {
							playQuestMovie(env, 0);
							return sendQuestDialog(env, 1353);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 4, 5); // 5
						}
						case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 5, 6, true, 2375, 10001);
						}
						case FINISH_DIALOG: {
							return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 000000) { // Viktor Logwin
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 20001);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 010101, 1, 3); // 1 - 3
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016"))) { // example zone
					return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, false));
			}
		}
		return HandlerResult.FAILED;
	}
}
