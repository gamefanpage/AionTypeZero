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
public class _2513TheStrangeCottage extends QuestHandler
{
	private final static int	questId	= 2513;

	public _2513TheStrangeCottage()
	{
		super(questId);
	}

    @Override
	public void register()
	{
		qe.registerQuestNpc(204732).addOnQuestStart(questId); //Gnalin
		qe.registerQuestNpc(204732).addOnTalkEvent(questId);
		qe.registerQuestNpc(204827).addOnTalkEvent(questId); //Hild
		qe.registerQuestNpc(204826).addOnTalkEvent(questId); //Freki
		qe.registerQuestNpc(790022).addOnTalkEvent(questId); //Byggvir
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if(qs == null || qs.getStatus() == QuestStatus.NONE){
			if(targetId == 204732){
				if(dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.START){
			int var = qs.getQuestVarById(0);
			if (targetId == 204826){//Freki
				switch (dialog){
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SETPRO1:
						if (var == 0){
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
				}
			}
			if (targetId == 204827){//Hild
				switch (dialog){
					case QUEST_SELECT:
						if (var == 0){
							return sendQuestDialog(env, 1352);
						}
					case SETPRO2:
						if (var == 0){
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
				}
			}
			if (targetId == 790022){
				switch (dialog){
					case QUEST_SELECT:
						if (var == 0){
							return sendQuestDialog(env, 1693);
						}
					case SETPRO3:
						if (var == 0){
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD){
			if (targetId == 204732){
				switch (dialog){
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default: return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
