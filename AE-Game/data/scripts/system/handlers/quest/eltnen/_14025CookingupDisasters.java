package quest.eltnen;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

public class _14025CookingupDisasters extends QuestHandler {

	private final static int questId = 14025;
        	private final static int[] mob_ids = { 212029, 211862, 211863, 212025, 212039, 212351 };

	public _14025CookingupDisasters() {
		super(questId);
	}

	@Override
	public void register() {
		int[] npc_ids = { 203989, 203901, 204020 };
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int mob_id : mob_ids) {
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
		}
		for (int npc_id : npc_ids) {
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		if (qs == null) {
			return false;
		}
		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203989) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 0) {
							return sendQuestDialog(env, 1011);
						}
						if (var == 1) {
							return sendQuestDialog(env, 1352);
						}
						if (var == 4) {
							return sendQuestDialog(env, 2034);
						}
                                                else  {
							return sendQuestDialog(env, 2716);
						}
					}
					case SETPRO1: {
						return defaultCloseDialog(env, 0, 1); // 1
					}
					case CHECK_USER_HAS_QUEST_ITEM: {
						return checkQuestItems(env, 1, 2, false, 1438, 10001);
					}
					case SETPRO2: {
						return defaultCloseDialog(env, 2, 3); // 3
					}
					case SETPRO4: {
						return defaultCloseDialog(env, 4, 5); // 5
					}
					case SETPRO6: {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return closeDialogWindow(env);
					}
				}
			}
			else if (targetId == 204020) {
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 3) {
							return sendQuestDialog(env, 1693);
						}
					}
					case SETPRO3: {
						return defaultCloseDialog(env, 3, 4); // 4
					}
				}
			}
			else if (targetId == 203901) { // Telemachus
				switch (dialog) {
					case QUEST_SELECT: {
						if (var == 2) {
							return sendQuestDialog(env, 1352);
						}
					}
					case CHECK_USER_HAS_QUEST_ITEM: {
						return checkQuestItems(env, 2, 2, true, 5, 1438); // reward
					}
					case FINISH_DIALOG: {
						return sendQuestSelectionDialog(env);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203901) { // Telemachus
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() != QuestStatus.START)
			return false;
		if ((targetId == 212029 && qs.getQuestVarById(1) < 5) || (targetId == 211862 && qs.getQuestVarById(1) < 5 || (targetId == 211863 && qs.getQuestVarById(1) < 5)
                        || (targetId == 212025 && qs.getQuestVarById(1) < 5) || (targetId == 212039 && qs.getQuestVarById(1) < 5))) {
			return defaultOnKillEvent(env, mob_ids, 0, 4, 1);
		}
		if ((targetId == 212351 && qs.getQuestVarById(2) < 2)) {
			return defaultOnKillEvent(env, mob_ids, 0, 1, 2);
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14024, true);
	}
}
