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

package quest.pandaemonium;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _4907LepharistsiElysea extends QuestHandler {

	private final static int questId = 4907;

	public _4907LepharistsiElysea()
    {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204208).addOnQuestStart(questId);
		qe.registerQuestNpc(204208).addOnTalkEvent(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestItem(700511,questId);

	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204208) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
                    return sendQuestDialog(env, 4762);
                if (env.getDialog() == DialogAction.ASK_QUEST_ACCEPT)
                    return sendQuestDialog(env, 4);
                if (env.getDialog() == DialogAction.QUEST_REFUSE)
                    return sendQuestDialog(env, 1004);
                if (env.getDialog() == DialogAction.QUEST_ACCEPT_1)
                    return sendQuestStartDialog(env);
            }

        }
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 204208: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 2)
								return sendQuestDialog(env, 1352);
						case SETPRO1:
                            return sendQuestDialog(env, 1352);
                        case CHECK_USER_HAS_QUEST_ITEM:
                            if (player.getInventory().getItemCountByItemId(182207079) >= 1 && player.getInventory().getItemCountByItemId(182207080) >= 1 && player.getInventory().getItemCountByItemId(182207081) >= 1) {
                                qs.setStatus(QuestStatus.REWARD);
                                updateQuestStatus(env);
                                removeQuestItem(env, 182207079, 1);
                                removeQuestItem(env, 182207080, 1);
                                removeQuestItem(env, 182207081, 1);
                                return sendQuestDialog(env, 5);
                            }
                            else
                                return sendQuestDialog(env, 10001);
					}
					break;
				}

			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204208) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.getWorldId() == 310050000 && qs.getQuestVarById(0) == 0) {
                qs.setQuestVar(1);
                updateQuestStatus(env);
            }
        }
        return false;
    }


}
