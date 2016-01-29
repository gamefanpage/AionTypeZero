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
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;


/**
 * @author zhkchi
 *
 */
public class _28300FloatingDoom  extends QuestHandler {

	private final static int questId = 28300;

	public _28300FloatingDoom() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804719).addOnQuestStart(questId);
		qe.registerQuestNpc(804719).addOnTalkEvent(questId);
		qe.registerQuestNpc(804821).addOnTalkEvent(questId);
		qe.registerQuestNpc(799530).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ATURAM_SKY_FORTRESS_1_300240000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804719) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (targetId == 804821) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1013:
						return sendQuestDialog(env, 1013);
					case SETPRO1:
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
				}
			}
		}
		else if ((qs.getStatus() == QuestStatus.REWARD)) {
			if (targetId == 799530) {
				switch (env.getDialog()) {
					case USE_OBJECT:
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
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName == ZoneName.get("ATURAM_SKY_FORTRESS_1_300240000")) {
			Player player = env.getPlayer();
			if (player == null)
				return false;
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var == 1) {
					playQuestMovie(env, 467);
					changeQuestStep(env, 1, 1, true);
					return true;
				}
			}
		}
		return false;
	}
}

