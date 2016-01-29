package quest.fatebound_abbey;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _Q29602 extends QuestHandler {

	private final static int questId = 29602;

	public _Q29602() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(804663).addOnQuestStart(questId);
		qe.registerQuestNpc(804663).addOnTalkEvent(questId);
		qe.registerQuestNpc(205986).addOnTalkEvent(questId);
		qe.registerQuestNpc(205150).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("BALTASAR_HILL_VILLAGE_220050000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804663) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (targetId == 205986) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case SET_SUCCEED:
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
				}
			}
		}
		else if ((qs.getStatus() == QuestStatus.REWARD)) {
			if (targetId == 205150) {
				switch (env.getDialog()) {
					case USE_OBJECT:
						return sendQuestDialog(env, 10002);
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName == ZoneName.get("BALTASAR_HILL_VILLAGE_220050000")) {
			Player player = env.getPlayer();
			if (player == null)
				return false;
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var == 1) {
					changeQuestStep(env, 1, 1, true);
					return true;
				}
			}
		}
		return false;
	}
}
