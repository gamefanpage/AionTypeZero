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

package quest.satra_treasure_hoard;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;


/**
 * @author Ritsu
 *
 */
class _18904AccordingToHisAbility extends QuestHandler{

	private final static int questId=18904;
	private final static int[] npc_ids={219302,219303,219304,219305,219306,219307,219308,219309,219310}; //STR_DIC_E_Q18904a

	public _18904AccordingToHisAbility()
	{
		super(questId);

	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(800331).addOnQuestStart(questId);
		qe.registerQuestNpc(800331).addOnTalkEvent(questId); //Apsilon
		qe.registerQuestNpc(205844).addOnTalkEvent(questId); //Karuti
		for(int id:npc_ids)
			qe.registerQuestNpc(id).addOnKillEvent(questId);

	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		int var=qs.getQuestVarById(0);
		int targetId = env.getTargetId();
		if(env.getVisibleObject() instanceof Npc){
			targetId=((Npc)env.getVisibleObject()).getNpcId();
		}
		if(qs==null || qs.getStatus() == QuestStatus.NONE){
			if(targetId == 800331){
				if(env.getDialog() == DialogAction.QUEST_SELECT)//26
					return sendQuestDialog(env,1011);  //select1
				else
					return sendQuestStartDialog(env);
			}
		}
		else if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 800331){
				if(env.getDialog() == DialogAction.QUEST_SELECT)//26
					return sendQuestDialog(env,1011);
				else
					return sendQuestStartDialog(env);
			}
			else if(targetId == 205844){
				if(env.getDialog() ==DialogAction.QUEST_SELECT)//26
					return sendQuestDialog(env,1352);//select2
				else
					if (var == 9)
					{
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		else if(qs.getStatus() ==QuestStatus.REWARD){
			if(targetId==205844){
				if (env.getDialog() == DialogAction.USE_OBJECT) //-1=ERROR
					return sendQuestDialog(env, 1008);   //quest_complete
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())  //SET_SUCCEED
					return sendQuestDialog(env, 5);  //select_quest_reward1
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		return defaultOnKillEvent(env,npc_ids,0,9);
	}
}
