package quest.altgard;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _24015TotemPlowed extends QuestHandler {

	private final static int questId = 24015;

	public _24015TotemPlowed() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(203669).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("BLACK_CLAW_OUTPOST_220030000"), questId);
		qe.registerQuestNpc(700099).addOnKillEvent(questId);
		qe.registerQuestNpc(203557).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		final int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			switch (targetId) {
				case 203669:
					switch (env.getDialog()) {
						case QUEST_SELECT:
							if (var == 0)
								return sendQuestDialog(env, 1011);
							break;
						case SETPRO1:
							if (var == 0) {
								SkillEngine.getInstance().applyEffectDirectly(272, player, player, 0);
								return defaultCloseDialog(env, 0, 1);
							}
							break;
					}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203557) {
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

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId == 700099 && var >= 2 && var < 4) {
			qs.setQuestVarById(0, var + 1);
			updateQuestStatus(env);
			return true;
		}
        if (targetId == 700099 && var == 4) {
            changeQuestStep(env, 4, 4, true);
            updateQuestStatus(env);
            return true;
        }
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName != ZoneName.get("BLACK_CLAW_OUTPOST_220030000"))
			return false;
		final Player player = env.getPlayer();
		if (player == null)
			return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;
		if (qs.getQuestVarById(0) == 1) {
			qs.setQuestVarById(0, 2);
			updateQuestStatus(env);
            player.getEffectController().removeEffect(272);
			return true;
		}
		return false;
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 24010, true);
	}

}
