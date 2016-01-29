package quest.enshar;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

//By Evil_dnk

public class _Q25073 extends QuestHandler
{
	private final static int questId = 25073;

	public _Q25073()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804918).addOnQuestStart(questId);
		qe.registerQuestNpc(804918).addOnTalkEvent(questId);
		qe.registerQuestNpc(731556).addOnTalkEvent(questId);
		qe.registerQuestNpc(804732).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 804918)
			{
				if(dialog == DialogAction.QUEST_SELECT)
				{
					return sendQuestDialog(env, 4762);
				}
				else
				{
					return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs != null && qs.getStatus() == QuestStatus.START)
		{
			if(targetId == 731556)
			{
				if(dialog == DialogAction.QUEST_SELECT)
				{
					if(player.getInventory().getItemCountByItemId(182215725) > 0)
						return sendQuestDialog(env, 1011);
					else
					{
						PacketSendUtility.sendMessage(player, "You dont have Melchor's Venom");
						return sendQuestDialog(env, 0);
					}
				}
				else if(dialog == DialogAction.SET_SUCCEED)
				{
					removeQuestItem(env, 182215725, 1);
					changeQuestStep(env, 0, 1, true);
					return closeDialogWindow(env);
				}
			}
		}
		else if(qs != null && qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 804732)
			{
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
