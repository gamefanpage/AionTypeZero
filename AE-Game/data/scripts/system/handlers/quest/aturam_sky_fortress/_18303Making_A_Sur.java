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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @author zhkchi
 */
public class _18303Making_A_Sur extends QuestHandler {

	private final static int questId = 18303;

	public _18303Making_A_Sur() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799530).addOnQuestStart(questId);
		qe.registerQuestNpc(799530).addOnTalkEvent(questId);
		qe.registerQuestNpc(730390).addOnTalkEvent(questId);
		qe.registerQuestNpc(700980).addOnTalkEvent(questId);
		qe.registerQuestNpc(804820).addOnTalkEvent(questId);
		qe.registerQuestNpc(217382).addOnKillEvent(questId);
		qe.registerQuestNpc(217376).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE){
			if (targetId == 799530) {
					if (env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 4762);
					else if (env.getDialog() == DialogAction.QUEST_ACCEPT_1){
						playQuestMovie(env, 470);
					  return sendQuestStartDialog(env);
					}
					else
						return sendQuestStartDialog(env);
				}
			}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 730390) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case USE_OBJECT:
						return sendQuestDialog(env, 1007);
					case SETPRO1:
						TeleportService2.teleportTo(player, 300240000, 158.88f, 624.42f, 901f, (byte) 20);
						return closeDialogWindow(env);
					default:
						return sendQuestStartDialog(env);
				}
			}
			else if (targetId == 700980) {
				return useQuestObject(env, 2, 3, true, true);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 804820) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default:
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 0)
				return defaultOnKillEvent(env, 217382, 0, 1);
			else
				return defaultOnKillEvent(env, 217376, 1, 2);
		}
		return false;
	}
}
