package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author Romanz
 */
public class _80305BitterOrSweet extends QuestHandler {

	private final static int questId = 80305;
    int rewardIndex;

	public _80305BitterOrSweet() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(831391).addOnQuestStart(questId);
		qe.registerQuestNpc(831391).addOnTalkEvent(questId);
		qe.registerQuestNpc(831399).addOnTalkEvent(questId);
		qe.registerQuestNpc(831401).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env)
	{
		final Player player = env.getPlayer();
		int targetId = 0;
		if(env.getVisibleObject() instanceof Npc)
		targetId = ((Npc) env.getVisibleObject()).getNpcId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();

		if(qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()){
			if(targetId == 831391){
				if(dialog == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
                else if (dialog == DialogAction.SETPRO1) {
					QuestService.startQuest(env);
                    changeQuestStepRew(env, 0, 1);
                    rewardIndex = 0;
					return closeDialogWindow(env);
				}
                else if (dialog == DialogAction.SETPRO2) {
                    QuestService.startQuest(env);
                    changeQuestStepRew(env, 0, 2);
                    rewardIndex = 1;
                    return closeDialogWindow(env);
                }
                else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if(targetId == 831399){
                if (dialog == DialogAction.USE_OBJECT){
				return sendQuestDialog(env, 5);
                }
			}
            else if(targetId == 831401){
                if (dialog == DialogAction.USE_OBJECT){
                return sendQuestDialog(env, 6);
		}
            }
            return sendQuestEndDialog(env, rewardIndex);
        }
		return false;
	}
}
