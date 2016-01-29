package quest.rentus_base;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Romanz
 *
 */
public class _30500Desperation extends QuestHandler{

	private static final int questId = 30500;

	public _30500Desperation() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804876).addOnQuestStart(questId);
		qe.registerQuestNpc(804876).addOnTalkEvent(questId);
		qe.registerQuestNpc(804879).addOnTalkEvent(questId);
		qe.registerQuestNpc(799592).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 804876) {
				switch (dialog) {
					case QUEST_SELECT:{
						return sendQuestDialog(env, 1011);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (targetId == 804879) {
				switch (dialog) {
					case QUEST_SELECT:{
						return sendQuestDialog(env, 1352);
					}
					case SETPRO1:{
						changeQuestStep(env, 0, 1, false);
						return closeDialogWindow(env);
					}
				}
			}
			else if(targetId == 799592){
				switch (dialog) {
					case QUEST_SELECT:{
						return sendQuestDialog(env, 2375);
					}
					case SELECT_QUEST_REWARD:{
						changeQuestStep(env, 1, 1, true);
						return closeDialogWindow(env);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if(targetId == 799592){
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
