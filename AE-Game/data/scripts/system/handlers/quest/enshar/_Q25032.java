package quest.enshar;

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
public class _Q25032 extends QuestHandler
{
	private final static int	questId	= 25032;

	public _Q25032()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804912).addOnQuestStart(questId);
		qe.registerQuestNpc(804912).addOnTalkEvent(questId);
		qe.registerQuestNpc(804913).addOnTalkEvent(questId);
		qe.registerQuestNpc(804914).addOnTalkEvent(questId);
		qe.registerQuestNpc(220057).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804912) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			int var = qs.getQuestVarById(0);
			switch (targetId) {
				case 804912: {
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 1) {
								return sendQuestDialog(env, 1352);
							}
						}
						case SETPRO2: {
							return defaultCloseDialog(env, 1, 2);
						}
					}
					break;
				}
				case 804913: {
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 2) {
								return sendQuestDialog(env, 1693);
							}
						}
						case SET_SUCCEED: {
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
						}
					}
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 804914) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		return defaultOnKillEvent(env, 220057, 0, 1);
	}
}

