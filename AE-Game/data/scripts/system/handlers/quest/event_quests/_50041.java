package quest.event_quests;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

//By Romanz

public class _50041 extends QuestHandler {

	private final static int questId = 50041;

	public _50041() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(832815).addOnQuestStart(questId);
		qe.registerQuestNpc(832815).addOnTalkEvent(questId);
		qe.registerOnKillRanked(AbyssRankEnum.GENERAL, questId);
		qe.registerOnKillInWorld(400010000, questId);
		qe.registerOnKillInWorld(600050000, questId);
		qe.registerOnKillInWorld(600060000, questId);
		qe.registerOnKillInWorld(600070000, questId);
	}

    @Override
    public boolean onKillRankedEvent(QuestEnv env) {
        Player player = env.getPlayer();
        if (env.getVisibleObject() instanceof Player && player != null && (player.getWorldId() == 400010000 || player.getWorldId() == 600050000 || player.getWorldId() == 600060000 || player.getWorldId() == 600070000)) {
                return defaultOnKillRankedEvent(env, 0, 6, true);
        }
        return false;
    }

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (env.getTargetId() == 832815) {
			if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 4762);
				else
					return sendQuestStartDialog(env);
			}
			else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1352);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}
}
