package quest.pradeth_idgel_research_center;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _28014CanYouHeartheScreams extends QuestHandler {

	private final static int questId = 28014;

	public _28014CanYouHeartheScreams() {
		super(questId);
	}

	@Override
	public void register() {
        qe.registerQuestNpc(800919).addOnQuestStart(questId);
        qe.registerQuestNpc(800919).addOnTalkEvent(questId);
		qe.registerQuestItem(182215257, questId);

	}

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        int targetId = env.getTargetId();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 800919) {
                if (dialog == DialogAction.QUEST_SELECT) {
                           if (player.getInventory().getItemCountByItemId(182215257) == 0)
                           {
                               giveQuestItem(env, 182215257,1);
                           }
                    return sendQuestDialog(env, 1011);
                }
                else {
                    return sendQuestStartDialog(env);
                }
            }
        }
        else if (qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (targetId == 800919) {
                if (dialog == DialogAction.QUEST_SELECT)
                    if (var == 1)
                        return sendQuestDialog(env, 2375);
                if (dialog == DialogAction.SELECT_QUEST_REWARD){
                    changeQuestStep(env, 1, 2, true); // reward
                    return sendQuestDialog(env, 5);
                }
            }
        }
        else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 800919) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var == 0) {
					return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, false)); // 1
			}
		}
		return HandlerResult.SUCCESS; // ??
	}
}
