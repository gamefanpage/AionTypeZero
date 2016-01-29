package quest.gerha;

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
public class _Q13761 extends QuestHandler
{
	private final static int	questId	= 13761;

	public _Q13761()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(805275).addOnQuestStart(questId);
		qe.registerQuestNpc(805276).addOnQuestStart(questId);
		qe.registerQuestNpc(805277).addOnQuestStart(questId);
		qe.registerQuestNpc(805275).addOnTalkEvent(questId);
		qe.registerQuestNpc(805276).addOnTalkEvent(questId);
		qe.registerQuestNpc(805277).addOnTalkEvent(questId);
		qe.registerQuestNpc(235370).addOnKillEvent(questId);
		qe.registerQuestNpc(235371).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 805275 || targetId == 805276 || targetId == 805277)
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
				case 235370:
				case 235371:
					if (qs.getQuestVarById(1) != 4){
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
					}
					else {
						qs.setQuestVarById(1, 5);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
			}
		return false;
	}
}
