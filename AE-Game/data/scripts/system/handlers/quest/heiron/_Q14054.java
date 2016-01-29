package quest.heiron;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


//By Evil_dnk

public class _Q14054 extends QuestHandler {

	private final static int questId = 14054;
	private final static int[] npc_ids = { 204500, 204600, 204610, 800413, 802050, 204602 };
	private final static int[] mobs = { 212588, 213994, 213995, 213996, 213997, 213998, 213999, 214000, 214001, 214002, 214003, 214010, 214016, 214081, 233861, 702040 };

	public _Q14054() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
        for (int mob : mobs)
            qe.registerQuestNpc(mob).addOnKillEvent(questId);
        for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		return defaultOnLvlUpEvent(env, 14050, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (env.getTargetId() == 204602) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                        return sendQuestDialog(env, 1011);
                }
                else if (env.getDialog() == DialogAction.SETPRO1){
                    return defaultCloseDialog(env, 0, 1);
                }
                else
                    return sendQuestStartDialog(env);
		}
            if (env.getTargetId() == 800413) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    return sendQuestDialog(env, 1352);
                }
                else if (env.getDialog() == DialogAction.SETPRO2){
                    return defaultCloseDialog(env, 1, 2);
            }
        }
            if (env.getTargetId() == 802050) {
                int var1 = qs.getQuestVarById(1);
                int var2 = qs.getQuestVarById(2);
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    if (var1 == 6 && var2 == 3)
                    return sendQuestDialog(env, 2034);

                }
                if (env.getDialog() == DialogAction.USE_OBJECT){
                    if(var == 4)
                        return sendQuestDialog(env, 2716);
                }
                else if (env.getDialog() == DialogAction.SETPRO4){
                    qs.setQuestVar(3);
                    updateQuestStatus(env);
                    return closeDialogWindow(env);
                }
                else if (env.getDialog() == DialogAction.SETPRO6){
                  return defaultCloseDialog(env, 4, 5, true, false);
                }
            }

        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (env.getTargetId() == 204602) {
                if (env.getDialog() == DialogAction.USE_OBJECT) {
                    return sendQuestDialog(env, 3057);
                }
                else {
                    return sendQuestEndDialog(env);
                }
            }
        }

		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
            Player player = env.getPlayer();
            QuestState qs = player.getQuestStateList().getQuestState(questId);
            if (qs != null && qs.getStatus() == QuestStatus.START) {
                int var = qs.getQuestVarById(0);
                int var1 = qs.getQuestVarById(1);
                int var2 = qs.getQuestVarById(2);
                int targetId = env.getTargetId();
                int[] npcIds = { 213994, 213995, 213996, 213997, 213998, 213999, 214000, 214001, 214002, 214003,214010, 214016, 214081, 214087};
                if (var == 2)
                {
                    if (var2 < 3 && targetId == 702040)
                    {
                        qs.setQuestVarById(2, var2 + 1);
                        updateQuestStatus(env);
                        return true;
                    }
                    for (int id : npcIds) {
                    if (var1 < 6 && targetId == id)
                    {
                        qs.setQuestVarById(1, (var1 + 1));
                        updateQuestStatus(env);
                         return true;
                                }
                            }
                }
                if (var == 3)
                {
                    return defaultOnKillEvent(env, 233861, 3, false);
                }
            }
            return false;
        }
}
