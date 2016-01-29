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

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.dataholders.DataManager;
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
public class _Q24030 extends QuestHandler {

	private final static int questId = 24030;
	private final static int[] npcs = { 203550, 205020, 205118, 700551, 204052, 204206, 204207, 831739 };
	private final static int[] mobs = { 798342, 798343, 798344, 798345, 798346 };

	public _Q24030() {
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

        if (env.getTargetId() == 204206) {
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

        if (env.getTargetId() == 204207) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 1){
                        return sendQuestDialog(env, 1352);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO2) {
                        qs.setQuestVar(2);
                        updateQuestStatus(env);
                        TeleportService2.teleportTo(player, 220010000, player.getInstanceId(), 391f, 1893f, 327f);
                        return true;
            }
            }
        }

        if (env.getTargetId() == 203550) {
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 2){
                        return sendQuestDialog(env, 1693);
                    }
                    if(var == 3){
                        return sendQuestDialog(env, 2034);
                    }
                    if(var == 4){
                        return sendQuestDialog(env, 2375);
                    }
                    if(var == 8){
                        return sendQuestDialog(env, 3739);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO3){
                    return defaultCloseDialog(env, 2, 3);
                }
                else if (env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM){
                    if (QuestService.collectItemCheck(env, true)) {
                        qs.setQuestVar(4);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 10000);
                    }
                    else
                        return sendQuestDialog(env, 10001);
                }
                else if (env.getDialog() == DialogAction.SETPRO5){
                    return defaultCloseDialog(env, 4, 5);
                }
                else if (env.getDialog() == DialogAction.SET_SUCCEED){
                    return defaultCloseDialog(env, 8, 8, true, false);
                }
            }
        }
        if (env.getTargetId() == 700551) { // Fissure of Destiny
            if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (env.getDialog() == DialogAction.USE_OBJECT && var == 5) {
                WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320140000);
                InstanceService.registerPlayerWithInstance(newInstance, player);
                TeleportService2.teleportTo(player, 320140000, newInstance.getInstanceId(), 52, 174, 229);
                return true;
            }
          }
        }
        if (env.getTargetId() == 205020) { // Hagen
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    if (var == 5) {
                        return sendQuestDialog(env, 2716);
                    }
                }
                if (env.getDialog() == DialogAction.SETPRO6) {
                    if (var == 5) {
                        player.setState(CreatureState.FLIGHT_TELEPORT);
                        player.unsetState(CreatureState.ACTIVE);
                        player.setFlightTeleportId(1001);
                        PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 1001, 0));
                        final QuestEnv qe = env;
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                changeQuestStep(qe, 5, 6, false);
                            }
                        }, 1000);
                        return true;
                    }
                }
            }
        }

        if (env.getTargetId() == 204052) { // Vidar
         if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            removeQuestItem(env, 182207093, 1);
            removeQuestItem(env, 182207094, 1);
        return sendQuestEndDialog(env);
        }
        }
    return false;
    }



@Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(1);
            int var1 = qs.getQuestVarById(0);
            if (var >= 0 && var < 50 && var1 == 6) {
                int[] npcIds = { 798342, 798343, 798344, 798345 };
                if (var == 49){
                    QuestService.addNewSpawn(320140000, player.getInstanceId(), 798346, 240f, 257f, 208.53946f, (byte) 68);
                    changeQuestStep(env, 6, 7, false);
                }
                return defaultOnKillEvent(env, npcIds, 0, 50, 1);
            }
            if (var1 == 7) {
                qs.setQuestVarById(1, 0);
                return defaultOnKillEvent(env, 798346, 7, 8);
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
			if (var > 5) {
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
		if (player.getWorldId() != 320140000) {
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var > 5 && var < 8) {
					changeQuestStep(env, var, 5, false); // 1
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(SystemMessageId.QUEST_FAILED_$1,
						DataManager.QUEST_DATA.getQuestById(questId).getName()));
					return true;
				}
			}
		}
		return false;
	}

}
