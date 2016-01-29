package ai.worlds;

import ai.GeneralNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("world_blesser")
public class WorldBlesserAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
            case 831024:
            case 831025:
            case 831026:
            case 831027:
            case 831028:
            case 831029:
            case 831030:
            case 831031: {
				super.handleDialogStart(player);
				break;
			}
			default: {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			}
		}
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (dialogId == 10000) {
			SkillEngine.getInstance().getSkill(getOwner(), 20950, 1, player).useWithoutPropSkill();
		}
		else if (dialogId == 31 && questId != 0) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), dialogId, questId));
		}
        if (QuestEngine.getInstance().onDialog(env)) {
            return true;
        }
		return true;
	}

}
