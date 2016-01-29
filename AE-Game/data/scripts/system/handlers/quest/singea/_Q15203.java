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
public class _Q15203 extends QuestHandler
{
	private final static int questId = 15203;
	private final static int[] mobs_ids = {235829, 235831, 235851};

	public _Q15203()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804704).addOnQuestStart(questId);
		qe.registerQuestNpc(804704).addOnTalkEvent(questId);
		for (int mobs_id : mobs_ids)
			qe.registerQuestNpc(mobs_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 804704)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
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
		int var3 = qs.getQuestVarById(3);

		int targetId = env.getTargetId();
		switch (targetId)
		{
			case 235829:
				if(var1 >= 0 && var1 < 5)
				{
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
					if(var1 == 4 && var2 == 3 && var3 == 2)
					{
						qs.setStatus(QuestStatus.REWARD);
						qs.setQuestVar(1);
						updateQuestStatus(env);
					}
				}
				break;
			case 235831:
				if(var2 >= 0 && var2 < 3)
				{
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
					if(var1 == 5 && var2 == 2 && var3 == 2)
					{
						qs.setStatus(QuestStatus.REWARD);
						qs.setQuestVar(1);
						updateQuestStatus(env);
					}
				}
				break;
			case 235851:
				if(var3 >= 0 && var3 < 2)
				{
					qs.setQuestVarById(3, var3 + 1);
					updateQuestStatus(env);
					if(var1 == 5 && var2 == 3 && var3 == 1)
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
