package quest.verteron;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
public class _14013AFrillofaFuss extends QuestHandler {

	private final static int questId = 14013;
	private final static int[] mob_ids = { 210126, 210200, 210201, 210202 };

	public _14013AFrillofaFuss() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203129).addOnTalkEvent(questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int mob_id : mob_ids)
			qe.registerQuestNpc(mob_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14010, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203129) {
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
						else if (var == 6)
							return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1012:
						playQuestMovie(env, 27);
						return sendQuestDialog(env, 1012);
					case SETPRO1:
					case SETPRO2:
						if (var == 0 || var == 6) {
							qs.setQuestVarById(0, var + 1);
							updateQuestStatus(env);
							PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
							return true;
						}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203129) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 5);
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
		if ((targetId == 210126 && qs.getQuestVarById(1) < 6)) {
			return defaultOnKillEvent(env, mob_ids, 0, 5, 1);
		}
		if ((targetId == 210200 && qs.getQuestVarById(2) < 8 || (targetId == 210201 && qs.getQuestVarById(1) < 8))) {
			return defaultOnKillEvent(env, mob_ids, 0, 7, 2);
		}
		if ((targetId == 210202 && qs.getQuestVarById(3) <2)) {
			return defaultOnKillEvent(env, 210202, 1, true);
		}
		return false;
	}
}
