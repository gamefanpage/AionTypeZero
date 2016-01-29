package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _80307ChocolateExtraBitter extends QuestHandler {

	private final static int questId = 80307;

	public _80307ChocolateExtraBitter() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestItem(182215297, questId);
		qe.registerQuestNpc(831397).addOnQuestStart(questId);
		qe.registerQuestNpc(831397).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 831397) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.START) {
			if (targetId == 831397) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    return checkQuestItems(env, 0, 1, false, 10000, 10001);
				}
                else if (dialog == DialogAction.SETPRO2) {
                    giveQuestItem(env, 182215297, 1);
                    return closeDialogWindow(env);
			}
		}
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if(targetId == 831397){
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
            if (qs == null)
            {
                return HandlerResult.FAILED;
            }
            if (qs.getStatus() != QuestStatus.START && qs.getQuestVarById(0) != 1){
            return HandlerResult.FAILED;
            }
            if (id != 182215297)
            {
                return HandlerResult.FAILED;
            }

            if (player.getTarget().getObjectTemplate().getTemplateId() == 831403) {
            removeQuestItem(env, 182215297, 1);
            changeQuestStep(env, 1, 2, true);
            return HandlerResult.SUCCESS;
        }
        return HandlerResult.FAILED;
    }

}

