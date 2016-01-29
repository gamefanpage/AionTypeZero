package quest.raksa_solo;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _Q28740 extends QuestHandler {

	private final static int questId = 28740;

	public _Q28740() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(804732).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("UNDERGROUND_PRISON_FIRE_300610000"), questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		DialogAction dialog = env.getDialog();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();
        if(qs.getStatus() == QuestStatus.START) {
            if (targetId == 804732) {
                if (dialog == DialogAction.QUEST_SELECT) {
                    return sendQuestDialog(env, 1011);
                }
                else if (dialog == DialogAction.CHECK_USER_HAS_QUEST_ITEM) {
                    checkQuestItems(env, 0, 1, true, 5, 0);
                    return true;
                }
            }
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if(targetId == 804732){
                return sendQuestEndDialog(env);
            }
        }
        return false;
    }

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		return defaultOnEnterZoneEvent(env, zoneName, ZoneName.get("UNDERGROUND_PRISON_FIRE_300610000"));
	}
}
