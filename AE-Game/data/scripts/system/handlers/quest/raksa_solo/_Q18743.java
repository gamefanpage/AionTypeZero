package quest.raksa_solo;

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
public class _Q18743 extends QuestHandler
{
	private final static int	questId	= 18743;

	public _Q18743()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(206380).addOnQuestStart(questId);
		qe.registerQuestNpc(206380).addOnTalkEvent(questId);
		qe.registerQuestNpc(804707).addOnTalkEvent(questId);
		qe.registerQuestNpc(236306).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 206380)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
			else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
			{
			if (targetId == 804707) {
				if(env.getDialogId() == DialogAction.USE_OBJECT.id())
					return sendQuestDialog(env, 10002);
				else if(env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
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
			case 236306:
					qs.setQuestVarById(1, 2);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
			}
		return false;
	}

}
