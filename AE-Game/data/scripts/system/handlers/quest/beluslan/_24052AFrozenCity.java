package quest.beluslan;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _24052AFrozenCity extends QuestHandler {

	private final static int questId = 24052;
	private final static int[] npc_ids = { 204753, 790016, 730036, 279000 };

	public _24052AFrozenCity() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestItem(182215378, questId);
		qe.registerQuestItem(182215379, questId);
		qe.registerQuestItem(182215380, questId);
		qe.registerOnEnterZoneMissionEnd(questId);
        qe.registerQuestNpc(233864).addOnKillEvent(questId);
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
		return defaultOnLvlUpEvent(env, 24050, true);
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
			if (targetId == 204753) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					int[] questItems = { 182215378, 182215379, 182215380 };
					return sendQuestEndDialog(env, questItems);
				}
			}
		}
		else if (qs.getStatus() != QuestStatus.START) {
			return false;
		}
		if (targetId == 204753) {
			switch (env.getDialog()) {
				case QUEST_SELECT:
					if (var == 0)
						return sendQuestDialog(env, 1011);
					else if (var == 1)
						return sendQuestDialog(env, 2375);
				case SELECT_ACTION_1012:
					playQuestMovie(env, 242);
					break;
				case SELECT_ACTION_2376:
					if (QuestService.collectItemCheck(env, false))
						return sendQuestDialog(env, 2376);
					else
						return sendQuestDialog(env, 2461);
				case SETPRO1:
					ItemService.addItem(player, 182215378, 1);
					ItemService.addItem(player, 182215379, 1);
					ItemService.addItem(player, 182215380, 1);
					qs.setQuestVarById(0, 1);
					updateQuestStatus(env);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 0));
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (!player.isInsideZone(ZoneName.get("DF3_ITEMUSEAREA_Q2056")))
			return HandlerResult.FAILED;

		if (id != 182215378 && qs.getQuestVarById(0) == 1 || id != 182215379 && qs.getQuestVarById(0) == 2
			|| id != 182215380 && qs.getQuestVarById(0) == 3)
			return HandlerResult.UNKNOWN;

		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 2000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				if (qs.getQuestVarById(0) == 1) {
					playQuestMovie(env, 243);
					removeQuestItem(env, id, 1);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
				}
				else if (qs.getQuestVarById(0) == 2) {
					playQuestMovie(env, 244);
					removeQuestItem(env, id, 1);
					qs.setQuestVarById(0, qs.getQuestVarById(0) + 1);
					updateQuestStatus(env);
				}
				else if (qs.getQuestVarById(0) == 3 && qs.getStatus() != QuestStatus.COMPLETE
					&& qs.getStatus() != QuestStatus.NONE) {
					removeQuestItem(env, id, 1);
					playQuestMovie(env, 245);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
				}
			}
		}, 2000);
		return HandlerResult.SUCCESS;
	}
    @Override
    public boolean onKillEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null || qs.getStatus() != QuestStatus.START)
            return false;

        int var = qs.getQuestVarById(0);
        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();

        if (targetId == 233864 && var == 4) {
            qs.setQuestVarById(0, var + 1);
            updateQuestStatus(env);
            return true;
        }
        return false;
    }
}
