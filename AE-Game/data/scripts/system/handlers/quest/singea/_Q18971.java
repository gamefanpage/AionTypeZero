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
public class _Q18971 extends QuestHandler
{
	private final static int questId = 18971;
	private final static int[] mobs_ids = {235824, 235825, 235867, 235868, 235881};

	public _Q18971()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804865).addOnQuestStart(questId);
		qe.registerQuestNpc(804865).addOnTalkEvent(questId);
		qe.registerQuestNpc(805213).addOnTalkEvent(questId);
		qe.registerQuestNpc(805214).addOnTalkEvent(questId);
		qe.registerQuestNpc(805215).addOnTalkEvent(questId);
		for (int mobs_id : mobs_ids)
			qe.registerQuestNpc(mobs_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 804865)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		} else if(targetId == 805213 || targetId == 805214 || targetId == 805215)
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
				} else
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
			case 235824:
			case 235825:
				if(var1 >= 0 && var1 < 4)
				{
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
					if(var1 == 3 && var2 == 4 && var3 == 4)
					{
						qs.setStatus(QuestStatus.REWARD);
						qs.setQuestVar(1);
						updateQuestStatus(env);
					}
				}

				break;
			case 235867:
			case 235868:
				if(var2 >= 0 && var2 < 4)
				{
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
					if(var1 == 4 && var2 == 3 && var3 == 4)
					{
						qs.setStatus(QuestStatus.REWARD);
						qs.setQuestVar(1);
						updateQuestStatus(env);
					}
				}
				break;
			case 235881:
				if(var3 >= 0 && var3 < 4)
				{
					updateQuestStatus(env);
					qs.setQuestVarById(3, var3 + 1);
					if(var1 == 4 && var2 == 4 && var3 == 3)
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
