/*
 * [IVENT] The sacred fire
 */
package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Dision
 * M.O.G. Devs Team
 */

@AIName("the_sacred_fire")
public class TheSacredFireAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
        switch (getNpcId()) {
            case 831795: {
				super.handleDialogStart(player);
				break;
			} default: {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
				break;
			}
		}
	}
	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {

		  if (dialogId == 10000) {
		  PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			SkillEngine.getInstance().getSkill(getOwner(), 21359, 1, player).useWithoutPropSkill();
		 }
		 return true;
	  }

  }
