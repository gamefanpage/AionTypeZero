package quest.sanctum;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.zone.ZoneName;

//By Evil_dnk

public class _Q11303 extends QuestHandler {

	private final static int questId = 11303;
        private final static int[] npc_ids = { 799038, 798316};

	public _Q11303() {
		super(questId);
	}

    @Override
	public void register() {
        qe.registerQuestNpc(799038).addOnQuestStart(questId);
        for (int npc_id : npc_ids)
			qe.registerQuestNpc(npc_id).addOnTalkEvent(questId);
    }


    @Override
	public boolean onDialogEvent(QuestEnv env) {
        Player player = env.getPlayer();
        QuestState qs = player.getQuestStateList().getQuestState(questId);

        if (qs != null && qs.getStatus() == QuestStatus.START) {
            int var = qs.getQuestVarById(0);
            if (env.getTargetId() == 799038) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    if (var == 0)
                    return sendQuestDialog(env, 1011);
                }
                else if (env.getDialog() == DialogAction.SETPRO1){
                    return defaultCloseDialog(env, 0, 1); // 1
                }
                else
                    return sendQuestStartDialog(env);
            }

            if (env.getTargetId() == 205964) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                    if(var == 1){
                    return sendQuestDialog(env, 1352);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO2){
                    return defaultCloseDialog(env, 1, 2);
                }
            }
            }

     if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
          if (env.getTargetId() == 205842){
            if (env.getDialog() == DialogAction.USE_OBJECT){
                return sendQuestDialog(env, 3057);
        }
              return sendQuestEndDialog(env);
          }
        }

    return false;
      }
}
