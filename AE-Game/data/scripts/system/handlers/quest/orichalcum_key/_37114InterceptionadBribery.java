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

package quest.orichalcum_key;

import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;

//By Evil_dnk

public class _37114InterceptionadBribery extends QuestHandler {

	private final static int questId = 37114;

	public _37114InterceptionadBribery() {
		super(questId);
	}

	public void register() {
		qe.registerQuestNpc(799906).addOnQuestStart(questId);
		qe.registerQuestNpc(799906).addOnTalkEvent(questId);
		qe.registerQuestNpc(700973).addOnTalkEvent(questId);
		qe.registerQuestNpc(799932).addOnTalkEvent(questId);
	}


    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (qs == null || qs.getStatus() == QuestStatus.NONE) {
                if (targetId == 799906) {
                    if (dialog == DialogAction.QUEST_SELECT) {
                        return sendQuestDialog(env, 1011);
                    }
                    else {
                        return sendQuestStartDialog(env);
                    }
                }
            }

        }
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (targetId == 700973) {
                if (player.isInGroup2()){
                    PlayerGroup group = player.getPlayerGroup2();
                    for (Player member : group.getMembers()) {
                        if (member.isMentor() && MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE)  {
                            SkillEngine.getInstance().applyEffectDirectly(300, player, player, 600000);
                            return true;
                        }
                        else
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DailyQuest_Ask_Mentee);
                    }
                }
            }

            if (targetId == 799932) {
                if (dialog == DialogAction.USE_OBJECT) {
                    if (player.isInGroup2()){
                        PlayerGroup group = player.getPlayerGroup2();
                        for (Player member : group.getMembers()) {
                            if (member.isMentor() && MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE)  {
                     if(qs.getQuestVarById(0) == 0) {
                        if (player.getInventory().getItemCountByItemId(182210041) == 0 && player.getEffectController().hasAbnormalEffect(300))   {
                            QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), 700974, player.getX()+2, player.getY()+2, player.getZ(), player.getHeading(), 5);
                            player.getEffectController().removeEffect(300);
                            return false;
                        }
                        else
                        {
                            PacketSendUtility.sendMessage(player, "Transformation of Blaur required");
                            return closeDialogWindow(env);
                        }

                     }
                    }
                            else       {
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DailyQuest_Ask_Mentee);
                                return closeDialogWindow(env);
                            }
                  }
                }
            }
            }
            if (targetId == 799906) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    if(qs.getQuestVarById(0) == 0) {
                        return sendQuestDialog(env, 2375);
                    }
                }
                else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    return checkQuestItems(env, 0, 1, true, 5, 2716);
                }
            }
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 799906) {
                if (dialog == DialogAction.USE_OBJECT) {
                    return sendQuestDialog(env, 5);
                }
                else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
    }
}
