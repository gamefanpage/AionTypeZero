package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Romanz

public class _80266PreparingBirthdayCelebrationFeast extends QuestHandler {

	private final static int questId = 80266;

	public _80266PreparingBirthdayCelebrationFeast() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestItem(182215211, questId);
		qe.registerQuestNpc(831167).addOnQuestStart(questId);
		qe.registerQuestNpc(831167).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 831167) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					case ASK_QUEST_ACCEPT:
						return sendQuestDialog(env, 4);
					case QUEST_ACCEPT_1: {
						return sendQuestStartDialog(env, 182215211, 1);
					}
					case QUEST_REFUSE_1:
						return sendQuestDialog(env, 1004);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 831167) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 5);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, true));
		}
		return HandlerResult.FAILED;
	}
}
