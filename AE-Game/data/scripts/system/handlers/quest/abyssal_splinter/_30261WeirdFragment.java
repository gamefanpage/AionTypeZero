package quest.abyssal_splinter;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Rikka
 */
public class _30261WeirdFragment extends QuestHandler {

	private final static int questId = 30261;

	public _30261WeirdFragment() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(278533).addOnTalkEvent(questId);
		qe.registerQuestNpc(279029).addOnTalkEvent(questId);
		qe.registerQuestNpc(260264).addOnTalkEvent(questId);
		qe.registerQuestItem(182209800, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			return false;
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 278533: { // Rentia
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
					break;
				}
				case 279029: { // Lugbug
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2, true, false); // reward
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 260264) { // Aratus
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (QuestService.startQuest(env)) {
				removeQuestItem(env, 182209800, 1);
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.FAILED;
	}
}
