package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _80303ShovelIt extends QuestHandler {

    private final static int questId = 80303;

    public _80303ShovelIt() {
        super(questId);
    }

    @Override
    public void register() {
        qe.registerQuestNpc(831400).addOnQuestStart(questId);
        qe.registerQuestNpc(831400).addOnTalkEvent(questId);
        qe.registerQuestNpc(701774).addOnTalkEvent(questId);
        qe.registerQuestNpc(219640).addOnKillEvent(questId);

    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 831400) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 4762);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 831400) {
                int var = qs.getQuestVarById(0);

                if (dialog == DialogAction.QUEST_SELECT) {
                    if (var == 3)
                        return sendQuestDialog(env, 2034);
                    if (var == 4)
                        return sendQuestDialog(env, 2375);
                } else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    return checkQuestItems(env, 4, 4, true, 10002, 10001);
                } else if (dialog == DialogAction.SETPRO2) {
                    return defaultCloseDialog(env, 3, 4);
                } else if (dialog == DialogAction.SELECT_QUEST_REWARD) {
                    return sendQuestDialog(env, 5);
                }
            }
            if (targetId == 701774) {
                return true; // just give quest drop on use
            }
        } else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 831400) {
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
            int var = qs.getQuestVarById(0);
            if (var != 3)
                return defaultOnKillEvent(env, 219640, 0, 3);
        }
        return false;
    }
}
