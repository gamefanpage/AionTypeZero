package quest.event_quests;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _80033EventAvertingTheGaze extends QuestHandler {

	private final static int questId = 80033;

	public _80033EventAvertingTheGaze() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799781).addOnQuestStart(questId);
		qe.registerQuestNpc(799781).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE)
			if (env.getDialog() == DialogAction.QUEST_SELECT) {
				return sendQuestDialog(env, 1011);
			} else {
				return sendQuestStartDialog(env);
			}

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799781) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else if (env.getDialog() == DialogAction.ASK_QUEST_ACCEPT) {
					Storage storage = player.getInventory();
					if (storage.getItemCountByItemId(164002015) > 0)
						return sendQuestDialog(env, 2375);
					else
						return sendQuestDialog(env, 2716);
				}
				else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					if (qs.getQuestVarById(0) == 0)
						defaultCloseDialog(env, 0, 1, true, true, 0, 0, 164002015, 1);
					return sendQuestDialog(env, 5);
				}
				else if (env.getDialog() == DialogAction.SELECTED_QUEST_NOREWARD)
					return sendQuestRewardDialog(env, 799781, 5);
				else
					return sendQuestStartDialog(env);
			}
		}
		return sendQuestRewardDialog(env, 799781, 0);
	}
}
