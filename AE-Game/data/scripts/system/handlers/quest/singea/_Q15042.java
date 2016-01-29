package quest.singea;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _Q15042 extends QuestHandler
{
	private final static int questId = 15042;

	public _Q15042()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(804885).addOnQuestStart(questId);
		qe.registerQuestNpc(804885).addOnTalkEvent(questId);
		qe.registerQuestItem(182215676, questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if(qs == null || qs.getStatus() == QuestStatus.NONE)
		{
			if(targetId == 804885)
			{
				if(dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env, 182215676, 1);
			}
		}
		else if(qs.getStatus() == QuestStatus.REWARD)
		{
			if(targetId == 804885)
			{
				switch (env.getDialog())
				{
					case SELECT_QUEST_REWARD:
						return sendQuestDialog(env, 5);
					default:
						return sendQuestEndDialog(env);
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, Item item) {
		Player player = env.getPlayer();
		final int id = item.getItemTemplate().getTemplateId();

		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (id != 182215676)
			return HandlerResult.UNKNOWN;
		if (qs == null || qs.getQuestVarById(0) != 0)
			return HandlerResult.FAILED;

		if (qs != null || qs.getStatus() == QuestStatus.START) {
			if (player.isInsideZone(ZoneName.get("LF5_ITEMUSEAREA_Q15042")))
			{
				return HandlerResult.fromBoolean(useQuestItem(env, item, 0, 1, true, 182215753, 1, 0)); // 2
			}
		}
		return HandlerResult.FAILED;
	}
}
