package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _80030EventAnUnwelcomeGaze extends QuestHandler {

	private final static int questId = 80030;

	public _80030EventAnUnwelcomeGaze() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799766).addOnQuestStart(questId);
		qe.registerQuestNpc(799766).addOnTalkEvent(questId);
		qe.registerOnLevelUp(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if (qs == null || qs.getStatus() == QuestStatus.NONE)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 799766) {
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
					return sendQuestRewardDialog(env, 799766, 5);
				else
					return sendQuestStartDialog(env);
			}
		}
		return sendQuestRewardDialog(env, 799766, 0);
	}
}
