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

package quest.sanctum;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapType;

/**
 * @author xaerolt, Rolandas
 */
public class _1926SecretLibraryAccess extends QuestHandler {

	private final static int questId = 1926;
	private final static int[] npc_ids = { 203894, 203701 };// 203894 - Latri(start and finish), 203098 - Spatalos(for
																													// recomendation)

	public _1926SecretLibraryAccess() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203894).addOnQuestStart(questId);
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}

	// self explanatory
	private boolean AreVerteronQuestsFinished(Player player) {
		QuestState qs = player.getQuestStateList().getQuestState(1020);// last quest in Verteron state
		return ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE)) ? false
			: true;
	}

    private boolean AreVerteronQuestsFinishedPilot(Player player) {
        QuestState qs = player.getQuestStateList().getQuestState(14016);// last quest in Verteron state
        return ((qs == null) || (qs.getStatus() != QuestStatus.COMPLETE && qs.getStatus() != QuestStatus.NONE)) ? false
                : true;
    }


    @Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (targetId == 203894) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs.getStatus() == QuestStatus.REWARD && qs.getQuestVarById(0) == 0
				|| qs.getStatus() == QuestStatus.COMPLETE) {
				if (env.getDialog() == DialogAction.USE_OBJECT && qs.getStatus() == QuestStatus.REWARD)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECTED_QUEST_NOREWARD.id()) {
					removeQuestItem(env, 182206022, 1);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
					return sendQuestEndDialog(env);
				}
				ThreadPoolManager.getInstance().schedule(new Runnable() {

					@Override
					public void run() {
						TeleportService2.teleportTo(player, WorldMapType.SANCTUM.getId(), 2032.9f, 1473.1f, 592.2f, (byte) 195);
					}
				}, 3000);
			}
		}
		else if (targetId == 203701) {
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					if (AreVerteronQuestsFinished(player) && player.getPlayerClass() != PlayerClass.RIDER) {
						return sendQuestDialog(env, 1011);
					}

                    if (AreVerteronQuestsFinishedPilot(player) && player.getPlayerClass() == PlayerClass.RIDER) {
                        return sendQuestDialog(env, 1011);
                    }

					else
						return sendQuestDialog(env, 1097);
				}
				else if (env.getDialogId() == DialogAction.SET_SUCCEED.id()) {
					if (giveQuestItem(env, 182206022, 1)) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		return false;
	}
}
