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

package quest.udas_temple;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;


/**
 * @author Cheatkiller
 *
 */
public class _30111CenterOfTheWeb extends QuestHandler {

	private final static int questId = 30111;

	public _30111CenterOfTheWeb() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(799335).addOnQuestStart(questId);
		qe.registerQuestNpc(799335).addOnTalkEvent(questId);
		qe.registerQuestNpc(206105).addOnAtDistanceEvent(questId);
		qe.registerQuestNpc(215792).addOnKillEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("KINGSPINS_NEST_300160000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799335) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799335) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1352);
				}
				else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
					return defaultCloseDialog(env, 1, 2, true, true);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799335) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 5);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 215792, 1, true, 0);
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName != ZoneName.get("KINGSPINS_NEST_300160000"))
			return false;
		final Player player = env.getPlayer();
		if (player == null)
			return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		if (qs.getQuestVarById(0) == 0)
		{
			qs.setQuestVarById(0, 1);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}

}



