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

package quest.eltnen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapType;


public class _14026TheConspiracyoftheBeritraArmy extends QuestHandler {

	private final static int questId = 14026;

	private static List<Integer> mobs = new ArrayList<Integer>();

	static {
		mobs.add(211628);
		mobs.add(211630);
		mobs.add(213575);
	}


	public _14026TheConspiracyoftheBeritraArmy() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnDie(questId);
		qe.registerOnLogOut(questId);
		qe.registerOnQuestTimerEnd(questId);
		qe.registerQuestNpc(203901).addOnTalkEvent(questId);
		qe.registerQuestNpc(204020).addOnTalkEvent(questId);
		qe.registerQuestNpc(204044).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestNpc(700141).addOnTalkEvent(questId);
	}

     @Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203901: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							qs.setQuestVar(1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 1596.1948f, 1529.9152f, 317, (byte) 120, TeleportAnimation.BEAM_ANIMATION);
							return true;
						}
					}
				}
				case 204020: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							if (qs.getQuestVarById(0) == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
							qs.setQuestVar(2);
							updateQuestStatus(env);
							giveQuestItem(env, 182201013, 1);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 2500.15f, 780.9f, 409, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
							return true;
						}
					}
				}
				case 204044: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							switch (qs.getQuestVarById(0)) {
								case 2: {
									return sendQuestDialog(env, 1693);
								}
								case 4: {
									return sendQuestDialog(env, 2034);
								}
							}
						}
						case SETPRO3: {
							qs.setQuestVar(3);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							QuestService.questTimerStart(env, 180);
							spawn(player);
							return true;
						}
						case SETPRO4: {
							qs.setStatus(QuestStatus.REWARD);
							updateQuestStatus(env);
							TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 271.69f, 2787.04f, 272.47f, (byte) 50, TeleportAnimation.BEAM_ANIMATION);
							return true;
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) {
				switch (env.getDialog()) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 2375);
					}
					case SELECT_QUEST_REWARD: {
						return sendQuestDialog(env, 5);
					}
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		int[] quests = { 14025 };
		return defaultOnZoneMissionEndEvent(env, quests);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 14025};
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onQuestTimerEndEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(4);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onDieEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(2);
				updateQuestStatus(env);
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onLogOutEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				qs.setQuestVar(2);
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
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var == 3) {
				int targetId = env.getTargetId();
				if (mobs.contains(targetId)) {
					spawn(player);
					return true;
				}
			}
		}
		return false;
	}

	private void spawn(Player player) {
		int mobToSpawn = mobs.get(Rnd.get(0, 2));
		float x = 0;
		float y = 0;
		final float z = 217.48f;
		switch (mobToSpawn) {
			case 211628: {
				x = 254.74f;
				y = 236.72f;
				break;
			}
			case 211630: {
				x = 257.92f;
				y = 237.39f;
				break;
			}
			case 213575: {
				x = 261.86f;
				y = 237.5f;
				break;
			}
		}
		Npc spawn = (Npc) QuestService.spawnQuestNpc(310040000, player.getInstanceId(), mobToSpawn, x, y, z, (byte) 95);
		Collection<Npc> allNpcs = World.getInstance().getNpcs();
		Npc target = null;
		for (Npc npc : allNpcs) {
			if (npc.getNpcId() == 204044) {
				target = npc;
			}
		}
		if (target != null) {
			spawn.setTarget(target);
			((AbstractAI) spawn.getAi2()).setStateIfNot(AIState.WALKING);
			spawn.setState(1);
			spawn.getMoveController().moveToTargetObject();
			PacketSendUtility.broadcastPacket(spawn, new SM_EMOTION(spawn, EmotionType.START_EMOTE2, 0, spawn.getObjectId()));
		}
	}
}
