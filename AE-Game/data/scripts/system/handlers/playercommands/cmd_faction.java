package playercommands;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.model.ChatType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.network.aion.serverpackets.SM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.player.PlayerChatService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;
import org.apache.commons.lang.StringUtils;

/**
 * @author Shepper, modified: bobobear
 */
public class cmd_faction extends PlayerCommand {

	public cmd_faction() {
		super("chat");
	}

	@Override
	public void execute(Player player, String... params) {
		Storage sender = player.getInventory();

		if (!CustomConfig.FACTION_CMD_CHANNEL) {
			PacketSendUtility.sendMessage(player, "The command is disabled.");
			return;
		}

		if (params == null || params.length < 1) {
			PacketSendUtility.sendMessage(player, "syntax .chat <message>");
			return;
		}

		if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
			PacketSendUtility.sendMessage(player, "You can't talk in Prison.");
			return;
		}
		else if (player.isGagged()) {
			PacketSendUtility.sendMessage(player, "You are gaged, you can't talk.");
			return;
		}

		if (!CustomConfig.FACTION_FREE_USE) {
			if (sender.getKinah() > CustomConfig.FACTION_USE_PRICE)
				sender.decreaseKinah(CustomConfig.FACTION_USE_PRICE);
			else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_NOT_ENOUGH_MONEY);
				return;
			}
		}

		if (PlayerChatService.isFlooding(player)) {
			return;
		}

		String message = StringUtils.join(params, " ");
		String LogMessage = message;

		if (CustomConfig.FACTION_CHAT_CHANNEL) {
			ChatType channel = ChatType.CH1;

			for (Player listener : World.getInstance().getAllPlayers()) {
				if (listener.getAccessLevel() > 1) {
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(),
							(player.getRace() == Race.ASMODIANS ? "[[color:\u0410\u0441\u043c\u043e\u0434;0 1 0][color:\u0438\u0430\u043d\u0435;0 1 0]] " : "[[color:\u042d\u043b\u0438\u0439;0 1 0][color:\u0446\u044b;0 1 0]] ") + player.getName(), message, channel));
				}
				else if (listener.getRace() == player.getRace()) {
					PacketSendUtility.sendPacket(listener, new SM_MESSAGE(player.getObjectId(),
							(player.getRace() == Race.ASMODIANS ? "[[color:\u0410\u0441\u043c\u043e\u0434;0 1 0][color:\u0438\u0430\u043d\u0435;0 1 0]] " : "[[color:\u042d\u043b\u0438\u0439;0 1 0][color:\u0446\u044b;0 1 0]] ") + player.getName(), message, channel));
				}
			}
		}
		else {
			message = player.getName() + ": " + message;
			for (Player a : World.getInstance().getAllPlayers()) {
				if (a.getAccessLevel() > 1) {
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "([color:\u0410\u0441\u043c\u043e\u0434;0 1 0][color:\u0438\u0430\u043d\u0435;0 1 0]) " : "([color:\u042d\u043b\u0438\u0439;0 1 0][color:\u0446\u044b;0 1 0]) ") + message);
				}
				else if (a.getRace() == player.getRace()) {
					PacketSendUtility.sendMessage(a, (player.getRace() == Race.ASMODIANS ? "([color:\u0410\u0441\u043c\u043e\u0434;0 1 0][color:\u0438\u0430\u043d\u0435;0 1 0]) " : "([color:\u042d\u043b\u0438\u0439;0 1 0][color:\u0446\u044b;0 1 0]) ") + message);
				}
			}
		}

		if (LoggingConfig.LOG_FACTION) {
			PlayerChatService.chatLogging(player, ChatType.NORMAL, "[Faction Msg] " + LogMessage);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		// TODO Auto-generated method stub
	}

}
