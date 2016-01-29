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
public class _30364RemembranceOfSpiritsPast extends QuestHandler {

	private final static int questId = 30364;

	public _30364RemembranceOfSpiritsPast() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(204058).addOnTalkEvent(questId);
		qe.registerQuestNpc(204108).addOnTalkEvent(questId);
		qe.registerQuestItem(182209822, questId);
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
			if (targetId == 204108) { // Lanse
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1352);
						}
					}
					case SETPRO1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				}
			}
			else if (targetId == 204058) { // Sif
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 1) {
							return sendQuestDialog(env, 2375);
						}
					}
					case SELECT_QUEST_REWARD: {
						changeQuestStep(env, 1, 1, true); // reward
						return sendQuestDialog(env, 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204058) { // Sif
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
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.FAILED;
	}
}
