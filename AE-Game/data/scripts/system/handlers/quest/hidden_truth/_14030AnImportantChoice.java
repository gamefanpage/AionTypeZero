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

package quest.hidden_truth;

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.SystemMessageId;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

//By Evil_dnk

public class _14030AnImportantChoice extends QuestHandler {

	private final static int questId = 14030;
	private final static int[] npcs = { 790001, 700551, 205119, 700552, 205118, 203700 };
	private final static int[] mobs = { 214578, 215396, 215397, 215398, 215399, 215400 };

	public _14030AnImportantChoice() {
		super(questId);
	}

	@Override
	public void register() {
        qe.registerOnLevelUp(questId);
        qe.registerOnDie(questId);
		qe.registerOnEnterWorld(questId);
		for (int npc_id : npcs) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
	}

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env);
    }


    @Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (env.getTargetId() == 203700) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == DialogAction.QUEST_SELECT)
                    return sendQuestDialog(env, 1011);
                else if (env.getDialog() == DialogAction.SETPRO1)
                    return defaultCloseDialog(env, 0, 1); // 1
            }
            if (qs != null && qs.getStatus() == QuestStatus.REWARD)
            {
                return sendQuestEndDialog(env);
            }
            else
                return sendQuestStartDialog(env);
        }
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getTargetId() == 790001) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 1){
                        return sendQuestDialog(env, 1352);
                    }
                    if(var == 3){
                        return sendQuestDialog(env, 2034);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO2)
                    return defaultCloseDialog(env, 1, 2); // 1
                else if (env.getDialog() == DialogAction.SETPRO4)
                    return defaultCloseDialog(env, 3, 4, 182215387, 1, 0, 0);
                else
                    return sendQuestStartDialog(env);
            }
        }

        if (env.getTargetId() == 700551) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                if (env.getDialog() == DialogAction.USE_OBJECT && var == 4) {
                    WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(310120000);
                    InstanceService.registerPlayerWithInstance(newInstance, player);
                    TeleportService2.teleportTo(player, 310120000, newInstance.getInstanceId(), 52, 174, 229);
                    return true;
                }
        }
        }

        if (env.getTargetId() == 205119) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == DialogAction.QUEST_SELECT)
                    return sendQuestDialog(env, 2375);
                else if (env.getDialog() == DialogAction.SETPRO5){
                    player.setState(CreatureState.FLIGHT_TELEPORT);
                    player.unsetState(CreatureState.ACTIVE);
                    player.setFlightTeleportId(1001);
                    PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
                    final QuestEnv qe = env;
                    ThreadPoolManager.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            changeQuestStep(qe, 4, 5, false);
                        }
                    }, 1000);
                    return true;
                }
            }
        }
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (env.getTargetId() == 700552) {
                int var = qs.getQuestVarById(0);
            if (env.getDialog() == DialogAction.USE_OBJECT && var == 56) {
                return useQuestObject(env, 56, 57, true, 0, 0, 0, 182215387, 1, 0, false); // 54

            }
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
       if (var == 2)
            {
				return defaultOnKillEvent(env, 214578, 2, 3); // 3
			}
            if (var >= 5 && var < 55) {
                int[] npcIds = { 215396, 215397, 215398, 215399 };
                if (var == 54)     {
                    QuestService.addNewSpawn(310120000, player.getInstanceId(), 215400, 240f, 257f, 208.53946f, (byte) 68);
                }
                return defaultOnKillEvent(env, npcIds, 5, 55); // 2 - 52
            }
            else if (var == 55) {
                return defaultOnKillEvent(env, 215400, 55, 56); // 56
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
			if (var > 4) {
				changeQuestStep(env, var, 4, false); // 1
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
					DataManager.QUEST_DATA.getQuestById(questId).getName()));
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (player.getWorldId() != 310120000) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 4) {
					changeQuestStep(env, var, 4, false); // 1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
		}
		return false;
	}

}
