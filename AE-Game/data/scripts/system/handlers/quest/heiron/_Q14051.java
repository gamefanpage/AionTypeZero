package quest.heiron;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;

//By Evil_dnk
public class _Q14051 extends QuestHandler {

	private final static int questId = 14051;
	private final static int[] npc_ids = { 204549, 730026, 730024, 204500 };

	public _Q14051() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
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
            if (env.getTargetId() == 204500) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    return sendQuestDialog(env, 1011);
                }
                else if (env.getDialog() == DialogAction.SETPRO1){
                    return defaultCloseDialog(env, 0, 1); // 1
                }
                else
                    return sendQuestStartDialog(env);
            }

            if (env.getTargetId() == 204549) {
                int var = qs.getQuestVarById(0);

                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    if (var == 1)
                    return sendQuestDialog(env, 1352);
                    if (var == 2)
                        return sendQuestDialog(env, 1693);
                }
                else if (env.getDialog() == DialogAction.SETPRO2){
                    return defaultCloseDialog(env, 1, 2); // 1
                }
                else if (env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM){
                    if (QuestService.collectItemCheck(env, true)) {
                        qs.setQuestVar(3);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 10000);
                    }
                    else
                        return sendQuestDialog(env, 10001);
                }
            }
            if (env.getTargetId() == 730026) {
                    if (env.getDialog() == DialogAction.QUEST_SELECT){
                        return sendQuestDialog(env, 2035);
                    }
                    else if (env.getDialog() == DialogAction.SETPRO4){
                        return defaultCloseDialog(env, 3, 4, true, false);
                    }
            }
        }
        if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (env.getTargetId() == 730024){
                if (env.getDialog() == DialogAction.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                }
            else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD){
                return sendQuestEndDialog(env);
        }
                else if (env.getDialog() == DialogAction.SELECTED_QUEST_NOREWARD){
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
}
}
