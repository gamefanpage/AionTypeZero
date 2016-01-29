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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Gigi, Shepper
 */
public class _1563TheLegendofVindachinerk extends QuestHandler {

	private final static int questId = 1563;

	public _1563TheLegendofVindachinerk() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(798096).addOnQuestStart(questId); // Poporinerk
		qe.registerQuestNpc(279005).addOnTalkEvent(questId); // Kohrunerk
		qe.registerQuestItem(182201729, questId); // Jaiorunerk's Diary
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 798096) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}

		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798096: // Poporinerk
					switch (dialog) {
						case QUEST_SELECT:
							return sendQuestDialog(env, 1352);
						case SETPRO2:
							return checkQuestItems(env, 1, 0, true, 5, 1353);
					}
					break;
				case 279005:
					switch (dialog) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
						case SETPRO2:
							return checkQuestItems(env, 1, 2, true, 6, 1439);
					}
			}
		}
		if(sendQuestRewardDialog(env, 798096, 0, 0) || sendQuestRewardDialog(env, 279005, 0, 1))
			return true;
		else
			return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if (id != 182201729)
			return HandlerResult.UNKNOWN;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return HandlerResult.UNKNOWN;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				qs.setQuestVar(1);
				updateQuestStatus(env);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}
