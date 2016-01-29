package quest.rentus_base;

import org.typezero.gameserver.model.gameobjects.Npc;
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
public class _30553ComradesInArms extends QuestHandler
{
	private final static int questId=30553;
	public _30553ComradesInArms()
	{
		super(questId);
	}
	@Override
	public void register()
	{
		qe.registerQuestNpc(804909).addOnQuestStart(questId); //Lition
		qe.registerQuestNpc(804909).addOnTalkEvent(questId);
		qe.registerQuestNpc(701097).addOnTalkEvent(questId);
		qe.registerQuestNpc(799541).addOnTalkEvent(questId); //rodelion

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
		else if(qs.getStatus() ==QuestStatus.START)
		{

			switch(targetId)
			{
				case 804909:
				{
					switch(dialog)
					{
						case SELECT_QUEST_REWARD:
							return defaultCloseDialog(env, 1, 1, true, true);
					}
				}
				case 701097:
				{
					Npc npc = (Npc) env.getVisibleObject();
					npc.getController().onDelete();
					return true;
				}
				case 799541:
				{
					switch(dialog)
					{
						case QUEST_SELECT:
							return sendQuestDialog(env,1011);
						case SET_SUCCEED:
						{
							return defaultCloseDialog(env, 0, 1, true, false);//reward
						}
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
