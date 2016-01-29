package quest.wisplight_abbey;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _Q19602 extends QuestHandler {

	private final static int questId = 19602;

	public _Q19602() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(804652).addOnQuestStart(questId);
		qe.registerQuestNpc(804652).addOnTalkEvent(questId);
		qe.registerQuestNpc(205985).addOnTalkEvent(questId);
		qe.registerQuestNpc(798155).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("OBSERVATORY_VILLAGE_210060000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804652) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0) {
			if (targetId == 205985) {
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
			if (targetId == 798155) {
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
		if (zoneName == ZoneName.get("OBSERVATORY_VILLAGE_210060000")) {
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
