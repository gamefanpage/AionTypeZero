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

package quest.reshanta;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Hilgert
 */

public class _2841CleansingtheAsteriaChamber extends QuestHandler {

	private final static int questId = 2841;

	public _2841CleansingtheAsteriaChamber() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(271068).addOnQuestStart(questId);
		qe.registerQuestNpc(271068).addOnTalkEvent(questId);
		qe.registerQuestNpc(214762).addOnKillEvent(questId);
		qe.registerQuestNpc(214755).addOnKillEvent(questId);
		qe.registerQuestNpc(214752).addOnKillEvent(questId);
		qe.registerQuestNpc(214754).addOnKillEvent(questId);
		qe.registerQuestNpc(215441).addOnKillEvent(questId);
		qe.registerQuestNpc(214758).addOnKillEvent(questId);
		qe.registerQuestNpc(214766).addOnKillEvent(questId);
		qe.registerQuestNpc(214753).addOnKillEvent(questId);
		qe.registerQuestNpc(215444).addOnKillEvent(questId);
		qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.getStatus() == QuestStatus.COMPLETE) {
			if (targetId == 271068) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 271068)
				return true;
		}
		else if (qs.getStatus() == QuestStatus.REWARD && targetId == 271068) {
			qs.setQuestVarById(0, 0);
			updateQuestStatus(env);
			return sendQuestEndDialog(env);
		}
		return false;
	}

	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			if (player.getCommonData().getPosition().getMapId() == 300050000) {
				if (qs.getQuestVarById(0) < 43) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return true;
				}
				else if (qs.getQuestVarById(0) == 43 || qs.getQuestVarById(0) > 43) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}
}
