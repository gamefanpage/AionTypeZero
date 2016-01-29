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
public class _Q30514 extends QuestHandler{

	private static final int questId = 30514;

	public _Q30514() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(799592).addOnQuestStart(questId);
		qe.registerQuestNpc(799592).addOnTalkEvent(questId);
		qe.registerQuestNpc(799670).addOnTalkEvent(questId);
		qe.registerQuestNpc(217310).addOnKillEvent(questId);
		qe.registerQuestNpc(217311).addOnKillEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 799592) {
				switch (dialog) {
					case QUEST_SELECT:{
						return sendQuestDialog(env, 4762);
					}
					default:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if(targetId == 799592 || targetId == 799670)
			{
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
			case 217310:
				if (qs.getQuestVarById(1) != 1){
					qs.setQuestVarById(1, 1);
					updateQuestStatus(env);
				}
				if(qs.getQuestVarById(1) >= 1 && qs.getQuestVarById(2) >= 1)
				{
					qs.setStatus(QuestStatus.REWARD);
					qs.setQuestVar(1);
					updateQuestStatus(env);
				}
				break;
			case 217311:
				if (qs.getQuestVarById(2) != 1){
					qs.setQuestVarById(2, 1);
					updateQuestStatus(env);
				}
				if(qs.getQuestVarById(1) >= 1 && qs.getQuestVarById(2) >= 1)
				{
					qs.setStatus(QuestStatus.REWARD);
					qs.setQuestVar(1);
					updateQuestStatus(env);
				}
				break;
		}
		return false;
	}
}
