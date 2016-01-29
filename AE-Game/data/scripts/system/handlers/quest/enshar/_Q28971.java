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
public class _Q28971 extends QuestHandler
{
	private final static int	questId	= 28971;
        private final static int[] mobs_ids = { 219686, 219687, 219728, 219729, 219746, 219747 };

	public _Q28971()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804924).addOnQuestStart(questId);
		qe.registerQuestNpc(804924).addOnTalkEvent(questId);
		qe.registerQuestNpc(805216).addOnTalkEvent(questId);
		qe.registerQuestNpc(805217).addOnTalkEvent(questId);
		qe.registerQuestNpc(805218).addOnTalkEvent(questId);
		for (int mobs_id : mobs_ids)
		qe.registerQuestNpc(mobs_id).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 804924)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
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

  @Override
	public boolean onKillEvent(QuestEnv env)
	{
      Player player = env.getPlayer();
      QuestState qs = player.getQuestStateList().getQuestState(questId);
      if (qs == null || qs.getStatus() != QuestStatus.START)
          return false;

      int var1 = qs.getQuestVarById(1);
      int var2 = qs.getQuestVarById(2);
      int var3 = qs.getQuestVarById(3);
		int targetId = env.getTargetId();
		switch (targetId)
		{
			case 219686:
			case 219687:
				if(qs.getQuestVarById(1) >= 0 && qs.getQuestVarById(1) < 4)
				{
					qs.setQuestVarById(1, var1 + 1);
					updateQuestStatus(env);
				}
				break;
			case 219728:
			case 219729:
				if(qs.getQuestVarById(2) >= 0 && qs.getQuestVarById(2) < 4)
				{
					qs.setQuestVarById(2, var2 + 1);
					updateQuestStatus(env);
				}
				break;
			case 219746:
			case 219747:
				if(qs.getQuestVarById(3) >= 0 && qs.getQuestVarById(3) < 4)
				{
					qs.setQuestVarById(3, var3 + 1);
					updateQuestStatus(env);
				}
				break;
		}

		if(qs.getQuestVarById(1) == 4 && qs.getQuestVarById(2) == 4 && qs.getQuestVarById(3) == 4)
		{
			qs.setStatus(QuestStatus.REWARD);
			qs.setQuestVar(1);
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}
