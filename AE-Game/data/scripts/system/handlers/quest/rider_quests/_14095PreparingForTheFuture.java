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
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author pralinka
 */
public class _14095PreparingForTheFuture extends QuestHandler {

    private final static int questId = 14095;

    public _14095PreparingForTheFuture() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerOnLevelUp(questId);
        qe.registerOnLogOut(questId);
        qe.registerOnEnterWorld(questId);
        qe.registerQuestNpc(801327).addOnTalkEvent(questId); //Lord Kaisinel
        qe.registerQuestNpc(802178).addOnTalkEvent(questId); //Oriata of the Past
        qe.registerQuestNpc(802059).addOnTalkEvent(questId); //Protector Oriata
        qe.registerQuestNpc(205842).addOnTalkEvent(questId); //Ancanus
        qe.registerQuestNpc(203700).addOnTalkEvent(questId); //Fasimedes
        qe.registerQuestNpc(800527).addOnTalkEvent(questId); //Tirins
        qe.registerQuestItem(182215416, questId);
    }

    @Override
    public boolean onZoneMissionEndEvent(QuestEnv env) {
        return defaultOnZoneMissionEndEvent(env);
    }

    @Override
    public boolean onLvlUpEvent(QuestEnv env) {
        return defaultOnLvlUpEvent(env, 10093, true);
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
                case 801327: { //Lord Kaisinel
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 0) {
                                return sendQuestDialog(env, 1011);
                            }
                        }
                        case SETPRO1: {
                            WorldMapInstance newInstance = InstanceService.getNextAvailableInstance(300330000);
                            InstanceService.registerPlayerWithInstance(newInstance, player);
                            TeleportService2.teleportTo(player, 300330000, newInstance.getInstanceId(), 224f, 251f, 125f, (byte) 10, TeleportAnimation.BEAM_ANIMATION);
                            return defaultCloseDialog(env, 0, 1);
                        }
                    }
                    break;
                }
                case 802178: { //Oriata of the Past
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 1) {
                                return sendQuestDialog(env, 1352);
                            } else if (var == 2) {
                                return sendQuestDialog(env, 1693);
                            }
                        }
                        case SETPRO2: {
                            return defaultCloseDialog(env, 1, 2);
                        }
                        case SETPRO3: {
                            giveQuestItem(env, 182215416, 1);
                            Npc npc = (Npc) env.getVisibleObject();
                            npc.getController().onDelete();
                            return defaultCloseDialog(env, 2, 3);
                        }
                    }
                    break;
                }
                case 802059: { //Protector Oriata
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
                case 205842: { //Ancanus
                    switch (dialog) {
                        case QUEST_SELECT: {
                            if (var == 5) {
                                return sendQuestDialog(env, 2716);
                            }
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
            if (targetId == 800527) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            if (player.isInsideZone(ZoneName.get("IDLDF4a_ItemUseArea_Q14095")) && item.getItemId() == 182215416) {
                TeleportService2.teleportTo(player, 600030000, 1, 304, 1719, 295, (byte) 20, TeleportAnimation.BEAM_ANIMATION);

                return HandlerResult.fromBoolean(useQuestItem(env, item, 3, 4, false));
            }
        }
        return HandlerResult.FAILED;
    }

    @Override
    public boolean onEnterWorldEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (player.getWorldId() == 300330000) {
                if (var == 1) {
                    QuestService.addNewSpawn(300330000, player.getInstanceId(), 802178, 243f, 244f, 125f, (byte) 55);
                }
            }
        }
        return false;
    }
}
