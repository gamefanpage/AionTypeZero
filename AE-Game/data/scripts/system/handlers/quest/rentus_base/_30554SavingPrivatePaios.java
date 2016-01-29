package quest.rentus_base;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author Romanz
 *
 */
public class _30554SavingPrivatePaios extends QuestHandler
{
	private final static int questId=30554;
	public _30554SavingPrivatePaios()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(804909).addOnQuestStart(questId); //Lition
		qe.registerQuestNpc(804909).addOnTalkEvent(questId);
		qe.registerQuestNpc(799536).addOnTalkEvent(questId); //Paios
		qe.registerQuestNpc(701098).addOnTalkEvent(questId);

	}
	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player=env.getPlayer();
		QuestState qs=player.getQuestStateList().getQuestState(questId);
		DialogAction dialog=env.getDialog();
		int targetId=env.getTargetId();
		if(qs==null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())
		{
			switch(targetId)
			{
				case 804909:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,4762);

						default:
							return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if(qs!=null && qs.getStatus() ==QuestStatus.START)
		{

			switch(targetId)
			{
				case 799536:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,1352);
						case SET_SUCCEED:
						{
							return defaultCloseDialog(env, 1, 2, true, false);//reward

						}
					}
				}
				case 701098:
				{
					switch(dialog)
					{
						case USE_OBJECT:
							return useQuestObject(env, 0, 1, false, true);
					}
				}
				case 804909:
				{
					switch(dialog)
					{
						case SELECT_QUEST_REWARD:
							return defaultCloseDialog(env, 2, 2, true, false);
					}
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			switch(targetId)
			{
				case 804909:
				{
					return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}
}
