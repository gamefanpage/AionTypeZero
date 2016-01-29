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
public class _Q28970 extends QuestHandler {

	private final static int questId = 28970;

	public _Q28970() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804927).addOnQuestStart(questId);
		qe.registerQuestNpc(804927).addOnTalkEvent(questId);
		qe.registerQuestNpc(805216).addOnTalkEvent(questId);
		qe.registerQuestNpc(805217).addOnTalkEvent(questId);
		qe.registerQuestNpc(805218).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 804927)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if(targetId == 805216 || targetId == 805217 || targetId == 805218)
		{
			if(qs != null)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id() && qs.getStatus() == QuestStatus.START)
					return sendQuestDialog(env, 10002);
				else if(env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
				{
					qs.setQuestVar(1);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestEndDialog(env);
				}
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
