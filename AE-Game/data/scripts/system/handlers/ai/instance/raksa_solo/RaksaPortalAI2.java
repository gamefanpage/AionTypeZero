package ai.instance.raksa_solo;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

@AIName("raksa_portal")
public class RaksaPortalAI2 extends GeneralNpcAI2 {

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
			return true;
		}
                int instanceId = getPosition().getInstanceId();
		if (dialogId == DialogAction.SETPRO1.id()) {
        switch (getNpcId()) {
            case 206380:
            case 206397:
			TeleportService2.teleportTo(player, 300610000, instanceId, 811.3f, 829.5f, 733.7f, (byte) 0, TeleportAnimation.BEAM_ANIMATION);
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			break;
		}
		return true;
	}
        return false;

    }
}
