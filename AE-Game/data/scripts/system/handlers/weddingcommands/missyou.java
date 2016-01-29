package weddingcommands;

import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.chathandlers.ChatCommand;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;

/**
 * @author synchro2
 */
public class missyou extends PlayerCommand {

	public missyou() {
		super("missyou");
	}

	@Override
	public void execute(final Player player, String... params) {

		Player partner = player.findPartner();

		if (partner == null) {
			PacketSendUtility.sendMessage(player, "\u0412\u0430\u0448\u0435\u0439 \u043f\u043e\u043b\u043e\u0432\u0438\u043d\u043a\u0438 \u043d\u0435\u0442 \u0432  \u0438\u0433\u0440\u0435.");
			return;
		}
		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043a\u043e\u043c\u0430\u043d\u0434\u0443 \u043d\u0430\u0445\u043e\u0434\u044f\u0441\u044c \u0432 \u0442\u044e\u0440\u044c\u043c\u0435.");
			return;
		}

		if (partner.getWorldId() == 510010000 || partner.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0435\u0440\u0435\u043c\u0435\u0441\u0442\u0438\u0442\u0441\u044f " + partner.getName() +", \u0432\u0430\u0448 \u043f\u0430\u0440\u0442\u043d\u0435\u0440 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0442\u044e\u0440\u044c\u043c\u0435.");
			return;
		}

		if(partner.isInInstance()) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0435\u0440\u0435\u043c\u0435\u0441\u0442\u0438\u0442\u0441\u044f " + partner.getName() +", \u0442\u0430\u043a \u043a\u0430\u043a \u0412\u0430\u0448 \u043f\u0430\u0440\u0442\u043d\u0435\u0440 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0434\u0430\u043d\u0436\u0435.");
			return;
		}

		if(!player.isCommandInUse()) {
			TeleportService2.teleportTo(player, partner.getWorldId(), partner.getInstanceId(), partner.getX(), partner.getY(),
			partner.getZ(), partner.getHeading(), TeleportAnimation.BEAM_ANIMATION);
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043f\u0435\u0440\u0435\u043c\u0435\u0441\u0442\u0438\u043b\u0438\u0441\u044c \u043a " + partner.getName() + ".");
			player.setCommandUsed(true);

			ThreadPoolManager.getInstance().schedule(new Runnable() {
				@Override
				public void run() {
					player.setCommandUsed(false);
				}
			}, 60 * 60 * 1000);
		}
		else
			PacketSendUtility.sendMessage(player, "\u041a\u043e\u043c\u0430\u043d\u0434\u0443 \u043c\u043e\u0436\u043d\u043e \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e 1 \u0440\u0430\u0437 \u0432 \u0447\u0430\u0441.");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "\u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c.");
	}
}
