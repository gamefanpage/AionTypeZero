package quest.altgard;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _24013PoisonIntheWaters extends QuestHandler {

	private final static int questId = 24013;
	private final static int[] mobs = { 210455, 210456, 214039, 210458, 214032 };

	public _24013PoisonIntheWaters() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203631).addOnTalkEvent(questId);
		qe.registerQuestNpc(203621).addOnTalkEvent(questId);
		for (int mob : mobs) {
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		}
		qe.registerQuestItem(182215359, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203631: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
							return sendQuestDialog(env, 1011);
						}
						case SELECT_ACTION_1012: {
							playQuestMovie(env, 63);
							return sendQuestDialog(env, 1012);
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
					break;
				}
				case 203621: {
					switch (env.getDialog()) {
						case QUEST_SELECT: {
								return sendQuestDialog(env, 1352);
						}
						case SETPRO2: {
							giveQuestItem(env, 182215359, 1);
							return defaultCloseDialog(env, 1, 2);
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203631) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 2375);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			if (var >= 3 && var < 7) {
				return defaultOnKillEvent(env, mobs, var, var + 1);
			}
			else if (var == 7) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		if (player.isInsideZone(ZoneName.get("DF1A_ITEMUSEAREA_Q2016"))) {
			return HandlerResult.fromBoolean(useQuestItem(env, item, 2, 3, false));
		}
		return HandlerResult.FAILED;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 24010, true);
	}
}
