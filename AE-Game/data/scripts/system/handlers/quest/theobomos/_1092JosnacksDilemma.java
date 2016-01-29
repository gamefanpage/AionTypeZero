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

package quest.theobomos;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.services.QuestService;

/**
 * Talk with Atropos (798155). Talk with Josnack (798206). Get rid of stones so they can restore the statue: Stone above
 * the Statue (700389) Stone on the Statue Platform (700388) Talk with Atropos. Destroy the Eternal Flames (700390), and
 * collect their Soul Pieces (182208012) (6) from the Burnt Zombies (214552). Take them to Atropos. Receive a reward
 * from Atropos.
 *
 * @author Dune11
 * @reworked vlog
 */
public class _1092JosnacksDilemma extends QuestHandler {

	private final static int questId = 1092;
	private final static int[] npc_ids = { 798155, 798206, 700389, 700388};

	public _1092JosnacksDilemma() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(700390).addOnKillEvent(questId);
		qe.addHandlerSideQuestDrop(questId, 214552, 182208033, 1, 100);
		qe.registerOnDie(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1091, true);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 798155)
				return sendQuestEndDialog(env);
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 798155: // Atropos
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							if (var == 3)
								return sendQuestDialog(env, 2034);
							if (var == 4)
								return sendQuestDialog(env, 2375);
							return true;
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1); // 1
						case SETPRO4:
							return defaultCloseDialog(env, 3, 4); // 4
						case CHECK_USER_HAS_QUEST_ITEM:
							return checkQuestItems(env, 4, 4, true, 10001, 10008); // reward
					}
					break;
				case 798206: // Josnack
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 1)
								return sendQuestDialog(env, 1352);
							if (var == 2)
								return sendQuestDialog(env, 1693);
						case SELECT_ACTION_1353:
							playQuestMovie(env, 364);
							break;
						case SETPRO2:   {
								defaultCloseDialog(env, 1, 2); // 2
								TeleportService2.teleportTo(player, 210060000, 926f, 3035f, 186f, (byte) 30);
								return true;
					}
                    }
					break;
				case 700389: // Stone above the Statue
					switch (env.getDialog()) {
						case USE_OBJECT:
							if (var == 2 && qs.getQuestVarById(1) == 0) {
								qs.setQuestVarById(1, 1); // 1: 1
								updateQuestStatus(env);

								if (qs.getQuestVarById(2) == 1) {
									qs.setQuestVar(3); // 3
									updateQuestStatus(env);
								}
								return true;
							}
					}
					break;
				case 700388: // Stone on the Statue Platform
					switch (env.getDialog()) {
						case USE_OBJECT:
							if (var == 2 && qs.getQuestVarById(2) == 0) {
								qs.setQuestVarById(2, 1); // 2: 1
								if (qs.getQuestVarById(1) == 1) {
									qs.setQuestVar(3); // 3
									updateQuestStatus(env);
									return true;
								}
								updateQuestStatus(env);
								return true;
							}
					}
					break;
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			if (var == 2) {
				qs.setQuestVarById(0, 1);
				qs.setQuestVarById(1, 0);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		VisibleObject target = env.getVisibleObject();
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVars().getQuestVars();
			Storage bag = player.getInventory();
			if (target instanceof Npc) {
				Npc npc = (Npc) target;
				if(npc.getNpcId() == 700390 && var == 4 && (bag.getItemCountByItemId(182208012) < 6)) {
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading());
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX() - 2, npc.getY() - 2, npc.getZ(), npc.getHeading());
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 214552, npc.getX() + 2, npc.getY(), npc.getZ(), npc.getHeading());
					return true;
				}
			}
		}
		return false;
	}
}





