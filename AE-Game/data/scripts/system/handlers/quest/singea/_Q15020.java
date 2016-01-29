package quest.singea;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

/**
 * @author Romanz
 */
public class _Q15020 extends QuestHandler
{
	private final static int questId = 15020;
	private final static int[] mobs_ids = {235826, 235827};

	public _Q15020()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804876).addOnQuestStart(questId);
		qe.registerQuestNpc(804876).addOnTalkEvent(questId);
		for (int mobs_id : mobs_ids)
			qe.registerQuestNpc(mobs_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 804876)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			} else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
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
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null || qs.getStatus() != QuestStatus.START)
			return false;

		int var1 = qs.getQuestVarById(1);
		int var2 = qs.getQuestVarById(2);

		int targetId = env.getTargetId();
		switch (targetId)
		{
			case 235826:
				if(var1 >= 0 && var1 < 5)
				{
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
					if(var1 == 4 && var2 == 5)
					{
						qs.setStatus(QuestStatus.REWARD);
						qs.setQuestVar(1);
						updateQuestStatus(env);
					}
				}
				break;
			case 235827:
				if(var2 >= 0 && var2 < 5)
				{
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
					if(var1 == 5 && var2 == 4)
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
