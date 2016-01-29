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

import org.typezero.gameserver.model.DialogAction;
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
 * @author Balthazar
 */

public class _1467TheFourLeaders extends QuestHandler {

    private final static int questId = 1467;

    public _1467TheFourLeaders() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(204045).addOnQuestStart(questId);
        qe.registerQuestNpc(204045).addOnTalkEvent(questId);
        qe.registerQuestNpc(211696).addOnKillEvent(questId);
        qe.registerQuestNpc(211697).addOnKillEvent(questId);
        qe.registerQuestNpc(211698).addOnKillEvent(questId);
        qe.registerQuestNpc(211699).addOnKillEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();


        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204045) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 204045) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    if (qs.getQuestVarById(0) == 0) {
                        return sendQuestDialog(env, 1011);
                    }
                }
                if (env.getDialog() == DialogAction.SETPRO1) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
                if (env.getDialog() == DialogAction.SETPRO2) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 2);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;

                }
                if (env.getDialog() == DialogAction.SETPRO3) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 3);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
                if (env.getDialog() == DialogAction.SETPRO4) {
                        qs.setQuestVarById(0, qs.getQuestVarById(0) + 4);
                        updateQuestStatus(env);
                        PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                        return true;
                }
            }
        }


    if(qs != null && qs.getStatus() == QuestStatus.REWARD)

    {
        if (targetId == 204045) {
            switch (env.getDialog()) {
                case USE_OBJECT: {
                    switch (qs.getQuestVarById(0)) {
                        case 1: {
                            return sendQuestDialog(env, 5);
                        }
                        case 2: {
                            return sendQuestDialog(env, 6);
                        }
                        case 3: {
                            return sendQuestDialog(env, 7);
                        }
                        case 4: {
                            return sendQuestDialog(env, 8);
                        }
                    }
                }
                case SELECTED_QUEST_NOREWARD: {
                    QuestService.finishQuest(env, qs.getQuestVarById(0) - 1);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    return true;
                }
            }
        }
    }

    return false;
}

    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() != QuestStatus.START) {
            return false;
        }

        int var = 0;
        int targetId = 0;

        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();

        switch (targetId) {
            case 211696: {
                if (qs.getQuestVarById(0) == 1) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211697: {
                if (qs.getQuestVarById(0) == 2) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211698: {
                if (qs.getQuestVarById(0) == 3) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
            case 211699: {
                if (qs.getQuestVarById(0) == 4) {
                    if (var == 0) {
                        var = 1;
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
