package quest.eltnen;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

public class _14022TheTestoftheHeart extends QuestHandler {

	private final static int questId = 14022;

	public _14022TheTestoftheHeart() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnLogOut(questId);
		qe.registerQuestNpc(203900).addOnTalkEvent(questId);
		qe.registerQuestNpc(203996).addOnTalkEvent(questId);
		qe.registerQuestNpc(210799).addOnKillEvent(questId);
		qe.registerQuestNpc(210808).addOnKillEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerOnQuestTimerEnd(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203900: { // Diomedes
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1); // 1
						}
					}
					break;
				}
				case 203996: { // Kimeia
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
							if (var == 8) {
								return defaultCloseDialog(env, 8, 8, true, false);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2);
						}
					}
					break;
				}
			}
		}
		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203996)
			{
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 3398);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
        	public boolean onKillEvent(QuestEnv env) {
		int[] mobs = { 210808, 210799 };
		if (defaultOnKillEvent(env, mobs, 0, 6) || defaultOnKillEvent(env, mobs, 6, true))
			return true;
		else
			return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14020, true);
	}
}
