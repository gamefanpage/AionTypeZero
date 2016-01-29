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

package quest.elementis_forest;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 *
 * @author Ritsu
 */
public class _30405Cursebreakers extends QuestHandler
{

	private static final int questId = 30405;

	public _30405Cursebreakers()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(205492).addOnQuestStart(questId);
		qe.registerQuestNpc(205492).addOnTalkEvent(questId);
		qe.registerQuestNpc(282203).addOnTalkEvent(questId);
		qe.registerQuestNpc(282204).addOnTalkEvent(questId);
		qe.registerQuestNpc(217249).addOnKillEvent(questId);
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
			if (targetId == 205492)
			{
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START)
		{
			int var = qs.getQuestVarById(0);
			switch (targetId)
			{
				case 282203:
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
							final Npc npc = (Npc)env.getVisibleObject();
							npc.getController().scheduleRespawn();
							npc.getController().onDelete();
							QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 217249, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
							return defaultCloseDialog(env, 0, 1);
						}
					}
				}

				case 282204:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 2)
								return sendQuestDialog(env, 1693);
						}
						case SET_SUCCEED:
						{
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return closeDialogWindow(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 205492)
				switch (dialog)
				{
					case USE_OBJECT:
					{
						return sendQuestDialog(env, 10002);
					}
					default:
					{
						return sendQuestEndDialog(env);
					}
				}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch (targetId)
		{
			case 217249:
				if (qs.getQuestVarById(0) == 1)
				{
					Npc npc = (Npc) env.getVisibleObject();
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 282204, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
				}
		}
		return false;
	}
}
