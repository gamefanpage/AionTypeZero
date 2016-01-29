package ai.worlds;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */
@AIName("vip_buff")
public class VipBuffAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
            int membership = player.getClientConnection().getAccount().getMembership();

        if(membership >= 2)
		{
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
			}
		}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		QuestEnv env = new QuestEnv(getOwner(), player, questId, dialogId);
		env.setExtendedRewardIndex(extendedRewardIndex);
		if (QuestEngine.getInstance().onDialog(env) && dialogId != DialogAction.SETPRO1.id()) {
			return true;
		}
		if (dialogId == DialogAction.SETPRO1.id()) {
        switch (getNpcId()) {
            case 831987:
			SkillEngine.getInstance().getSkill(getOwner(), 21650, 1, player).useWithoutPropSkill();
			break;
            case 831988:
			SkillEngine.getInstance().getSkill(getOwner(), 21650, 2, player).useWithoutPropSkill();
			break;
            case 831989:
			SkillEngine.getInstance().getSkill(getOwner(), 21650, 3, player).useWithoutPropSkill();
			break;
            case 831990:
			SkillEngine.getInstance().getSkill(getOwner(), 21650, 4, player).useWithoutPropSkill();
			break;
		}
		return true;
	}
        return false;

    }
}
