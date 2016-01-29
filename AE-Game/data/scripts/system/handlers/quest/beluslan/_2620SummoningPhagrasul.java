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

package quest.beluslan;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
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
 * @author Nephis
 *
 */
public class _2620SummoningPhagrasul extends QuestHandler
{

	private final static int	questId	= 2620;
	private final static int[]   mob_ids   = { 213109, 213111 };

	public _2620SummoningPhagrasul()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(204787).addOnQuestStart(questId); //Chieftain Akagitan
		qe.registerQuestNpc(204787).addOnTalkEvent(questId);
		qe.registerQuestNpc(204824).addOnTalkEvent(questId); //Gigantic Phagrasul
		qe.registerQuestNpc(700323).addOnTalkEvent(questId); //Huge Mamut Skull
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

		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
		targetId = ((Npc) env.getVisibleObject()).getNpcId();

		switch(targetId)
		{
			case 213109:
				if(qs.getQuestVarById(1) < 5 && qs.getQuestVarById(0) == 1)
				{
					qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
					updateQuestStatus(env);
					return true;
				}
				break;

			case 213111:
				if(qs.getQuestVarById(2) < 5 && qs.getQuestVarById(0) == 1)
				{
					qs.setQuestVarById(2, qs.getQuestVarById(2) + 1);
					updateQuestStatus(env);
					return true;
				}
        }

		return false;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE){
			if (targetId == 204787){//Chieftain Akagitan
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else if (dialog == DialogAction.QUEST_ACCEPT_1){
					if (!giveQuestItem(env, 182204498, 1))
						return true;
					return sendQuestStartDialog(env);
				}
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			int var = qs.getQuestVarById(0);
			if (targetId == 204824){
				switch (dialog){
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SETPRO1:
						if (var == 0){
							qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							final Npc npc = (Npc)env.getVisibleObject();
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
								public void run()
								{
									npc.getController().onDelete();
								}
							}, 40000);
							return true;
						}
				}
			}
			if (targetId == 700323){//Hugh mamut skull
				switch (dialog){
					case USE_OBJECT:
						if (var == 0){
							final int targetObjectId = env.getVisibleObject().getObjectId();
							PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(), targetObjectId, 3000,
								1));
							PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.NEUTRALMODE2, 0,
								targetObjectId), true);
							ThreadPoolManager.getInstance().schedule(new Runnable(){
								@Override
								public void run()
								{
									@SuppressWarnings("unused")
									final QuestState qs = player.getQuestStateList().getQuestState(questId);
									removeQuestItem(env, 182204498, 1);
									if(player.getTarget() == null || player.getTarget().getObjectId() != targetObjectId)
										return;
									PacketSendUtility.sendPacket(player, new SM_USE_OBJECT(player.getObjectId(),
										targetObjectId, 3000, 0));
									PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_LOOT, 0,
										targetObjectId), true);
									QuestService.addNewSpawn(220040000, 1, 204824, (float) 2851.698, (float) 160.88698, (float) 301.78537, (byte) 93);
								}
							}, 3000);
						}
				}
			}
			if(targetId == 204787){//Chieftain Akagitan
				switch (dialog){
					case USE_OBJECT:
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 204787)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
