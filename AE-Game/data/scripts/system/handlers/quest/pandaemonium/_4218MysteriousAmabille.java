package quest.pandaemonium;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
/**
 * @author Romanz
 *
 */

public class _4218MysteriousAmabille extends QuestHandler
{
	private final static int	questId	= 4218;

	public _4218MysteriousAmabille()
	{
		super(questId);
	}

	@Override
	public void register()
	{
		qe.registerQuestNpc(204284).addOnQuestStart(questId);
		qe.registerQuestNpc(204284).addOnTalkEvent(questId);
		qe.registerQuestNpc(798334).addOnTalkEvent(questId);
		qe.registerQuestNpc(730208).addOnTalkEvent(questId);
	}

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);
        DialogAction dialog = env.getDialog();
        int targetId = env.getTargetId();

        if (qs == null || qs.getStatus() == QuestStatus.NONE) {
            if (targetId == 204284) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                } else {
                    return sendQuestStartDialog(env);
                }
            }
        } else if (qs.getStatus() == QuestStatus.START) {
            if (targetId == 730208) {
                Npc npc = (Npc) env.getVisibleObject();
                npc.getController().delete();
                return true;
            }
            else if (targetId == 798334) {
                if (dialog == DialogAction.QUEST_SELECT) {
                        return sendQuestDialog(env, 1352);
                }else if (dialog == DialogAction.SETPRO1) {
                    changeQuestStep(env, 0, 1, true);
                    return sendQuestDialog(env, 5);
                }
            }
        } else if (qs.getStatus() == QuestStatus.REWARD) {
            if (targetId == 798334) {
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }
}
