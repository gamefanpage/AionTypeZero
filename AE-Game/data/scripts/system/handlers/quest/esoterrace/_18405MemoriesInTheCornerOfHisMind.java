package quest.esoterrace;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Vincas
 */
public class _18405MemoriesInTheCornerOfHisMind extends QuestHandler {

	public static final int questId = 18405;
	public static final int npcDaidra = 799553, npcTillen = 799552;

	public _18405MemoriesInTheCornerOfHisMind() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(npcDaidra).addOnTalkEvent(questId);
		qe.registerQuestNpc(npcTillen).addOnTalkEvent(questId);
		qe.registerQuestItem(182215002, questId);
	}

	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();

		if (env.getTargetId() == 0 && env.getDialog() == DialogAction.QUEST_ACCEPT_1) {
			QuestService.startQuest(env);
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(0, 0));
			return true;
		}

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		if (qs.getStatus() == QuestStatus.START) {
			switch (env.getTargetId()) {
				case npcDaidra:
					if (qs.getQuestVarById(0) == 0) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 1352);
						else if (env.getDialog() == DialogAction.SETPRO1)
							return defaultCloseDialog(env, 0, 1, 182215024, 1, 182215002, 1);
					}
				case npcTillen:
					if (qs.getQuestVarById(0) == 1) {
						if (env.getDialog() == DialogAction.QUEST_SELECT)
							return sendQuestDialog(env, 2375);
						else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD)
							removeQuestItem(env, 182215024, 1);
						return defaultCloseDialog(env, 1, 2, true, true);
					}
			}
		}
		return sendQuestRewardDialog(env, npcTillen, 0);
	}

	@Override
	public HandlerResult onItemUseEvent(final QuestEnv env, Item item) {
		final Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();
		final int itemObjId = item.getObjectId();

		if (id != 182215002)
			return HandlerResult.FAILED;
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 3000, 0,
			0), true);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), itemObjId, id, 0,
					1, 0), true);
				sendQuestDialog(env, 4);
			}
		}, 3000);
		return HandlerResult.SUCCESS;
	}
}
