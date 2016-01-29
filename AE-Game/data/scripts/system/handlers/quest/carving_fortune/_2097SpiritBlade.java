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

package quest.carving_fortune;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke, dune11
 */
public class _2097SpiritBlade extends QuestHandler {

	private final static int questId = 2097;

	public _2097SpiritBlade() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203550).addOnTalkEvent(questId); // MUNIN
		qe.registerQuestNpc(203546).addOnTalkEvent(questId); // SKULID
		qe.registerQuestNpc(279034).addOnTalkEvent(questId);
		qe.registerQuestNpc(700509).addOnTalkEvent(questId);
		qe.registerQuestNpc(700510).addOnTalkEvent(questId);
		qe.registerQuestNpc(214696).addOnTalkEvent(questId);
		qe.registerQuestItem(182207085, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVars().getQuestVars();
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203550: // MUNIN
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
						case SETPRO1:
							if (var == 0) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
					break;
				case 203546: // SKULID
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
							return true;
						case SETPRO2:
							if (var == 1) {
								qs.setQuestVarById(0, var + 1);
								updateQuestStatus(env);
								PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
								return true;
							}
					}
					break;
				case 279034:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 2)
								return sendQuestDialog(env, 1693);
						case CHECK_USER_HAS_QUEST_ITEM:
							if (var == 2) {
								if (QuestService.collectItemCheck(env, true)) {
									if (!giveQuestItem(env, 182207085, 1))
										return true;
									qs.setStatus(QuestStatus.REWARD);
									updateQuestStatus(env);
									PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
									return sendQuestDialog(env, 10000);
								}
								else
									return sendQuestDialog(env, 10001);
							}
					}
					break;
                                case 700509: {
                                        switch (env.getDialog()) {
						case USE_OBJECT:
					return true;
				}
                            }
                                    break;
                                case 700510: {
                                        switch (env.getDialog()) {
						case USE_OBJECT:
					return true;
				}
                            }
                                    break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD && targetId == 203550)
			return sendQuestEndDialog(env);
		return false;
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env);
	}

}
