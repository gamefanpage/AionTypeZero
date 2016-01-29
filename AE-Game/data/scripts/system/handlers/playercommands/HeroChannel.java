package playercommands;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Romanz
 */
public class HeroChannel extends PlayerCommand {

    public HeroChannel() {
        super("hero");
    }

    @Override
    public void execute(Player player, String... params) {
	if (player.getAbyssRank().getRank().getId() >= AbyssRankEnum.COMMANDER.getId()) {
        int i = 1;
		boolean check = true;

        if (params.length < 1) {
            PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .hero <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
            return;
        }
            StringBuilder sbMessage;
                sbMessage = new StringBuilder("([color:\u0412\u0435\u043b\u0438\u043a;0 1 1][color:\u0438\u0439 \u0432\u043e;0 1 1][color:\u0438\u043d;0 1 1])" + " " + player.getName() + ": ");

            for (String s : params) {
                if (i++ != 0 && (check)) {
                    sbMessage.append(s + " ");
                }
            }

        String message = sbMessage.toString().trim();
        int messageLenght = message.length();

        final String sMessage = message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player player) {
                        PacketSendUtility.sendMessage(player, sMessage);
                }
            });
        } else {
            PacketSendUtility.sendMessage(player, "\u0412\u0430\u0448 \u0440\u0435\u0439\u0442\u0438\u043d\u0433 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u0435\u043d \u0434\u043b\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u0434\u0430\u043d\u043d\u043e\u0433\u043e \u0447\u0430\u0442\u0430.");
        }
    }

    @Override
    public void onFail(Player player, String message) {
        PacketSendUtility.sendMessage(player, "\u0441\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441 : .hero <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
    }
}
