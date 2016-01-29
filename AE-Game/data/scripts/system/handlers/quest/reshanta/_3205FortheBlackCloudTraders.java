package quest.reshanta;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

//By Evil_dnk

public class _3205FortheBlackCloudTraders extends QuestHandler
{

	private final static int questId = 3205;

	public _3205FortheBlackCloudTraders()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(279010).addOnQuestStart(questId);
		qe.registerQuestNpc(279010).addOnTalkEvent(questId);
		qe.registerQuestNpc(203735).addOnTalkEvent(questId);
		qe.registerQuestNpc(798321).addOnTalkEvent(questId);
		qe.registerQuestNpc(215049).addOnKillEvent(questId);
		qe.registerQuestNpc(219024).addOnKillEvent(questId);
	}


	@Override
	public boolean onDialogEvent(QuestEnv env)
	{

		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();

		if(targetId == 279010)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		if(qs == null)
			return false;
		int var = qs.getQuestVarById(0);
		if(qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 279010)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
				{
					if(var == 15)
						return sendQuestDialog(env, 1352);
				} else if(env.getDialogId() == DialogAction.SETPRO2.id())
				{
					return defaultCloseDialog(env, 15, 16);
				}
			}
			if(targetId == 203735)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
				{
					if(var == 16)
						return sendQuestDialog(env, 1693);
				}
				else if(env.getDialogId() == DialogAction.SET_SUCCEED.id())
				{
					return defaultCloseDialog(env, 16, 17, true, false);
				}
			}
		}

		if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 798321)
				return sendQuestEndDialog(env);
		}
		return false;
	}


	@Override
	public boolean onKillEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = env.getTargetId();


		if(qs.getStatus() != QuestStatus.START)
			return false;
		switch (targetId)
		{
			case 215049:
			case 219024:
				if(var <= 14)
				{
					qs.setQuestVar(var + 1);
					updateQuestStatus(env);
					return true;
				}
		}
		return false;
	}
}
