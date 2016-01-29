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

package quest.inggison;

import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;


/**
 * @author Cheatkiller
 *
 */
public class _11076ProofOfTalent extends QuestHandler {

	private final static int questId = 11076;

	public _11076ProofOfTalent() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(799025).addOnQuestStart(questId);
		qe.registerQuestNpc(799084).addOnTalkEvent(questId);
		qe.registerQuestNpc(799025).addOnTalkEvent(questId);
		qe.registerOnEnterWindStream(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799025) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799084) {
				if (dialog == DialogAction.QUEST_SELECT) {
					if(qs.getQuestVarById(0) == 3)
						return sendQuestDialog(env, 2034);
				}
				else if (dialog == DialogAction.SETPRO4) {
					TeleportService2.teleportTo(player, 210050000, 1338.6f, 279.6f, 590, (byte) 80, TeleportAnimation.BEAM_ANIMATION);
					return defaultCloseDialog(env, 3, 4, true, false);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 799025) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWindStreamEvent(QuestEnv env, int teleportId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if(player.getWorldId() == 210050000) {
				if(teleportId == 152001)
					changeQuestStep(env, 0, 1, false);
				else if(teleportId == 153001)
					changeQuestStep(env, 1, 2, false);
				else if(teleportId == 154001)
					changeQuestStep(env, 2, 3, false);
				return true;
			}
		}
		return false;
	}
}

