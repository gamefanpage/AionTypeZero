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

package quest.terath_dredgion;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Ritsu
 *
 */
public class _30600FightOfTheNavigators extends QuestHandler
{
	private final static int questId=30600;
	public _30600FightOfTheNavigators()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(205842).addOnQuestStart(questId); //Ancanus
		qe.registerQuestNpc(205842).addOnTalkEvent(questId);
		qe.registerQuestNpc(800325).addOnTalkEvent(questId);//Hejitor,Jerot
		qe.registerQuestNpc(219256).addOnKillEvent(questId);
		qe.registerQuestNpc(219257).addOnKillEvent(questId);
		qe.registerQuestNpc(219264).addOnKillEvent(questId);

	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		final int targetId=env.getTargetId();

		DialogAction dialog=env.getDialog();
		if(qs == null || qs.getStatus()== QuestStatus.NONE || qs.canRepeat())
		{
			if(targetId==205842)
			{
				switch(dialog)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env,1011);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs!=null && qs.getStatus()==QuestStatus.START)
		{
			int var=qs.getQuestVarById(0);//the station is important
			switch(targetId)
			{
				case 800325:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,1352);
						case SETPRO1:
						{
							return  defaultCloseDialog(env,0,0);
						}
					}
				}
				case 205842:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,2375);
						case SELECT_QUEST_REWARD:
						{
							if(var ==1)
							{
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env,5);
							}
						}

					}
				}
			}
		}
		else if(qs!=null && qs.getStatus()==QuestStatus.REWARD)
		{
			switch(targetId)
			{
				case 205842:
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
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		int targetId=0;
		if(qs == null || qs.getStatus()!= QuestStatus.START)
		{
			return false;
		}
		else
		{
			if (env.getVisibleObject() instanceof Npc)
				targetId = ((Npc) env.getVisibleObject()).getNpcId();
			switch(targetId)
			{
				case 219256:
				case 219257:
					if(qs.getQuestVarById(0)==0)
					{
						qs.setQuestVar(1);
						updateQuestStatus(env);
						return true;
					}

				case 219264:
					if(qs.getQuestVarById(0)==1)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
			}
		}
		return false;
	}

}
