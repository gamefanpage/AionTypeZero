package quest.danuar_reliquary;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 *
 */
public class _26920ForgottenReliquary extends QuestHandler {

	private final static int questId = 26920;

	public _26920ForgottenReliquary() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(801547).addOnQuestStart(questId);
		qe.registerQuestNpc(801547).addOnTalkEvent(questId);
		qe.registerQuestNpc(206325).addOnAtDistanceEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("LDF5_UNDER_SENSORYAREA_Q16920_206325_1_600070000"),questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);

		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 801547)
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 4762);
					case QUEST_ACCEPT_SIMPLE:
						return sendQuestStartDialog(env);
					case QUEST_REFUSE_SIMPLE:
						return sendQuestEndDialog(env);
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 10002);
				else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id())
					return sendQuestDialog(env, 5);
				else
					return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onAtDistanceEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			if (qs.getQuestVars().getQuestVars() == 0) {
				qs.setStatus(QuestStatus.REWARD);
				updateQuestStatus(env);
				return true;
			}
		}
		return false;
	}
    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        if (zoneName == ZoneName.get("LDF5_UNDER_SENSORYAREA_Q16920_206325_1_600070000")) {
            Player player = env.getPlayer();
            if (player == null)
                return false;
            QuestState qs = player.getQuestStateList().getQuestState(questId);
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                if (var == 0) {
                    changeQuestStep(env, 0, 1, true);
                    return true;
                }
            }
        }
        return false;
    }
}
