package quest.eltnen;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Romanz
 *
 */
public class _80312FaviollaRequest extends QuestHandler {

	private final static int questId = 80312;

	public _80312FaviollaRequest() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(831421).addOnQuestStart(questId);
		qe.registerQuestNpc(831421).addOnTalkEvent(questId);
		qe.registerQuestNpc(831422).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(final QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 831421) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 831422 && var == 0) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1352);
					case SELECT_ACTION_1353:{
						qs.setQuestVarById(0, 1);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return sendQuestDialog(env, 5);
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 831422) {
						return sendQuestEndDialog(env);
			}
			return false;
		}
		return false;
	}
}
