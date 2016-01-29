package playercommands;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author xXMashUpXx
 */
public class AsmoChannel extends PlayerCommand {

    public AsmoChannel() {
        super("asmo");
    }

    @Override
    public void execute(Player player, String... params) {
        if (player.getRace() == Race.ASMODIANS && !player.isInPrison()) {
            int i = 1;
            boolean check = true;
            Race adminRace = player.getRace();
            String adminname = "";

            if (params.length < 1) {
                PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .asmo <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
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
                sbMessage = new StringBuilder("([color:\u0410\u0441\u043c\u043e\u0434;0 1 0][color:\u0438\u0430\u043d\u0435;0 1 0])" + " " + player.getName() + ": ");
            }
            adminRace = Race.ASMODIANS;

            for (String s : params) {
                if (i++ != 0 && (check)) {
                    sbMessage.append(s + " ");
                }
            }

            String message = sbMessage.toString().trim();
            int messageLenght = message.length();

            final String sMessage = message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);
            final boolean toAll = params[0].equals("ALL");
            final Race race = adminRace;

            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player player) {
                    if (toAll || player.getRace() == race || (player.getAccessLevel() > 0)) {
                        PacketSendUtility.sendMessage(player, sMessage);
                    }
                }
            });
        } else {
            PacketSendUtility.sendMessage(player, "\u0412\u0430\u0448\u0430 \u0440\u0430\u0441\u0430 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u044d\u0442\u043e\u0442 \u0447\u0430\u0442 . \u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 .ely");
        }
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .asmo <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
    }
}
