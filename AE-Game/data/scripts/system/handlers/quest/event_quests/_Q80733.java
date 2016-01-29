package quest.event_quests;

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
public class _Q80733 extends QuestHandler
{
	private final static int	questId	= 80733;


	public _Q80733()
	{
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(833543).addOnQuestStart(questId);
		qe.registerQuestNpc(833543).addOnTalkEvent(questId);
		qe.registerQuestNpc(231130).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		if(targetId == 833543)
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

		switch(targetId){
				case 231130:
					if (qs.getQuestVarById(1) != 5){
						qs.setQuestVarById(1, qs.getQuestVarById(1) + 1);
						updateQuestStatus(env);
					}
					else {
						qs.setQuestVarById(1, 6);
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
					}
			}
		return false;
	}
}
