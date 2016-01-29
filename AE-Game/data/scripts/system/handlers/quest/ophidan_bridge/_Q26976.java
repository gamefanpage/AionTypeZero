package quest.ophidan_bridge;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


public class _Q26976 extends QuestHandler {

	private final static int questId = 26976;

	public _Q26976() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(801764).addOnQuestStart(questId);
		qe.registerQuestNpc(801764).addOnTalkEvent(questId);
		qe.registerQuestNpc(801765).addOnTalkEvent(questId);
	}

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 801764) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 4762);
                }
                else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        else if(qs.getStatus() == QuestStatus.START) {
            if (targetId == 801765) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                }
                else if (dialog == DialogAction.SETPRO1) {
                    return defaultCloseDialog(env, 0, 1); // 1
                }
            }
            else if(targetId == 801764) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1352);
                }
                else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    checkQuestItems(env, 1, 2, true, 5, 0);
                    return true;
                }
            }
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if(targetId == 801764){
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
