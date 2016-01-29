package quest.fort_tiamat;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Romanz
 */
public class _Q30723 extends QuestHandler {

	private final static int questId = 30723;

	public _Q30723() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804897).addOnTalkEvent(questId);
		qe.registerQuestNpc(804898).addOnTalkEvent(questId);
		qe.registerQuestItem(182215691, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			return false;
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 804897 || targetId == 804898) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (QuestService.startQuest(env)) {
				changeQuestStep(env, 0, 0, true); // reward
				return HandlerResult.fromBoolean(sendQuestDialog(env, 4));
			}
		}
		return HandlerResult.UNKNOWN;
	}
}
