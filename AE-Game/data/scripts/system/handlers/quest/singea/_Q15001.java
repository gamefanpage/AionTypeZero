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
public class _Q15001 extends QuestHandler
{
	private final static int questId = 15001;
	private final static int[] mobs_ids = {235790, 235791, 235799, 235800};

	public _Q15001()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804698).addOnQuestStart(questId);
		qe.registerQuestNpc(804698).addOnTalkEvent(questId);
		for (int mobs_id : mobs_ids)
			qe.registerQuestNpc(mobs_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 804698)
			{
				if(dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		} else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 804698)
			{
				switch (env.getDialog())
				{
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default:
						return sendQuestEndDialog(env);
				}
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
			case 235790:
			case 235791:
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
			case 235799:
			case 235800:
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
