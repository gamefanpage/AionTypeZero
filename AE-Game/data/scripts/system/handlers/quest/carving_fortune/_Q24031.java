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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.WorldMapInstance;

//By Evil_dnk

public class _Q24031 extends QuestHandler {

	private final static int questId = 24031;
	private final static int[] npcs = { 204052, 801224, 203550, 203654, 204369, 730888, 730898, 205020};
	private final static int[] mobs = { 233878 };

	public _Q24031() {
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
        qe.registerQuestItem(182215394, questId);
        qe.registerQuestItem(182215395, questId);
        qe.registerQuestItem(182215396, questId);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 24030, false);
    }

    @Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

      if (qs != null && qs.getStatus() == QuestStatus.START) {
        if (env.getTargetId() == 204052) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    return sendQuestDialog(env, 1011);
                }
                else if (env.getDialog() == DialogAction.SETPRO1){
                    return defaultCloseDialog(env, 0, 1); // 1
                }
                else
                    return sendQuestStartDialog(env);
        }

        if (env.getTargetId() == 801224) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 1){
                        return sendQuestDialog(env, 1352);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO2)
                    return defaultCloseDialog(env, 1, 2); // 1
        }
        if (env.getTargetId() == 203550) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 2){
                        return sendQuestDialog(env, 1693);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO3)
                    return defaultCloseDialog(env, 2, 3, 182215394,1,0,0); // 1
        }
        if (env.getTargetId() == 203654) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 4){
                        return sendQuestDialog(env, 2375);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO5)
                    return defaultCloseDialog(env, 4, 5, 182215395,1,0,0); // 1
            }
            if (env.getTargetId() == 204369) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 6){
                        return sendQuestDialog(env, 3057);
                    }
                    if(var == 8){
                        return sendQuestDialog(env, 3739);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO7)
                    return defaultCloseDialog(env, 6, 7, 182215396,1,0,0);
                else if (env.getDialog() == DialogAction.SETPRO9){         //TODO - Find real part of the quest
                    qs.setQuestVar(9);
                    WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(320040000);
                    InstanceService.registerPlayerWithInstance(newInstance, player);
                    TeleportService2.teleportTo(player, 320040000, newInstance.getInstanceId(), 252, 263, 228);
                    updateQuestStatus(env);
                    QuestService.spawnQuestNpc(320040000, player.getInstanceId(), 730888, 254f, 246f, 222.53946f, (byte) 68);
                     QuestService.spawnQuestNpc(320040000, player.getInstanceId(), 233878, 254f, 248f, 223.53946f, (byte) 68);
                    return true;
                }
            }
            if (env.getTargetId() == 730888) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 10){
                        return sendQuestDialog(env, 4081);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO11){
                    Npc npc = (Npc) env.getVisibleObject();
                    npc.getController().delete();
                    QuestService.spawnQuestNpc(320040000, player.getInstanceId(), 730898, 254f, 246f, 222.53946f, (byte) 68);
                    return defaultCloseDialog(env, 10, 11); // 1
                }
            }
            if (env.getTargetId() == 730898) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    int var = qs.getQuestVarById(0);
                    if(var == 11){
                        return sendQuestDialog(env, 4082);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO12){
                    return defaultCloseDialog(env, 11, 12, true, false); // 1
                }
            }
        }
        if (qs != null && qs.getStatus() == QuestStatus.REWARD)
        {
            if (env.getTargetId() == 204052) {
                if (env.getDialog() == DialogAction.USE_OBJECT) {
                    return sendQuestDialog(env, 4083);
                }
                if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
                    return sendQuestDialog(env, 5);
                }
                if (env.getDialog() == DialogAction.SELECTED_QUEST_REWARD1) {
                    QuestService.finishQuest(env);
                    return closeDialogWindow(env);
                }
                if (env.getDialog() == DialogAction.SELECTED_QUEST_REWARD2) {
                    QuestService.finishQuest(env);
                    return closeDialogWindow(env);
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
       if (var == 9)
            {
				return defaultOnKillEvent(env, 233878, 9, 10);
			}
        }
		return false;
	}


    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        final int id = item.getItemTemplate().getTemplateId();

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (id == 182215394 && var == 3){
            return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, false));
        }
            if (id == 182215395 && var == 5){
                return HandlerResult.fromBoolean(useQuestItem(env, item, 5, 6, false));
            }
            if (id == 182215396 && var == 7){
                return HandlerResult.fromBoolean(useQuestItem(env, item, 7, 8, false));
            }
        }
        return HandlerResult.FAILED;
    }
}
