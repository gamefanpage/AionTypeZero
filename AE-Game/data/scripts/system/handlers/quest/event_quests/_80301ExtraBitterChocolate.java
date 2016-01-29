package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


public class _80301ExtraBitterChocolate extends QuestHandler {

	private final static int questId = 80301;

	public _80301ExtraBitterChocolate() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(831395).addOnQuestStart(questId);
		qe.registerQuestNpc(831395).addOnTalkEvent(questId);
		qe.registerQuestItem(182215291, questId);
	}

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
            if (targetId == 831395) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 4762);
                }
                else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        else if(qs.getStatus() == QuestStatus.START) {
            if (targetId == 831395) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                }
                else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    return checkQuestItems(env, 0, 1, false, 10000, 10001);
                }
                else if (dialog == DialogAction.SETPRO2) {
                    giveQuestItem(env, 182215291, 1);
                    return closeDialogWindow(env);
                }
            }
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if(targetId == 831395){
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

    @Override
    public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        final int id = item.getItemTemplate().getTemplateId();
        if (qs == null && qs.getStatus() != QuestStatus.START && qs.getQuestVarById(0) != 1){
            return HandlerResult.FAILED;
        }
        if (id != 182215291)
        {
            return HandlerResult.FAILED;
        }

        if (player.getTarget().getObjectTemplate().getTemplateId() == 831402) {
            removeQuestItem(env, 182215291, 1);
            changeQuestStep(env, 1, 2, true);
            return HandlerResult.SUCCESS;
        }
        return HandlerResult.FAILED;
    }
}
