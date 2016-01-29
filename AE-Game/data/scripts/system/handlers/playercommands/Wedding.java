package playercommands;

import org.typezero.gameserver.configs.main.WeddingsConfig;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.services.WeddingService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;

//By Evil_dnk

public class Wedding extends PlayerCommand {
    public Wedding() {
        super("wedding");
    }

    @Override
    public void execute(Player player, String... params) {
        if (!WeddingsConfig.WEDDINGS_ENABLE) {
            PacketSendUtility.sendMessage(player, "\u0421\u0432\u0430\u0434\u044c\u0431\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u044b \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435.");
            return;
        }
        if (params.length == 0 || params == null) {
            PacketSendUtility.sendMessage(player, "\u0421\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441: .wedding <cancel | wife>");
            return;
        }

        if (params[0].equalsIgnoreCase("cancel")) {
            if (!player.isMarried()) {
                PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u0441\u043e\u0441\u0442\u043e\u0438\u0442\u0435 \u0432 \u0431\u0440\u0430\u043a\u0435.");
                return;
            }

            if (World.getInstance().findPlayer(player.getPartnerName()) == null) {
                if (WeddingsConfig.WEDDINGS_DISSMIS_TOLL != 0) {

                    if (player.getPlayerAccount().getToll() < WeddingsConfig.WEDDINGS_DISSMIS_TOLL) {
                        // You do not have enough Points.
                        PacketSendUtility.sendMessage(player, "\u041d\u0430 \u0432\u0430\u0448\u0435\u043c \u0441\u0447\u0435\u0442\u0435 \u043d\u0435 \u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432 \u0434\u043b\u044f \u043e\u043f\u043b\u0430\u0442\u044b \u0443\u0441\u043b\u0443\u0433 \u0417\u0410\u0413\u0421\u0430 \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0441\u0442\u0438 \u0440\u0430\u0437\u0432\u043e\u0434. \u0414\u043b\u044f \u0440\u0430\u0437\u0432\u043e\u0434\u0430 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u043d\u0430\u043b\u0438\u0447\u0438\u0435 "+WeddingsConfig.WEDDINGS_DISSMIS_TOLL+ "\u041f\u043e\u0438\u043d\u0442\u043e\u0432");
                        return;
                    }
                    else
                    {
                    long toll = player.getPlayerAccount().getToll() - WeddingsConfig.WEDDINGS_TOLL;
                    player.getPlayerAccount().setToll(toll);
                    PacketSendUtility.sendMessage(player, "\u0421 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 \u0441\u043d\u044f\u0442\u043e  " + WeddingsConfig.WEDDINGS_DISSMIS_TOLL + " \u041f\u043e\u0438\u043d\u0442\u043e\u0432.");
                    }
                }
                WeddingService.getInstance().dismissMarriage(player);
                return;
            }

            if (WeddingsConfig.WEDDINGS_DISSMIS_TOLL != 0) {
                if (player.getPlayerAccount().getToll() < WeddingsConfig.WEDDINGS_DISSMIS_TOLL) {
                    // You do not have enough Points.
                    PacketSendUtility.sendMessage(player, "\u041d\u0430 \u0432\u0430\u0448\u0435\u043c \u0441\u0447\u0435\u0442\u0435 \u043d\u0435 \u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u0441\u0440\u0435\u0434\u0441\u0442\u0432 \u0434\u043b\u044f \u043e\u043f\u043b\u0430\u0442\u044b \u0443\u0441\u043b\u0443\u0433 \u0417\u0410\u0413\u0421\u0430 \u0447\u0442\u043e\u0431\u044b \u043f\u0440\u043e\u0438\u0437\u0432\u0435\u0441\u0442\u0438 \u0440\u0430\u0437\u0432\u043e\u0434. \u0414\u043b\u044f \u0440\u0430\u0437\u0432\u043e\u0434\u0430 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e \u043d\u0430\u043b\u0438\u0447\u0438\u0435 "+WeddingsConfig.WEDDINGS_DISSMIS_TOLL+ "\u041f\u043e\u0438\u043d\u0442\u043e\u0432");
                    return;
                }
                else{
                long toll = player.getPlayerAccount().getToll() - WeddingsConfig.WEDDINGS_TOLL;
                player.getPlayerAccount().setToll(toll);
                PacketSendUtility.sendMessage(player, "\u0421 \u0431\u0430\u043b\u0430\u043d\u0441\u0430 \u0441\u043d\u044f\u0442\u043e  " + WeddingsConfig.WEDDINGS_DISSMIS_TOLL + " \u041f\u043e\u0438\u043d\u0442\u043e\u0432.");
                }
            }

            WeddingService.getInstance().unDoWedding(player, World.getInstance().findPlayer(player.getPartnerName()));
            return;
        }

        if (params[0].equalsIgnoreCase("wife")) {

            if (!WeddingsConfig.WEDDINGS_ENABLE) {
                PacketSendUtility.sendMessage(player, "\u0421\u0432\u0430\u0434\u044c\u0431\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u044b.");
                return;
            }

            if (params == null || params.length != 2) {
                PacketSendUtility.sendMessage(player, "\u0421\u0438\u043d\u0442\u0430\u043a\u0441\u0438\u0441: .wedding wife <characterName>");
                return;
            }

            Player wife = World.getInstance().findPlayer(Util.convertName(params[1]));

            if (wife == null) {
                PacketSendUtility.sendMessage(player, "\u041d\u0435\u0432\u0435\u0441\u0442\u044b \u043d\u0435\u0442 \u043e\u043d\u043b\u0430\u0439\u043d.");
                return;
            }
            if (player.equals(wife)) {
                PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u0437\u044f\u0442\u044c \u0432 \u0436\u0435\u043d\u044b \u0441\u0435\u0431\u044f.");
                return;
            }
            if (wife.getWorldId() == 510010000 || wife.getWorldId() == 520010000) {
                PacketSendUtility.sendMessage(player, "\u041d\u0435\u0432\u0435\u0441\u0442\u0430 \u0432 \u0442\u044e\u0440\u044c\u043c\u0435.");
                return;
            }

            if (player.getWorldId() == 510010000 || player.getWorldId() == 520010000) {
                PacketSendUtility.sendMessage(player, "\u0412\u044b \u0432 \u0442\u044e\u0440\u044c\u043c\u0435.");
                return;
            }
            if (player.isMarried()) {
                PacketSendUtility.sendMessage(player, "\u041d\u0435\u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0432\u0441\u0442\u0443\u043f\u0438\u0442\u044c \u0432 \u043d\u043e\u0432\u044b\u0439 \u0431\u0440\u0430\u043a \u043d\u0435 \u0440\u0430\u0437\u043e\u0440\u0432\u0430\u0432 \u0442\u0435\u043a\u0443\u0449\u0438\u0439.");
                return;
            }

            if (wife.isMarried()) {
                PacketSendUtility.sendMessage(player, "\u0414\u0430\u043d\u043d\u044b\u0439 \u0438\u0433\u0440\u043e\u043a \u0443\u0436\u0435 \u0432 \u0441\u043e\u0441\u0442\u043e\u0438\u0442 \u0432 \u0431\u0440\u0430\u043a\u0435.");
                return;
            }
            if (player.getCommonData().getGender() != Gender.MALE) {
                PacketSendUtility.sendMessage(player, "\u041f\u0440\u043e\u0441\u0438\u0442\u044c \u043e \u0441\u0432\u0430\u0434\u044c\u0431\u0435 \u0434\u043e\u043b\u0436\u0435\u043d \u043a\u0430\u0432\u0430\u043b\u0435\u0440.");
                return;
            }

            WeddingService.getInstance().registerOffer(player, wife);
        }
    }
}
