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


package quest.theobomos;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Collect Bloodwing Meat and lure Vison (798214). Take Bloodwing Meat to Tityus (798191).
 *
 * @author Balthazar
 * @reworked vlog
 */

public class _3092VisonTheDrakie extends QuestHandler {

	private final static int questId = 3092;

	public _3092VisonTheDrakie() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798191).addOnQuestStart(questId);
		qe.registerQuestNpc(798191).addOnTalkEvent(questId);
		qe.registerQuestNpc(798214).addOnTalkEvent(questId);
		qe.registerOnLogOut(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798191) { // Tityus
				switch (env.getDialog()) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 1011);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798214: { // Vison
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 0) {
								long itemCount = player.getInventory().getItemCountByItemId(182208066);
								if (itemCount >= 25) {
									return sendQuestDialog(env, 1352);
								}
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
				}
				case 798191: {
					if (env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 2375);
					if(env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
							return checkQuestItems(env, 1, 2, true, 5, 2716); // reward
					}
					if (env.getDialogId() == DialogAction.FINISH_DIALOG.id())
						return defaultCloseDialog(env, 1, 1);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798191) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
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
