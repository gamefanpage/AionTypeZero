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
public class _Q15050 extends QuestHandler
{
	private final static int	questId	= 15050;

	public _Q15050()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804706).addOnQuestStart(questId);
		qe.registerQuestNpc(804706).addOnTalkEvent(questId);
		qe.registerQuestNpc(235863).addOnKillEvent(questId);
		qe.registerQuestNpc(235864).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 804706)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
			{
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
		int var1 = qs.getQuestVarById(1);
		switch(targetId)
		{
				case 235863:
				case 235864:
					if(var1 >= 0 && var1 < 7)
					{
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
						if(var1 == 6)
						{
							qs.setStatus(QuestStatus.REWARD);
								qs.setQuestVar(1);
							updateQuestStatus(env);
						}
					}
					break;
			}
		return false;
	}
}
