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
public class _Q80731 extends QuestHandler
{
	private final static int	questId	= 80731;


	public _Q80731()
	{
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(833543).addOnQuestStart(questId);
		qe.registerQuestNpc(833543).addOnTalkEvent(questId);
		qe.registerQuestNpc(230858).addOnKillEvent(questId);
		qe.registerQuestNpc(231073).addOnKillEvent(questId);
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
      if (qs == null || qs.getStatus() != QuestStatus.START)
          return false;

      int var1 = qs.getQuestVarById(1);
      int var2 = qs.getQuestVarById(2);

      int targetId = env.getTargetId();
          switch (targetId) {
              case 230858:
                  if (var1 >= 0 && var1 < 3) {
                      qs.setQuestVarById(1, var1 + 1);
                      updateQuestStatus(env);
                  }
                  break;
              case 231073:
                  if (var2 >= 0 && var2 < 2) {
                      qs.setQuestVarById(2, var2 + 1);
                      updateQuestStatus(env);
                  }
                  break;
          }

      if (var1 == 3 && var2 == 2) {
          qs.setStatus(QuestStatus.REWARD);
          updateQuestStatus(env);
          return true;
      }
      return false;
  }
}
