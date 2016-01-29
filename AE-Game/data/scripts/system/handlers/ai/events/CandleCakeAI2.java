package ai.events;

import ai.GeneralNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.MuiService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Romanz
 */

@AIName("candle_cake")
public class CandleCakeAI2 extends GeneralNpcAI2 {

  	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {

		  if (dialogId == 10000) {
				if (player.getInventory().getItemCountByItemId(164000225) > 0) {
					player.getInventory().decreaseByItemId(164000225, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20884, 1, player).useWithoutPropSkill();
                }
                else if (player.getInventory().getItemCountByItemId(164000226) > 0) {
					player.getInventory().decreaseByItemId(164000226, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20884, 1, player).useWithoutPropSkill();
                }
                else if (player.getInventory().getItemCountByItemId(164000235) > 0) {
					player.getInventory().decreaseByItemId(164000235, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20885, 1, player).useWithoutPropSkill();
                }
                else if (player.getInventory().getItemCountByItemId(164000237) > 0) {
					player.getInventory().decreaseByItemId(164000237, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20885, 1, player).useWithoutPropSkill();
                }
                else if (player.getInventory().getItemCountByItemId(164000236) > 0) {
					player.getInventory().decreaseByItemId(164000236, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20886, 1, player).useWithoutPropSkill();
                }
                else if (player.getInventory().getItemCountByItemId(164000238) > 0) {
					player.getInventory().decreaseByItemId(164000238, 1);
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					SkillEngine.getInstance().getSkill(getOwner(), 20886, 1, player).useWithoutPropSkill();
                }
				else {
					PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
					PacketSendUtility.sendBrightYellowMessageOnCenter(player, MuiService.getInstance().getMessage("EVENT_ALCHIMIC"));
                }
		 }
		 return true;
	  }

  }
