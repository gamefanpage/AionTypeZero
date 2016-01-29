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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 *
 * @author Ritsu
 */
public class _30407CatchingtheLight extends QuestHandler
{

	private static final int questId = 30407;

	public _30407CatchingtheLight()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(799551).addOnQuestStart(questId);
		qe.registerQuestNpc(799551).addOnTalkEvent(questId);
		qe.registerQuestNpc(205575).addOnTalkEvent(questId);
		qe.registerQuestItem(182213014, questId);
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item)
	{
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();
		final Npc npc = (Npc) player.getTarget();
		if (((Npc) player.getTarget()).getNpcId() != 217262)
			return HandlerResult.UNKNOWN;
		if (!MathUtil.isIn3dRange(player, npc, 12.5f))
			return HandlerResult.UNKNOWN;
		if (id != 182213014)
			return HandlerResult.UNKNOWN;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return HandlerResult.FAILED;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run()
			{
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);

				npc.getController().scheduleRespawn();
				npc.getController().onDelete();
				player.getInventory().decreaseByObjectId(itemObjId, 1);
				giveQuestItem(env, 182213015, 1);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
		{
			if (targetId == 799551)
			{
				if (dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				if (dialog == DialogAction.QUEST_ACCEPT_SIMPLE)
				{
					if (giveQuestItem(env, 182213014, 1))
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
				case 799551:
				{
					switch (dialog)
					{
						case QUEST_SELECT:
						{
							if (var == 0)
								return sendQuestDialog(env, 1011);
						}
						case CHECK_USER_HAS_QUEST_ITEM_SIMPLE:
						{
							if (QuestService.collectItemCheck(env, true))
							{
								changeQuestStep(env, 0, 0, true);
								return sendQuestDialog(env, 10002);
							}
							else
								return closeDialogWindow(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD)
		{
			if (targetId == 799551)
				return sendQuestEndDialog(env);
		}
		return false;
	}
}
