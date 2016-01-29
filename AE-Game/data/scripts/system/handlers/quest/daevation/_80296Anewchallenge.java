package quest.daevation;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Romanz
 */
public class _80296Anewchallenge extends QuestHandler {

	private static final int questId = 80296;
	private final static int dialogs[] = { 1013, 1034, 1055, 1076, 5103, 1098, 1119, 1140, 1161, 5104, 1183, 1204, 1225, 1246,
			5105, 1268, 1289, 1310, 1331, 5106, 2376, 2461, 2546, 2631, 2632};
	private final static int items[] = { 110601357, 113601309, 114601306, 112601300, 111601320,  110301410, 113301375, 114301410, 112301294, 111301351,
			110101514, 113101376, 114101404, 112101313, 111101359,  110501383, 113501356, 114501364, 112501281, 111501341,  110301670, 113301639, 114301674, 112301547, 111301608};
	private int choice = 0;
	private int item;


	public _80296Anewchallenge() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(831388).addOnQuestStart(questId);
		qe.registerQuestNpc(831388).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int dialogId = env.getDialogId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 831388) {
				if (dialogId == DialogAction.EXCHANGE_COIN.id()) {
					QuestService.startQuest(env);
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 831388) {
				if (dialogId == DialogAction.EXCHANGE_COIN.id()) {
					return sendQuestDialog(env, 1011);
				}
				switch (dialogId) {
					case 1012:
					case 1097:
					case 1182:
					case 1267:
					case 2375:
						return sendQuestDialog(env, dialogId);
					case 1013:
					case 1034:
					case 1055:
					case 1076:
					case 5103:
					case 1098:
					case 1119:
					case 1140:
					case 1161:
					case 5104:
					case 1183:
					case 1204:
					case 1225:
					case 1246:
					case 5105:
					case 1268:
					case 1289:
					case 1310:
					case 1331:
					case 5106:
					case 2376:
					case 2461:
					case 2546:
					case 2631:
					case 2632:{
						item = getItem(dialogId);
						if (player.getInventory().getItemCountByItemId(item) > 0)
							return sendQuestDialog(env, 1013);
						else
							return sendQuestDialog(env, 1352);
					}
					case 10000:
					case 10001:
					case 10002:
					case 10003: {
						if (player.getInventory().getItemCountByItemId(186000041) < 10) // Daevanion's Light
							return sendQuestDialog(env, 1009);
						changeQuestStep(env, 0, 0, true);
						choice = dialogId - 10000;
						return sendQuestDialog(env, choice + 5);
					}
					case 10004:
						if (player.getInventory().getItemCountByItemId(186000041) < 10) // Daevanion's Light
							return sendQuestDialog(env, 1009);
						changeQuestStep(env, 0, 0, true);
						choice = dialogId - 10000;
						return sendQuestDialog(env, 45);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 831388) {
				removeQuestItem(env, item, 1);
				removeQuestItem(env, 186000041, 10);
				return sendQuestEndDialog(env, choice);
			}
		}
		return false;
	}

	private int getItem(int dialogId) {
		int x = 0;
		for (int id : dialogs) {
			if (id == dialogId)
				break;
			x++;
		}
		return (items[x]);
	}
}
