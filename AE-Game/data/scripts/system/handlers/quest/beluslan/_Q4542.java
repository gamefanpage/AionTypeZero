package quest.beluslan;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

public class _Q4542 extends QuestHandler {

	private final static int questId = 4542;
	private final static int[] npc_ids = { 204768, 204743, 204808 };

	public _Q4542() {
		super(questId);
	}

	@Override
	public void register() {
        qe.registerQuestNpc(204768).addOnQuestStart(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}


	@Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (env.getTargetId() == 204768) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 4762);
                }
                else {
                    return sendQuestStartDialog(env, 182204310, 1);
                }
            }
        }

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
                 if (env.getTargetId() == 204768) {
                if (env.getDialog() == DialogAction.QUEST_SELECT) {
							if (var == 1)
								return sendQuestDialog(env, 1352);
							if (var == 5 && player.getInventory().getItemCountByItemId(182204321) >= 1)
								return sendQuestDialog(env, 2716);
                }
                    if (env.getDialog() == DialogAction.SETPRO2)
							return defaultCloseDialog(env, 1, 2); // 3
                    if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD)
                    {
							removeQuestItem(env, 182204321, 1);
							return defaultCloseDialog(env, 5, 5, true, true); // reward
						}
                    if (env.getDialog() == DialogAction.SETPRO6) {
							playQuestMovie(env, 239);
							return closeDialogWindow(env);
						}
					}
            if (env.getTargetId() == 204743){
                if (env.getDialog() == DialogAction.QUEST_SELECT)
				   if (var == 0)
						return sendQuestDialog(env, 1011);
                    if (env.getDialog() == DialogAction.SETPRO1)
							return defaultCloseDialog(env, 0, 1, 182204311, 1, 182204310, 1); // 2
					}
            if (env.getTargetId() == 204808){
                if (env.getDialog() == DialogAction.QUEST_SELECT)    {
							if (var == 2)
								return sendQuestDialog(env, 1693);
							if (var == 3)
								return sendQuestDialog(env, 2034);
							if (var == 4)
								return sendQuestDialog(env, 2375);
                        }
                if (env.getDialog() == DialogAction.SETPRO3)
                            {
								playQuestMovie(env, 240);
								return defaultCloseDialog(env, 2, 3, 0, 0, 182204311, 1); // 3
							}
                if (env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM)
							return checkQuestItems(env, 3, 4, false, 10000, 10001); // 4
                if (env.getDialog() == DialogAction.FINISH_DIALOG) {
							if (var == 4)
								defaultCloseDialog(env, 4, 4); // 3
							if (var == 3)
								defaultCloseDialog(env, 3, 3); // 3
                }
                if (env.getDialog() == DialogAction.SETPRO5)
							return defaultCloseDialog(env, 4, 5, false, false, 182204321, 1, 0, 0); // 5
					}

		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (env.getTargetId() == 204768) { // Sleipnir
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
