package playercommands;

import org.typezero.gameserver.model.Wedding;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.WeddingService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author synchro2
 */
public class cmd_answer extends PlayerCommand {

	public cmd_answer() {
		super("answer");
	}

	@Override
	public void execute(Player player, String... params) {
		Wedding wedding = WeddingService.getInstance().getWedding(player);

		if (params == null || params.length != 1) {
			PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
			return;
		}

		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't use this command on prison.");
			return;
		}

		if (wedding == null) {
			PacketSendUtility.sendMessage(player, "Wedding not started.");
            return;
		}

		if (params[0].toLowerCase().equals("yes")) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u0441\u043e\u0433\u043b\u0430\u0441\u0438\u043b\u0438\u0441\u044c.");
			WeddingService.getInstance().acceptWedding(player);
		}

		if (params[0].toLowerCase().equals("no")) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043e\u0442\u043a\u0430\u0437\u0430\u043b\u0438\u0441\u044c.");
			WeddingService.getInstance().cancelWedding(player);
		}

	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax .answer yes/no.");
	}
}
