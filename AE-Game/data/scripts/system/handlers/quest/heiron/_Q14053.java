package quest.heiron;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapType;

//By Evil_dnk
public class _Q14053 extends QuestHandler {

	private final static int questId = 14053;

	public _Q14053() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
		qe.registerQuestNpc(204020).addOnTalkEvent(questId);
		qe.registerQuestNpc(204602).addOnTalkEvent(questId);
		qe.registerQuestNpc(204501).addOnTalkEvent(questId);
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
                    if(var == 0){
                        return sendQuestDialog(env, 1011);
                    }
                    if(var == 2){
                        return sendQuestDialog(env, 1693);
                    }
                    if(var == 3){
                        return sendQuestDialog(env, 2034);
                    }
                }
                else if (env.getDialog() == DialogAction.SETPRO1){
                    qs.setQuestVar(1);
                    updateQuestStatus(env);
                    PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(env.getVisibleObject().getObjectId(), 10));
                    TeleportService2.teleportTo(player, WorldMapType.ELTNEN.getId(), 1603.1948f, 1529.9152f, 317, (byte) 120, TeleportAnimation.BEAM_ANIMATION);
                    return true;
                }
                else if (env.getDialog() == DialogAction.SETPRO3){
                    return defaultCloseDialog(env, 2, 3);
                }
                else if (env.getDialog() == DialogAction.CHECK_USER_HAS_QUEST_ITEM){
                    if (QuestService.collectItemCheck(env, true)) {
                        qs.setQuestVar(4);
                        qs.setStatus(QuestStatus.REWARD);
                        updateQuestStatus(env);
                        return sendQuestDialog(env, 10000);
                    }
                    else
                        return sendQuestDialog(env, 10001);
           }
                else
                    return sendQuestStartDialog(env);
            }

            if (env.getTargetId() == 204020) {
                if (env.getDialog() == DialogAction.QUEST_SELECT){
                        return sendQuestDialog(env, 1352);
                }
                else if (env.getDialog() == DialogAction.SETPRO2){
                        qs.setQuestVar(2);
                        updateQuestStatus(env);
                        return closeDialogWindow(env);
                }
		}
            }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if (env.getTargetId() == 204602) {
                if (env.getDialog() == DialogAction.USE_OBJECT) {
                    return sendQuestDialog(env, 2375);
                }
                else {
                    return sendQuestEndDialog(env);
                }
            }
        }
        return false;
	}
}
