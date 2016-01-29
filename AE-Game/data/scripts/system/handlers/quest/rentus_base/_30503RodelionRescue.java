package quest.rentus_base;

import org.typezero.gameserver.model.gameobjects.Npc;
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
public class _30503RodelionRescue extends QuestHandler {

	private static final int questId = 30503;

	public _30503RodelionRescue() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804879).addOnQuestStart(questId);
		qe.registerQuestNpc(804879).addOnTalkEvent(questId);
		qe.registerQuestNpc(799541).addOnTalkEvent(questId);
		qe.registerQuestNpc(701097).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 804879) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.START) {
			if(targetId == 799541){
				switch (dialog) {
					case QUEST_SELECT:{
						return sendQuestDialog(env, 1011);
					}
					case SET_SUCCEED:
						changeQuestStep(env, 0, 0, true);
						return closeDialogWindow(env);
				}
			}
			else if(targetId == 799541) {
				Npc npc = (Npc) env.getVisibleObject();
				npc.getController().onDelete();
				return true;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 205438) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

}
