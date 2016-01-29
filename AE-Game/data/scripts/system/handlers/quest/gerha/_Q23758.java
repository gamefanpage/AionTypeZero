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
public class _Q23758 extends QuestHandler
{
	private final static int	questId	= 23758;

	public _Q23758()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(805291).addOnQuestStart(questId);
		qe.registerQuestNpc(805292).addOnQuestStart(questId);
		qe.registerQuestNpc(805293).addOnQuestStart(questId);
		qe.registerQuestNpc(805291).addOnTalkEvent(questId);
		qe.registerQuestNpc(805292).addOnTalkEvent(questId);
		qe.registerQuestNpc(805293).addOnTalkEvent(questId);
		qe.registerQuestNpc(235366).addOnKillEvent(questId);
		qe.registerQuestNpc(235367).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 805291 || targetId == 805292 || targetId == 805293)
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
				case 235366:
				case 235367:
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
