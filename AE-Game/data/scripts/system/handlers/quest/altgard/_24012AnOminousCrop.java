package quest.altgard;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 *
 */
public class _24012AnOminousCrop extends QuestHandler {

	private final static int questId = 24012;

	public _24012AnOminousCrop() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203605).addOnTalkEvent(questId);
		qe.registerQuestNpc(700096).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("MUMU_FARMLAND_220030000"), questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203605: {
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							else if (var == 5)
								return sendQuestDialog(env, 2716);
						case SETPRO1:
							return defaultCloseDialog(env, 0, 1);
						case CHECK_USER_HAS_QUEST_ITEM:
							return checkQuestItems(env, 5, 6, true, 5, 10001);
					}
					break;
				}
				case 700096: {
					switch (env.getDialog()) {
						case USE_OBJECT: {
							if (var >= 2 && var < 5) {
								return useQuestObject(env, var, var + 1, false, true);
							}
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203605)
				return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName == ZoneName.get("MUMU_FARMLAND_220030000")) {
			Player player = env.getPlayer();
			if (player == null)
				return false;
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START) {
				int var = qs.getQuestVarById(0);
				if (var == 1) {
					changeQuestStep(env, 1, 2, false);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 24011);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 24010, true);
	}
}
