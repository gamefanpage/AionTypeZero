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

package quest.altgard;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_USE_OBJECT;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 *
 */
public class _2252ChasingtheLegend extends QuestHandler
{

	private final static int	questId	= 2252;
	private final static int[]   mob_ids   = { 210634 }; //Minusha's Spirit

	public _2252ChasingtheLegend()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(203646).addOnQuestStart(questId); //Sinood
		qe.registerQuestNpc(203646).addOnTalkEvent(questId);
		qe.registerQuestNpc(700060).addOnTalkEvent(questId); //Bone of Minusha
		      for(int mob_id : mob_ids)
         qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
				if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		switch(targetId)
		{
			case 210634: //Minusha's Spirit
				if(var == 0)
				{
					qs.setQuestVarById(0, var + 1);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		@SuppressWarnings("unused")
		Npc npc = null;
		if(env.getVisibleObject() instanceof Npc)
					npc = (Npc) env.getVisibleObject();
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if(qs == null || qs.getStatus() == QuestStatus.NONE){
			if(targetId == 203646){//Sinood
				if(dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if(targetId == 203646){
				switch (dialog){
					case QUEST_SELECT:
						if(var == 1){
							qs.setQuestVar(2);
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							return sendQuestDialog(env, 1352);
						}
					case SELECT_QUEST_REWARD:
						if(var == 2)
							return sendQuestEndDialog(env);
				}
			}
			if(targetId == 700060){
				switch (dialog){
					case USE_OBJECT:
						if(var == 0){
							final int targetObjectId = env.getVisibleObject().getObjectId();
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
								1));
							PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0,
								targetObjectId), true);
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
								public void run()
								{
									Npc npc = (Npc)player.getTarget();
									if(npc == null || npc.getObjectId() != targetObjectId)
										return;
									QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 210634, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading()); //Minusha's Spirit
									if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
										return;
									PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
										targetObjectId, 3000, 0));
									PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0,
										targetObjectId), true);
									((Npc)player.getTarget()).getController().onDie(null);
								}
							}, 3000);
						}
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
				return sendQuestEndDialog(env);
		return false;
	}
}
