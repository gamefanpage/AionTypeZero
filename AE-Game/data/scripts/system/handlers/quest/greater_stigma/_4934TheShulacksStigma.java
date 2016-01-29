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

package quest.greater_stigma;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author JIEgOKOJI
 * @modified kecimis
 */
public class _4934TheShulacksStigma extends QuestHandler {

	private final static int questId = 4934;

	public _4934TheShulacksStigma() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204051).addOnQuestStart(questId); // Vergelmir start
		qe.registerQuestNpc(204211).addOnTalkEvent(questId); // Moreinen
		qe.registerQuestNpc(204285).addOnTalkEvent(questId); // Teirunerk
		qe.registerQuestNpc(700562).addOnTalkEvent(questId); //
		qe.registerQuestNpc(204051).addOnTalkEvent(questId); // Vergelmir
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		// Instanceof
		final Player player = env.getPlayer();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);

		// ------------------------------------------------------------
		// NPC Quest :
		// 0 - Vergelmir start
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 204051) {
				// Get QUEST_SELECT in the eddit-HyperLinks.xml
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					// Send HTML_PAGE_SELECT_NONE to eddit-HtmlPages.xml
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
				case 204211: // Moreinen
					if (var == 0) {
						switch (env.getDialog()) {
							// Get QUEST_SELECT in the eddit-HyperLinks.xml
							case QUEST_SELECT:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1011);
								// Get SETPRO1 in the eddit-HyperLinks.xml
							case SETPRO1:
								qs.setQuestVar(1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
					}
					// 2 / 4- Talk with Teirunerk
				case 204285:
					if (var == 1) {
						switch (env.getDialog()) {
							// Get QUEST_SELECT in the eddit-HyperLinks.xml
							case QUEST_SELECT:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1352);
								// Get SETPRO1 in the eddit-HyperLinks.xml
							case SETPRO2:
								qs.setQuestVar(2);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
						}
					}
					else if (var == 2) {
						switch (env.getDialog()) {
							// Get QUEST_SELECT in the eddit-HyperLinks.xml
							case QUEST_SELECT:
								// Send select1 to eddit-HtmlPages.xml
								return sendQuestDialog(env, 1693);
								// Get SETPRO1 in the eddit-HyperLinks.xml
							case CHECK_USER_HAS_QUEST_ITEM:
								if (player.getInventory().getItemCountByItemId(182207102) < 1) {
									// player doesn't own required item
									return sendQuestDialog(env, 10001);
								}
								removeQuestItem(env, 182207102, 1);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env, 10000);
						}
					}
					return false;
				case 700562:
					if (var == 2) {
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								updateQuestStatus(env);
							}
						}, 3000);
						return true;
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204051) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
