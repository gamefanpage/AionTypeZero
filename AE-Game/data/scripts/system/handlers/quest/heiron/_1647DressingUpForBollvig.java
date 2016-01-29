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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Balthazar
 * @reworked vlog
 */
public class _1647DressingUpForBollvig extends QuestHandler {

	private final static int questId = 1647;

	public _1647DressingUpForBollvig() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(790019).addOnQuestStart(questId);
		qe.registerQuestNpc(790019).addOnTalkEvent(questId);
		qe.registerQuestNpc(700272).addOnTalkEvent(questId);
		qe.registerOnMovieEndQuest(199, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 790019) { // Zetus
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					default: {
						return sendQuestStartDialog(env, 182201783, 1);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 700272) { // Suspicious Stone Statue
				if (dialog == DialogAction.USE_OBJECT) {
					// Wearing Stenon Blouse and Stenon Skirt
					if (!player.getEquipment().getEquippedItemsByItemId(110100150).isEmpty()
						&& !player.getEquipment().getEquippedItemsByItemId(113100144).isEmpty()) {
						// Having Myanee's Flute
						if (player.getInventory().getItemCountByItemId(182201783) > 0) {
							playQuestMovie(env, 199);
							return useQuestObject(env, 0, 0, true, false); // reward
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 790019) { // Zetus
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 10002);
					}
					default: {
						return sendQuestEndDialog(env);
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onMovieEndEvent(QuestEnv env, int movieId) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (movieId == 199) {
				QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 204635, player.getX(), player.getY(), player.getZ(), (byte) 0);
				QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 204635, player.getX() + 2 , player.getY() - 2, player.getZ(), (byte) 0);
				QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 204635, player.getX() - 2, player.getY() + 2, player.getZ(), (byte) 0);
				return true;
			}
		}
		return false;
	}

}
