package quest.singea;

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
public class _Q15101 extends QuestHandler
{
	private final static int	questId	= 15101;

	public _Q15101()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804711).addOnQuestStart(questId);
		qe.registerQuestNpc(804711).addOnTalkEvent(questId);
		qe.registerQuestNpc(804715).addOnTalkEvent(questId);
		qe.registerQuestNpc(235939).addOnKillEvent(questId);
		qe.registerQuestNpc(235940).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 804711) {
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
				case 804715: {
					switch (dialog) {
						case QUEST_SELECT: {
							if (var == 0) {
								return sendQuestDialog(env, 1011);
							}
						}
						case SETPRO1: {
							return defaultCloseDialog(env, 0, 1);
						}
					}
					break;
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 804715) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int targetId = env.getTargetId();

		switch(targetId){
				case 235939:
				case 235940:
					if (qs.getQuestVarById(1) != 9){
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
					}
					else {
						qs.setQuestVarById(1, 10);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
			}
		return false;
	}
}
