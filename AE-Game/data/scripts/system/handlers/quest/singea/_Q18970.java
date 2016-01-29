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
public class _Q18970 extends QuestHandler {

	private final static int questId = 18970;

	public _Q18970() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804709).addOnQuestStart(questId);
		qe.registerQuestNpc(804709).addOnTalkEvent(questId);
		qe.registerQuestNpc(805213).addOnTalkEvent(questId);
		qe.registerQuestNpc(805214).addOnTalkEvent(questId);
		qe.registerQuestNpc(805215).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if(targetId == 804709)
		{
			if(qs == null || qs.getStatus() == QuestStatus.NONE)
			{
				if(env.getDialogId() == DialogAction.QUEST_SELECT.id())
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if(targetId == 805213 || targetId == 805214 || targetId == 805215)
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
}
