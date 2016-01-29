/**
 * This file is part of Aion Eternity Core <Ver:4.5>.
 *
 * Aion Eternity Core is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * Aion Eternity Core is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Aion Eternity Core. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package quest.rider_quests;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;

/**
 * @author pralinka
 */
public class _14091IdentityBusiness extends QuestHandler {

    private final static int questId = 14091;

    public _14091IdentityBusiness() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnLogOut(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(802061).addOnTalkEvent(questId); //Oriata of the Past
        qe.registerQuestNpc(802063).addOnTalkEvent(questId); //Kaisinel
        qe.registerQuestNpc(798926).addOnTalkEvent(questId); //Agent Outremus
        qe.registerQuestNpc(798600).addOnTalkEvent(questId); //Eremitia
        qe.registerQuestNpc(203700).addOnTalkEvent(questId); //Fasimedes
        qe.registerQuestNpc(205987).addOnTalkEvent(questId); //Garnon
        qe.registerQuestNpc(800165).addOnTalkEvent(questId); //Crispin
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 14090, true);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null) {
            return false;
        }
        int var = qs.getQuestVarById(0);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();
        if (qs.getStatus() == QuestStatus.START) {
            switch (targetId) {
                case 802061: { //Oriata of the Past
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case SETPRO1: {
                            QuestService.addNewSpawn(300500000, player.getInstanceId(), 802063, 253f, 245f, 125f, (byte) 119);
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getController().onDelete();
                            return defaultCloseDialog(env, 0, 1);
                        }
                    }
                    break;
                }
                case 802063: { //Kaisinel
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            }
                        }
                        case SETPRO2: {
                            giveQuestItem(env, 182215411, 1);
                            TeleportService2.teleportTo(player, 210050000, 1, 1313, 246, 592, (byte) 20, TeleportAnimation.BEAM_ANIMATION);
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getController().onDelete();
                            return defaultCloseDialog(env, 1, 2);
                        }
                    }
                    break;
                }
                case 798926: { //Agent Outremus
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case SETPRO3: {
                            return defaultCloseDialog(env, 2, 3);
                        }
                    }
                    break;
                }
                case 798600: { //Eremitia
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 3) {
                                return sendQuestDialog(env, 2034);
                            } else if (var == 5) {
                                return sendQuestDialog(env, 2716);
                            }
                        }
                        case SETPRO4: {
                            return defaultCloseDialog(env, 3, 4);
                        }
                        case SETPRO6: {
                            return defaultCloseDialog(env, 5, 6);
                        }
                    }
                    break;
                }
                case 203700: { //Fasimedes
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 4) {
                                return sendQuestDialog(env, 2375);
                            }
                        }
                        case SETPRO5: {
                            return defaultCloseDialog(env, 4, 5);
                        }
                    }
                    break;
                }
                case 205987: { //Garnon
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 6) {
                                return sendQuestDialog(env, 3057);
                            }
                        }
                        case SETPRO7: {
                            return defaultCloseDialog(env, 6, 6, true, false);
                        }
                    }
                    break;
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800165) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (player.getWorldId() == 300500000) {
                if (var == 0) {
                    QuestService.addNewSpawn(300500000, player.getInstanceId(), 802061, 255f, 245f, 125f, (byte) 55);
                }
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
            if (var <= 2) {
                qs.setQuestVar(0);
                updateQuestStatus(env);
                return true;
            }
        }
        return false;
    }
}
