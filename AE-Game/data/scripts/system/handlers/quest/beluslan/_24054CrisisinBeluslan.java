package quest.beluslan;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _24054CrisisinBeluslan extends QuestHandler {

	private final static int questId = 24054;
	private final static int[] npc_ids = { 204702, 278001, 204807, 204052, 802053 };

	public _24054CrisisinBeluslan() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZone(ZoneName.get("MALEK_MINE_220040000"), questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(233865).addOnKillEvent(questId);
		qe.registerQuestNpc(702041).addOnKillEvent(questId);
		for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 24050, true);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
		switch (targetId) {
			case 233865:
				if (qs.getQuestVarById(0) == 5) {
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
				}
				break;
            case 702041:
                if (qs.getQuestVarById(0) >= 2 && qs.getQuestVarById(0) < 5) {
                    qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
                    updateQuestStatus(env);
                }
                break;
		}
		return false;
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 204052) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
			return false;
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204702) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
					else if (var == 2)
						return sendQuestDialog(env, 1693);
					else if (var == 6)
						return sendQuestDialog(env, 2375);
				case SELECT_ACTION_1694: {
                    playQuestMovie(env, 255);
                    break;
                }
				case SETPRO1:
					if (var == 0) {
						return defaultCloseDialog(env, 0, 1); // 1
					}
				case SETPRO3:
					if (var == 2) {
						return defaultCloseDialog(env, 2, 3); // 3
					}
				case SET_SUCCEED:
					if (var == 6) {
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
						return true;
					}
			}
		}
		else if (targetId == 802053) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 1)
						return sendQuestDialog(env, 1352);
				case SETPRO2:
					if (var == 1) {
						if (!giveQuestItem(env, 182204320, 1))
							return true;
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		else if (targetId == 204807) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 3)
						return sendQuestDialog(env, 2034);
				case SETPRO4:
					if (var == 3) {
						qs.setQuestVarById(0, var + 1);
						updateQuestStatus(env);
						PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
						return true;
					}
			}
		}
		return false;
	}
}
