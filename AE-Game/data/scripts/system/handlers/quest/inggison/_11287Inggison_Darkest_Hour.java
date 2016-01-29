package quest.inggison;

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.skillengine.SkillEngine;

//By Romanz

public class _11287Inggison_Darkest_Hour extends QuestHandler {

	private final static int questId = 11287;

	public _11287Inggison_Darkest_Hour() {
		super(questId);
	}

    @Override
	public void register() {
		qe.registerQuestNpc(799038).addOnQuestStart(questId);
		qe.registerQuestNpc(701345).addOnTalkEvent(questId);
		qe.registerQuestNpc(701346).addOnTalkEvent(questId);
		qe.registerQuestNpc(701347).addOnTalkEvent(questId);
		qe.registerQuestNpc(701348).addOnTalkEvent(questId);
		qe.registerQuestNpc(799094).addOnTalkEvent(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		DialogAction dialog = env.getDialog();
		int targetId = env.getTargetId();

		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == 799038) {
				if (dialog == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 4762);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if(qs.getStatus() == QuestStatus.START) {
                    Npc npc = player.getPosition().getWorldMapInstance().getNpc(258200);
                    Npc aether = player.getPosition().getWorldMapInstance().getNpc(701345);
                    Npc aether2 = player.getPosition().getWorldMapInstance().getNpc(701346);
                    Npc aether3 = player.getPosition().getWorldMapInstance().getNpc(701347);
                    Npc aether4 = player.getPosition().getWorldMapInstance().getNpc(701348);
			if (targetId == 701345) {
				if (dialog == DialogAction.USE_OBJECT) {
						if(qs.getQuestVarById(0) == 0)
						SkillEngine.getInstance().applyEffectDirectly(20107, npc, npc, 15000);
						SkillEngine.getInstance().getSkill(aether, 20111, 1, player).useWithoutPropSkill();
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
			}
			if (targetId == 701346) {
				if (dialog == DialogAction.USE_OBJECT) {
						if(qs.getQuestVarById(0) == 0)
						SkillEngine.getInstance().applyEffectDirectly(20108, npc, npc, 15000);
						SkillEngine.getInstance().getSkill(aether2, 20112, 1, player).useWithoutPropSkill();
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
			}
			if (targetId == 701347) {
				if (dialog == DialogAction.USE_OBJECT) {
						if(qs.getQuestVarById(0) == 0)
						SkillEngine.getInstance().applyEffectDirectly(20109, npc, npc, 15000);
						SkillEngine.getInstance().getSkill(aether3, 20113, 1, player).useWithoutPropSkill();
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
			}
			if (targetId == 701348) {
				if (dialog == DialogAction.USE_OBJECT) {
						if(qs.getQuestVarById(0) == 0)
						SkillEngine.getInstance().applyEffectDirectly(20110, npc, npc, 15000);
						SkillEngine.getInstance().getSkill(aether4, 20114, 1, player).useWithoutPropSkill();
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
				}
			}
        }
        else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
            if(targetId == 799094){
                return sendQuestEndDialog(env);
            }
        }
		return false;
	}

}

