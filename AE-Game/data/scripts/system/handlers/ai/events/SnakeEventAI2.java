package ai.events;

import ai.GeneralNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.skillengine.SkillEngine;

@AIName("snake_event")
public class SnakeEventAI2 extends GeneralNpcAI2 {

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
			return true;
		}
		if (dialogId == DialogAction.SETPRO1.id()) {
        switch (getNpcId()) {
            case 832963:
            case 832974:
					SkillEngine.getInstance().getSkill(getOwner(), 10976 , 1, player).useWithoutPropSkill();
					SkillEngine.getInstance().getSkill(getOwner(), 10977 , 1, player).useWithoutPropSkill();
					SkillEngine.getInstance().getSkill(getOwner(), 10978 , 1, player).useWithoutPropSkill();
			break;
            case 832964:
            case 832975:
					SkillEngine.getInstance().getSkill(getOwner(), 10979, 1, player).useWithoutPropSkill();
			break;
		}
		return true;
	}
        return false;

    }
}
