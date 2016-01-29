package quest.altgard;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _24010SuthranOrders extends QuestHandler {

	private final static int questId = 24010;

	public _24010SuthranOrders() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203557).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("ALTGARD_FORTRESS_220030000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		if (targetId != 203557)
			return false;
		if (qs.getStatus() == QuestStatus.START) {
			if (env.getDialog() == DialogAction.QUEST_SELECT) {
				qs.setQuestVar(1);
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return sendQuestDialog(env, 1011);
			}
			else
				return sendQuestStartDialog(env);
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (env.getDialogId() == DialogAction.SELECTED_QUEST_NOREWARD.id()) {
				int[] ids = { 24011, 24012, 24013, 24014, 24015, 24016 };
				for (int id : ids) {
					QuestEngine.getInstance().onEnterZoneMissionEnd(
						new QuestEnv(env.getVisibleObject(), env.getPlayer(), id, env.getDialogId()));
				}
			}
			return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		return defaultOnEnterZoneEvent(env, zoneName, ZoneName.get("ALTGARD_FORTRESS_220030000"));
	}
}
