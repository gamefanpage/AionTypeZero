package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

//By Evil_dnk

public class _80315 extends QuestHandler {

    private final static int questId = 80315;

    public _80315() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(831423).addOnTalkEvent(questId);
        qe.registerQuestNpc(831424).addOnTalkEvent(questId);
        qe.registerQuestItem(182215304, questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();

       int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        if (targetId == 0) {
            if (env.getDialogId() == 1002) {
                QuestService.startQuest(env);
                PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
                return true;
            }
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
            if (targetId == 831423) {
                if (dialog == DialogAction.USE_OBJECT) {
                    if (var == 0) {
                        changeQuestStep(env, 0, 1, false);
                        return true;
                    }

                }
            }
            if (targetId == 831424) {
                if (dialog == DialogAction.USE_OBJECT) {
                    if (var == 0) {
                        changeQuestStep(env, 0, 1, false);
                        return sendQuestDialog(env, 2375);
                    }
                }
                if (dialog == DialogAction.QUEST_SELECT) {
                    if (var == 1)
                        return sendQuestDialog(env, 2375);
                } else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
                    changeQuestStep(env, 1, 2, true);
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 831424) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
        final Player player = env.getPlayer();
        final int id = item.getItemTemplate().getTemplateId();
        final int itemObjId = item.getObjectId();

        if (id != 182215304)
            return HandlerResult.UNKNOWN;
        PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
                0), true);
        ThreadPoolManager.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
                        1, 0), true);
                sendQuestDialog(env, 4);
            }

        }, 3000);
        return HandlerResult.SUCCESS;
    }
}

