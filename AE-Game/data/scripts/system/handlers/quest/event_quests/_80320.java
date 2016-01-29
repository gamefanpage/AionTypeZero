package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _80320 extends QuestHandler {

    private final static int questId = 80320;

    public _80320() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(831426).addOnQuestStart(questId);
        qe.registerQuestNpc(831426).addOnTalkEvent(questId);
        qe.registerQuestNpc(831427).addOnTalkEvent(questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 831426) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 831427) {
                int var = qs.getQuestVarById(0);

                if (dialog == DialogAction.QUEST_SELECT) {
                    if (var == 0) {
                        changeQuestStep(env, 0, 1, false);
                        return sendQuestDialog(env, 1352);
                    }
                    if (var == 1)
                        return sendQuestDialog(env, 2375);
                } else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    return checkQuestItems(env, 1, 2, true, 5, 2716);
                } else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 831427) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

}
