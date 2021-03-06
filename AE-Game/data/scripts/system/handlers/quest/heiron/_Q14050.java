package quest.heiron;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

//By Evil_dnk

public class _Q14050 extends QuestHandler {

	private final static int questId = 14050;

	public _Q14050() {
		super(questId);
	}


    @Override
    public void register() {
        qe.registerQuestNpc(204500).addOnTalkEvent(questId);
        qe.registerOnEnterZone(ZoneName.get("NEW_HEIRON_GATE_210040000"), questId);
    }

    @Override
    public boolean onDialogEvent(QuestEnv env) {
        final Player player = env.getPlayer();
        final QuestState qs = player.getQuestStateList().getQuestState(questId);
        if (qs == null)
            return false;

        int targetId = 0;
        if (env.getVisibleObject() instanceof Npc)
            targetId = ((Npc) env.getVisibleObject()).getNpcId();
        if (targetId != 204500)
            return false;
        if (qs.getStatus() == QuestStatus.START) {
            if (env.getDialog() == DialogAction.QUEST_SELECT)
                return sendQuestDialog(env, 10002);
            else if (env.getDialogId() == DialogAction.SELECT_QUEST_REWARD.id()) {
                qs.setStatus(QuestStatus.REWARD);
                qs.setQuestVarById(0, 1);
                updateQuestStatus(env);
                return sendQuestDialog(env, 5);
            }
            return false;
        }
        else if (qs.getStatus() == QuestStatus.REWARD) {
            if (env.getDialogId() == DialogAction.SELECTED_QUEST_NOREWARD.id()) {
                int[] ids = { 14051, 14052, 14053, 14054};
                for (int id : ids) {
                    QuestEngine.getInstance().onEnterZoneMissionEnd(
                            new QuestEnv(env.getVisibleObject(), env.getPlayer(), id, env.getDialogId()));
                }
            }
            return sendQuestEndDialog(env);
        }
        return false;
    }

    @Override
    public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
        return defaultOnEnterZoneEvent(env, zoneName, ZoneName.get("NEW_HEIRON_GATE_210040000"));
    }
}
