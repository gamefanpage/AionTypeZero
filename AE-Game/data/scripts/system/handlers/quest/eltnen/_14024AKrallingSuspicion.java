package quest.eltnen;

import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

public class _14024AKrallingSuspicion extends QuestHandler {

	private final static int questId = 14024;
	private final static int[] npc_ids = { 203904, 204045, 204003, 204004, 204020, 203901 };

	public _14024AKrallingSuspicion() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14020, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204020){
				return sendQuestEndDialog(env);
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 203904) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204045) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SELECT_ACTION_1354:
					if (var == 1)
						playQuestMovie(env, 32);
					break;
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						TeleportService2.teleportTo(player, 210020000, 1357f, 2566f, 279.6f, (byte) 89, TeleportAnimation.BEAM_ANIMATION);
						return true;
					}
					return false;
			}
		}
		else if (targetId == 204004) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 2034);
				case CHECK_USER_HAS_QUEST_ITEM: {
							return checkQuestItems(env, 2, 2, true, 10000, 10001);
						}

			}
		}
		return false;
	}
}
