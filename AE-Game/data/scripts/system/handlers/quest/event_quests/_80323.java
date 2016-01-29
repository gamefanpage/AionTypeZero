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

public class _80323 extends QuestHandler {

    private final static int questId = 80323;

    public _80323() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(831428).addOnQuestStart(questId);
        qe.registerQuestNpc(831428).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 831428) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 831428) {
                int var = qs.getQuestVarById(0);

                if (dialog == DialogAction.USE_OBJECT) {
                    if (var == 0) {
                          changeQuestStep(env, 0, 1, false);
                        return sendQuestDialog(env, 1003);
                    }
                    if (var == 1) {
                        changeQuestStep(env, 0, 1, false);
                        return sendQuestDialog(env, 2375);
                    }
                } else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
                    changeQuestStep(env, 0, 1, true);
                    return sendQuestDialog(env, 5);
                }
                else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM)
                    return checkQuestItems(env, 1, 2, true, 5, 2716);

            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 831428) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}

