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

package quest.aturam_sky_fortress;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Luzien
 */
public class _28302DocumentSaved extends QuestHandler {

	private final static int questId = 28302;
	private int[] mobIds = new int[] {700981, 700982, 700983, 700984, 700985};

	public _28302DocumentSaved() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799530).addOnQuestStart(questId);
		qe.registerQuestNpc(799530).addOnTalkEvent(questId);
		qe.registerQuestNpc(730375).addOnTalkEvent(questId);
		for (int id : mobIds) {
			qe.registerQuestNpc(id).addOnKillEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799530) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else if (env.getDialog() == DialogAction.QUEST_ACCEPT_1){
					 playQuestMovie(env, 468);
					 return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 730375) {
				if (var == 5) {
					switch (env.getDialog()) {
						case USE_OBJECT:
							return sendQuestDialog(env, 1352);
						case SET_SUCCEED:
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						default:
							return sendQuestDialog(env, 2716);
					}
				}
			}
			else if (targetId == 799530) {
				return sendQuestDialog(env, 1004);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799530) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					removeQuestItem(env,182212101,1);
					return sendQuestDialog(env, 5);
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
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() != QuestStatus.START) {
			return false;
		}

		int targetId = 0;
		int var = qs.getQuestVarById(0);

		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (var > 4 || targetId == 0)
			return false;

		for (int id : mobIds) {
			if (targetId == id) {
				qs.setQuestVarById(0, var+1);
        updateQuestStatus(env);
        return true;
      }
		}
		return false;
	}
}
