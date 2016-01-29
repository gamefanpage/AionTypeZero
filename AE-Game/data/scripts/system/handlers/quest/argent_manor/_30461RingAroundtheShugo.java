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

package quest.argent_manor;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 *
 * @author Ritsu
 */
public class _30461RingAroundtheShugo extends QuestHandler
{

	private static final int questId = 30461;

	public _30461RingAroundtheShugo()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(204108).addOnQuestStart(questId);
		qe.registerQuestNpc(204108).addOnTalkEvent(questId);
		qe.registerQuestNpc(799546).addOnTalkEvent(questId);
		qe.registerQuestNpc(799547).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if (targetId == 204108)
			{
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				if (dialog == DialogAction.QUEST_ACCEPT_SIMPLE)
				{
					if (giveQuestItem(env, 182213032, 1))
						return sendQuestStartDialog(env);
					else
						return true;
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			int var = qs.getQuestVarById(0);
			switch (targetId)
			{
				case 799547:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 0)
								return sendQuestDialog(env, 1011);
						}
						case SETPRO1:
						{
							removeQuestItem(env, 182213032, 1);
							return defaultCloseDialog(env, 0, 0, true, false);
						}
					}
				}

			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 799546)
				switch (dialog)
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, 10002);
					}
					case SELECT_QUEST_REWARD:
					{
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
		}
		return false;
	}
}
