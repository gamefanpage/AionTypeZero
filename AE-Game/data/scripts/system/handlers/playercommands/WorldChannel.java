package playercommands;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author xXMashUpXx
 */
public class WorldChannel extends PlayerCommand {

    public WorldChannel() {
        super("world");
    }

    @Override
    public void execute(Player player, String... params) {
        int i = 1;
        boolean check = true;
        String adminname = "";

        if (params.length < 1) {
            PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .world <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
            return;
        }

        if (player.isGM()) {
            	adminname = "([color:\u0410\u0434\u043c\u0438\u043d;1 0 0][color:\u0438\u0441\u0442\u0440\u0430;1 0 0][color:\u0446\u0438\u044f;1 0 0]) ";
        }

        adminname += player.getName() + ": ";

        StringBuilder sbMessage;
        if (player.isGM()) {
            sbMessage = new StringBuilder(adminname);
        } else {
            sbMessage = new StringBuilder("([color:\u041c\u0438\u0440;0 1 0])" + " " + player.getName() + ": ");
        }

        for (String s : params) {
            if (i++ != 0 && (check)) {
                sbMessage.append(s + " ");
            }
        }

        String message = sbMessage.toString().trim();
        int messageLenght = message.length();

        final String sMessage = message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);
        if (player.isGM()) {

		    World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			    @Override
			    public void visit(Player player) {
				    PacketSendUtility.sendMessage(player, sMessage);
			    }
		    });
        } else if (!player.isGM() && !player.isInPrison()) {
                World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                    @Override
                    public void visit(Player player) {
                        PacketSendUtility.sendMessage(player, sMessage);
                    }
                });
        } else {
            return;
        }
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .world <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
    }
}
