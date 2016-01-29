package playercommands;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author loleron pieced together from DyeAction.java, Set.java
 */
public class Kraska extends PlayerCommand {

	public Kraska() {
		super("\u043a\u0440\u0430\u0441\u043a\u0430");
	}

	@Override
	public void execute(Player admin, String... params) {
		Player target;

		// Add a check to prevent players to dye other people
		if (admin.getAccessLevel() > 0 && admin.getTarget() instanceof Player) {
			target = (Player) admin.getTarget();
		}
		else {
			target = admin;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0432\u0437\u044f\u0442\u044c \u0441\u0435\u0431\u044f \u0432 \u0442\u0430\u0440\u0433\u0435\u0442 !");
			return;
		}

		if (params.length == 0 || params.length > 2) {
			PacketSendUtility.sendMessage(admin, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 .\u043a\u0440\u0430\u0441\u043a\u0430 <\u043a\u0440\u0430\u0441\u043a\u0430 \u0446\u0432\u0435\u0442 | \u043e\u0442\u043c\u0435\u043d\u0430>");
			return;
		}

		long price = CustomConfig.DYE_PRICE;
		if (admin.getInventory().getKinah() < price && !admin.isGM()) {
			PacketSendUtility.sendMessage(admin, "\u0412\u0430\u043c \u043d\u0443\u0436\u043d\u043e \u0438\u043c\u0435\u0442\u044c " + CustomConfig.DYE_PRICE + " \u0434\u043b\u044f \u043f\u043e\u043a\u0440\u0430\u0441\u043a\u0438.");
			return;
		}

		String color = "";
		if (params.length == 2) {
			if (params[1].equalsIgnoreCase("petal")) {
				color = params[0];
			}
			else {
				color = params[0] + " " + params[1];
			}
		}
		else {
			color = params[0];
		}

		int rgb = 0;
		int bgra = 0;

		if (color.equalsIgnoreCase("\u0433\u043e\u043b\u0443\u0431\u0430\u044f")) {
			color = "1f87f5";
		} // 169200002, 169201002
		else if (color.equalsIgnoreCase("\u043a\u043e\u0440\u0438\u0447\u043d\u0435\u0432\u0430\u044f")) {
			color = "66250e";
		} // 169200003, 169201003
		else if (color.equalsIgnoreCase("\u0444\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u0430\u044f")) {
			color = "c38df5";
		} // 169200004, 169201004
		else if (color.equalsIgnoreCase("\u043a\u0440\u0430\u0441\u043d\u0430\u044f")) {
			color = "c22626";
		} // 169200005, 169201005, 169220001, 169230001, 169231001
		else if (color.equalsIgnoreCase("\u0431\u0435\u043b\u0430\u044f")) {
			color = "ffffff";
		} // 169200006, 169201006, 169220002, 169231002
		else if (color.equalsIgnoreCase("\u0447\u0435\u0440\u043d\u0430\u044f")) {
			color = "000000";
		} // 169200007, 169201007, 169230008, 169231008
		else if (color.equalsIgnoreCase("\u043e\u0440\u0430\u043d\u0436\u0435\u0432\u0430\u044f")) {
			color = "e36b00";
		} // 169201008, 169220004, 169230009, 169231009
		else if (color.equalsIgnoreCase("\u043f\u0443\u0440\u043f\u0443\u0440\u043d\u0430\u044f")) {
			color = "440b9a";
		} // 169201009, 169220005, 169230007, 169231003
		else if (color.equalsIgnoreCase("\u0440\u043e\u0437\u043e\u0432\u0430\u044f")) {
			color = "d60b7e";
		} // 169201010, 169220006, 169230010, 169231010
		else if (color.equalsIgnoreCase("\u0436\u0435\u043b\u0442\u0430\u044f")) {
			color = "fcd251";
		} // 169201011, 169220007, 169230004, 169231004
		else if (color.equalsIgnoreCase("\u0437\u0435\u043b\u0435\u043d\u0430\u044f")) {
			color = "5f730e";
		} // 169201013, 169220009, 169230005, 169231006
		else if (color.equalsIgnoreCase("\u0441\u0438\u043d\u044f\u044f")) {
			color = "14398b";
		} // 169201014, 169220010, 169230006, 169231007
		try {
			rgb = Integer.parseInt(color, 16);
			bgra = 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
		}

		catch (NumberFormatException e) {
			if (!color.equalsIgnoreCase("\u043e\u0442\u043c\u0435\u043d\u0430")) {
				PacketSendUtility.sendMessage(admin, "[\u041e\u0448\u0438\u0431\u043a\u0430] \u041d\u0435 \u0432\u0435\u0440\u043d\u043e \u0443\u043a\u0430\u0437\u0430\u043d \u0446\u0432\u0435\u0442: " + color);
				return;
			}
		}

		if (!admin.isGM())
			admin.getInventory().decreaseKinah(price);

		for (Item targetItem : target.getEquipment().getEquippedItemsWithoutStigma()) {
			if (color.equals("\u043e\u0442\u043c\u0435\u043d\u0430")) {
				targetItem.setItemColor(0);
			}
			else {
				targetItem.setItemColor(bgra);
			}
			ItemPacketService.updateItemAfterInfoChange(target, targetItem);
		}
		PacketSendUtility.broadcastPacket(target, new SM_UPDATE_PLAYER_APPEARANCE(target.getObjectId(), target
			.getEquipment().getEquippedForApparence()), true);
		target.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		if (target.getObjectId() != admin.getObjectId()) {
			PacketSendUtility.sendMessage(target, "You have been dyed by " + admin.getName() + "!");
		}
		PacketSendUtility.sendMessage(admin, "\u041f\u043e\u043a\u0440\u0430\u0441\u043a\u0430 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 " + target.getName() + " \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0430 \u0443\u0441\u043f\u0435\u0448\u043d\u043e!");
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}
}
