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
public class _Q25202 extends QuestHandler
{
	private final static int	questId	= 25202;

	public _Q25202()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804914).addOnQuestStart(questId);
		qe.registerQuestNpc(804914).addOnTalkEvent(questId);
		qe.registerQuestNpc(219815).addOnKillEvent(questId);
		qe.registerQuestNpc(219816).addOnKillEvent(questId);
		qe.registerQuestNpc(219817).addOnKillEvent(questId);
		qe.registerQuestNpc(219819).addOnKillEvent(questId);
		qe.registerQuestNpc(219822).addOnKillEvent(questId);
		qe.registerQuestNpc(219821).addOnKillEvent(questId);
		qe.registerQuestNpc(219826).addOnKillEvent(questId);
		qe.registerQuestNpc(219823).addOnKillEvent(questId);
		qe.registerQuestNpc(219824).addOnKillEvent(questId);
		qe.registerQuestNpc(219818).addOnKillEvent(questId);
		qe.registerQuestNpc(219820).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 804914)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
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

		switch(targetId){
				case 219815:
				case 219816:
				case 219821:
				case 219819:
				case 219817:
				case 219822:
				case 219820:
				case 219818:
				case 219823:
				case 219824:
				case 219826:
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
