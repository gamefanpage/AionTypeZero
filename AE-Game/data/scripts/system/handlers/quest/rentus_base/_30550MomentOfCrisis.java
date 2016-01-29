package quest.rentus_base;

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
public class _30550MomentOfCrisis extends QuestHandler
{
	private final static int questId=30550;
	public _30550MomentOfCrisis()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(804723).addOnQuestStart(questId); //Skafir
		qe.registerQuestNpc(804723).addOnTalkEvent(questId);
		qe.registerQuestNpc(805156).addOnTalkEvent(questId); //Maios
		qe.registerQuestNpc(799592).addOnTalkEvent(questId); //Oreitia
	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		int targetId=env.getTargetId();
		DialogAction dialog=env.getDialog();
		if(qs==null || qs.getStatus() ==QuestStatus.NONE || qs.canRepeat())
		{
			if(targetId==804723)
			{
				switch(dialog)
				{
					case QUEST_SELECT:
						return sendQuestDialog(env,1011);
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs != null && qs.getStatus() == QuestStatus.START)
		{
			int var=qs.getQuestVarById(0);
			switch(targetId)
			{

				case 805156:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,1352);
						case SETPRO1:
						{
							return  defaultCloseDialog(env,0,1);
						}
					}
				}
				case 799592:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,2375);
						case SELECT_QUEST_REWARD:
						{
							if(var ==1)
							{
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestDialog(env,5);
							}
						}
					}
				}
			}

		}
		else if(qs != null && qs.getStatus() ==QuestStatus.REWARD)
		{
			switch(targetId)
			{
				case 799592:
				{
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
