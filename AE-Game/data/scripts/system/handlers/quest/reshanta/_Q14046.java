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

package quest.reshanta;

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
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Rhys2002
 */
public class _Q14046 extends QuestHandler {

    private final static int questId = 14046;
    private final static int[] npc_ids = {278500, 203834, 203786, 203754, 203704};

    public _Q14046() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerQuestItem(182215354, questId);
        for (int npc_id : npc_ids) {
            qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
        }
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 14040, true);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc) {
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        }

        if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 203704) {
                if (env.getDialog() == DialogAction.USE_OBJECT){
                    return sendQuestDialog(env, 10002);
}                else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()){
                    return sendQuestDialog(env, 5);
                } else {
                    return sendQuestEndDialog(env);
                }
            }
            return false;
        } else if (qs.getStatus() != QuestStatus.START) {
            return false;
        }
        if (targetId == 278500) {
            switch (env.getDialog()) {
                case QUEST_SELECT:
                    if (var == 0) {
                        return sendQuestDialog(env, 1011);
                    }
                case SETPRO1:
                    if (var == 0) {
                        return defaultCloseDialog(env, 0, 1);
                    }
            }
        } else if (targetId == 203834) {
            switch (env.getDialog()) {
                case QUEST_SELECT:
                    if (var == 1) {
                        return sendQuestDialog(env, 1352);
                    } else if (var == 3) {
                        return sendQuestDialog(env, 2034);
                    } else if (var == 5) {
                        return sendQuestDialog(env, 2716);
                    }
                case SELECT_ACTION_1353:
                    playQuestMovie(env, 102);
                    break;
                case SETPRO2:
                    if (var == 1) {
                        return defaultCloseDialog(env, 1, 2);
                    }
                case SETPRO4:
                    if (var == 3) {
                        return defaultCloseDialog(env, 3, 4);
                    }
                case SETPRO6:
                    if (var == 5) {
                        removeQuestItem(env, 182215354, 1);
                        return defaultCloseDialog(env, 5, 6);
                    }
            }
        } else if (targetId == 203786) {
            switch (env.getDialog()) {
                case QUEST_SELECT:
                    if (var == 2) {
                        return sendQuestDialog(env, 1693);
                    }
                case CHECK_USER_HAS_QUEST_ITEM:
                    return checkQuestItems(env, 2, 3, false, 10000, 10001, 182215354, 1);
            }
        } else if (targetId == 203754) {
            switch (env.getDialog()) {
                case QUEST_SELECT:
                    if (var == 6) {
                        return sendQuestDialog(env, 3057);
                    }
                case SET_SUCCEED:
                    if (var == 6) {
                        return defaultCloseDialog(env, 6, 6, true, false);
                    }
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182215354) {
            return HandlerResult.UNKNOWN;
        }

            final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START || qs.getQuestVarById(0) != 4) {
                return HandlerResult.UNKNOWN;
        }

            PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 1000, 0,
                    0), true);
            ThreadPoolManager.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
                            1, 0), true);
                    playQuestMovie(env, 170);
                    qs.setQuestVar(5);
                    updateQuestStatus(env);
                }
            }, 1000);
            return HandlerResult.SUCCESS;
        }
    }
