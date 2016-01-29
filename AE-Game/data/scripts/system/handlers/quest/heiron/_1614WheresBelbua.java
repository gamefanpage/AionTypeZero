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

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * Go to the Mudthorn Experiment Lab and find Belbua (204645). When you're ready to leave, talk to Belbua. Escort Belbua
 * outside the Mudthorn Experiment Lab. Let Phuthollo (204519) know Belbua is free.
 *
 * @author Rhys2002
 * @reworked vlog
 */
public class _1614WheresBelbua extends QuestHandler {

	private final static int questId = 1614;

	public _1614WheresBelbua() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204519).addOnQuestStart(questId);
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(204519).addOnTalkEvent(questId);
		qe.registerQuestNpc(204645).addOnTalkEvent(questId);
		qe.registerAddOnReachTargetEvent(questId);
		qe.registerAddOnLostTargetEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();


        int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204519) { // Phuthollo
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		 if (qs != null && qs.getStatus() == QuestStatus.START)  {
             int var = qs.getQuestVarById(0);
             if (targetId == 204645) // Belbua
          {
                if (dialog == DialogAction.QUEST_SELECT) {
                    if (var == 0)
								return sendQuestDialog(env, 1011);
                   else if (var == 1)
                        return sendQuestDialog(env, 1352);
							}
                  if (dialog == DialogAction.SETPRO1)
                        return defaultCloseDialog(env, 0, 1);
                  if (dialog == DialogAction.SETPRO2)
                  {
                      changeQuestStep(env, 1, 2, false);
                      updateQuestStatus(env);
							return defaultStartFollowEvent(env, (Npc) env.getVisibleObject(), 376f, 529f, 133f, 0, 1);
						}
					}
				}
    	if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204519) { // Phuthollo
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 1) {
				changeQuestStep(env, 1, 0, false);
			}
		}
		return false;
	}

	@Override
	public boolean onNpcReachTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 3, true); // reward
	}

	@Override
	public boolean onNpcLostTargetEvent(QuestEnv env) {
		return defaultFollowEndEvent(env, 2, 1, false); // 0
	}
}
