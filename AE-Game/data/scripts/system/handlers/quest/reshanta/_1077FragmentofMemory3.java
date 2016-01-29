package quest.reshanta;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

public class _1077FragmentofMemory3 extends QuestHandler {

	private final static int questId = 1077;
	private final static int[] npc_ids = { 203704, 798154, 204574, 204652, 204653, 278500 };

	public _1077FragmentofMemory3() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(214598).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 1701, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 278500) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 203704) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
				case SETPRO1:
					if (var == 0) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						TeleportService2.teleportTo(player, 210060000, 2275.42f, 2218.12f, 59f, (byte) 15, TeleportAnimation.BEAM_ANIMATION);
						return true;
					}
			}
		}
		else if (targetId == 798154) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SETPRO2:
					if (var == 1) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						TeleportService2.teleportTo(player, 210040000, 711.14f, 629.1f, 130f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
						return true;
					}
			}
		}
		else if (targetId == 204574) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 2)
						return sendQuestDialog(env, 1693);
				case SETPRO3:
					if (var == 2) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		else if (targetId == 204652) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3)
						return sendQuestDialog(env, 2034);
					else if (var == 6)
						return sendQuestDialog(env, 3057);
					else if (var != 3)
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10009));
				case SETPRO10:
					if (var == 3) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
					player.setState(CreatureState.FLIGHT_TELEPORT);
					player.unsetState(CreatureState.ACTIVE);
					player.setFlightTeleportId(71001);
					PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 71001, 0));
					return true;
				case SET_SUCCEED:
					if (var == 6) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						TeleportService2.teleportTo(player, 400010000, 2958.9f, 672.34f, 1521.7f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		else if (targetId == 204653) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 4)
						return sendQuestDialog(env, 2375);
					else if (var != 4)
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10010));
				case SELECT_ACTION_2376:
					if (var == 4) {
						playQuestMovie(env, 421);
						break;
					}
				case SETPRO11:
					if (var == 4) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
					}
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
                    player.setState(CreatureState.FLIGHT_TELEPORT);
                    player.unsetState(CreatureState.ACTIVE);
                    player.setFlightTeleportId(72001);
					PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FLYTELEPORT, 72001, 0));
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		if (defaultOnKillEvent(env, 214598, 5, 6)) {
			playQuestMovie(env, 422);
			return true;
		}
		else
			return false;
	}
}
