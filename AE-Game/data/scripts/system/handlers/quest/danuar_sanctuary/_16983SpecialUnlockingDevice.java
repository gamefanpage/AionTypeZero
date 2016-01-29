package quest.danuar_sanctuary;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Romanz
 *
 */
public class _16983SpecialUnlockingDevice extends QuestHandler {

	private final static int questId = 16983;

	public _16983SpecialUnlockingDevice() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(801953).addOnQuestStart(questId);
		qe.registerQuestNpc(801953).addOnTalkEvent(questId);
		qe.registerQuestNpc(801543).addOnTalkEvent(questId);
		qe.registerQuestNpc(701862).addOnTalkEvent(questId);
		qe.registerQuestNpc(701863).addOnTalkEvent(questId);
		qe.registerQuestNpc(701864).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 801953) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					case QUEST_ACCEPT_SIMPLE: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
			else if (targetId == 701862 || targetId == 701863 || targetId == 701864) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
				int var = qs.getQuestVarById(0);
							if (var == 0) {
								return useQuestObject(env, 0, 1, true, true);
							}
				}
			}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 801543) {
				if (dialog == DialogAction.USE_OBJECT) {
					return sendQuestDialog(env, 10002);
				}
				else {
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
