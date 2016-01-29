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
public class _Q25201 extends QuestHandler
{
	private final static int	questId	= 25201;

	public _Q25201()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804914).addOnQuestStart(questId);
		qe.registerQuestNpc(804914).addOnTalkEvent(questId);
		qe.registerQuestNpc(219802).addOnKillEvent(questId);
		qe.registerQuestNpc(219803).addOnKillEvent(questId);
		qe.registerQuestNpc(219814).addOnKillEvent(questId);
		qe.registerQuestNpc(219804).addOnKillEvent(questId);
		qe.registerQuestNpc(219811).addOnKillEvent(questId);
		qe.registerQuestNpc(219805).addOnKillEvent(questId);
		qe.registerQuestNpc(219806).addOnKillEvent(questId);
		qe.registerQuestNpc(219807).addOnKillEvent(questId);
		qe.registerQuestNpc(219808).addOnKillEvent(questId);
		qe.registerQuestNpc(219809).addOnKillEvent(questId);
		qe.registerQuestNpc(219812).addOnKillEvent(questId);
		qe.registerQuestNpc(219813).addOnKillEvent(questId);
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
				case 219802:
				case 219803:
				case 219805:
				case 219806:
				case 219807:
				case 219808:
				case 219809:
				case 219812:
				case 219813:
				case 219814:
				case 219804:
				case 219811:
				case 219810:
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
