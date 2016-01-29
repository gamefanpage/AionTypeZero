package quest.verteron;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Romanz
 */
public class _14014TurningtheIde extends QuestHandler {

	private final static int questId = 14014;
	private final static int[] npcs = { 203146, 802045, 203147, 203164 };
	private final static int[] mobs = { 210178, 216892 };
	private final static int[] items = { 182215314, 182200023 };

	public _14014TurningtheIde() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		for (int npc : npcs)
			qe.registerQuestNpc(npc).addOnTalkEvent(questId);
		for (int item : items)
			qe.registerQuestItem(item, questId);
		for (int mob : mobs)
			qe.registerQuestNpc(mob).addOnKillEvent(questId);
		qe.registerQuestNpc(210158).addOnAttackEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14010, true);
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
				case 203146: // Estino
					if (var == 0 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1011);
					if (env.getDialog() == DialogAction.SETPRO1)
						return defaultCloseDialog(env, 0, 1, 182215314, 1, 0, 0); // 1
					break;
				case 203147: // Meteina
					if (var == 2 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 1693);
					if (env.getDialog() == DialogAction.SETPRO3)
						return defaultCloseDialog(env, 2, 3); // 3
					break;
				case 802045: // Livanon
					if (var == 3 && env.getDialog() == DialogAction.QUEST_SELECT)
						return sendQuestDialog(env, 2034);
                    if (env.getDialog() == DialogAction.SELECT_ACTION_2376)
                        return defaultCloseDialog(env, 4, 5); // 3
     				if (env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM)
						return checkQuestItems(env, 3, 4, false, 2375, 2120); // 4
					break;
				case 700037: // Tursin Tribal Flag
					if (env.getDialog() == DialogAction.USE_OBJECT && var >= 6 && var < 9) {
						return useQuestObject(env, var, var + 1, false, 0, 0, 0, 0, 0, 0, true); // disappear
					}
					break;
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203164) { // Spatalos
                return sendQuestEndDialog(env);
            }
		}
		return false;
	}

	@Override
	public boolean onAttackEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return false;
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (targetId == 210158) // Tursin Loudmouth Boss
		{
			if (MathUtil.getDistance(env.getVisibleObject(), 1552.7401f, 1160.3622f, 114.06791f) <= 30) {
				if (qs.getQuestVarById(0) == 11) {
					playQuestMovie(env, 22);
					((Npc) env.getVisibleObject()).getController().onDie(player);
					qs.setQuestVar(10);
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return true;
				}
				else if (qs.getQuestVarById(0) == 4) {
					playQuestMovie(env, 13);
					((Npc) env.getVisibleObject()).getController().onDie(player);
					qs.setQuestVarById(0, 5); // 5
					updateQuestStatus(env);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public HandlerResult onItemUseEvent(QuestEnv env, final Item item) {
		final Player player = env.getPlayer();
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() != QuestStatus.START)
			return HandlerResult.UNKNOWN;
		int var = qs.getQuestVarById(0);

		final int id = item.getItemTemplate().getTemplateId();

		if (id == 182215314) // Transformation potion
			if (var == 1 && player.isInsideZone(ZoneName.get("TURSIN_OUTPOST_210030000")))
				return HandlerResult.fromBoolean(useQuestItem(env, item, 1, 2, false, 18));

		if (id == 182200023) // Flint
			if (var == 9 && player.isInsideZone(ZoneName.get("TURSIN_TOTEM_POLE_210030000")))
				return HandlerResult.fromBoolean(useQuestItem(env, item, 9, 10, false));
		return HandlerResult.FAILED;
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
		  if (targetId == 210178 || targetId == 21692)
          {

              if (var > 4 && var < 7)
                  changeQuestStep(env, var, var+1, false);
              if (var == 7)
                  changeQuestStep(env, 7, 8, true);
          }
		return false;
	}
}
