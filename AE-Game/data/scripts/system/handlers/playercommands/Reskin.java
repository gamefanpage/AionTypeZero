package playercommands;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_ACCOUNT_TOLL_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wakizashi, Imaginary
 * @revork Alex
 */
public class Reskin extends PlayerCommand /*��� AL 3.0+ extends ChatCommand*/ {

    public Reskin() {
        super("reskin");
    }

    @Override
    public void execute(Player admin, String... params) {
        if (params.length != 2) {
            onFail(admin, null);
            return;
        }

        if (admin.getClientConnection().getAccount().getMembership() < 0 && !admin.isGM()) {
            PacketSendUtility.sendYellowMessageOnCenter(admin, "\u042d\u0442\u0430 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430!");
            return;
        }

        Player target = admin;
        VisibleObject creature = admin.getTarget();
        if (admin.getTarget() instanceof Player && admin.isGM()) {
            target = (Player) creature;
        }
        int oldItemId = 0;
        int newItemId = 0;
        try {
            String item = params[0];
            if (item.equals("[item:")) {
                item = params[1];
                Pattern id = Pattern.compile("(\\d{9})");
                Matcher result = id.matcher(item);
                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                }
            } else {
                Pattern id = Pattern.compile("\\[item:(\\d{9})");
                Matcher result = id.matcher(item);

                if (result.find()) {
                    oldItemId = Integer.parseInt(result.group(1));
                } else {
                    oldItemId = Integer.parseInt(params[0]);
                }
            }
            try {
                String items = params[1];
                if (items.equals("[item:")) {
                    items = params[2];
                    Pattern id = Pattern.compile("(\\d{9})");
                    Matcher result = id.matcher(items);
                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    }
                } else {
                    Pattern id = Pattern.compile("\\[item:(\\d{9})");
                    Matcher result = id.matcher(items);

                    if (result.find()) {
                        newItemId = Integer.parseInt(result.group(1));
                    } else {
                        newItemId = Integer.parseInt(params[1]);
                    }
                }
            } catch (NumberFormatException ex) {
                PacketSendUtility.sendMessage(admin, "1 " + (admin.isGM() ? ex : ""));
                return;
            } catch (Exception ex2) {
                PacketSendUtility.sendMessage(admin, "2 " + (admin.isGM() ? ex2 : ""));
                return;
            }
        } catch (NumberFormatException ex) {
            PacketSendUtility.sendMessage(admin, "3 " + (admin.isGM() ? ex : ""));
            return;
        } catch (Exception ex2) {
            PacketSendUtility.sendMessage(admin, "4 " + (admin.isGM() ? ex2 : ""));
            return;
        }
        if (DataManager.ITEM_DATA.getItemTemplate(newItemId) == null) {
            PacketSendUtility.sendMessage(admin, "Item id is incorrect: " + newItemId);
            return;
        }
        if (!admin.isGM()) {
            target = admin;
        }

        int tollPrice = 30;
        List<Item> items = target.getInventory().getItemsByItemId(oldItemId);
        List<Item> itemnew = target.getInventory().getItemsByItemId(newItemId);
        if (oldItemId == newItemId) {
            PacketSendUtility.sendMessage(admin, "\u0421 \u0443\u043c\u0430 \u0441\u043e\u0448\u043e\u043b \u0447\u0442\u043e\u043b\u044c? :D");
            return;
        }

        // ������ ��� �� ����� �������. ����� �� ������ , ��� �� ��� � �.�.
        if (DataManager.ITEM_DATA.getItemTemplate(oldItemId).getItemSlot() != DataManager.ITEM_DATA.getItemTemplate(newItemId).getItemSlot()) {
            PacketSendUtility.sendMessage(admin, "\u041d\u0435\u043b\u044c\u0437\u044f :D");
            return;
        }

        if (itemnew.isEmpty() && !admin.isGM()) {
            ss(target, tollPrice, newItemId, items);
            return;
        }
        if (items.isEmpty()) {
            if (admin.isGM()) {
                PacketSendUtility.sendMessage(admin, "Old itemID \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 \u0432\u0437\u044f\u0442\u043e\u0433\u043e \u0432 \u0442\u0430\u0433\u0440\u0435\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435.");
                return;
            } else {
                PacketSendUtility.sendMessage(admin, "Old itemID \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u0432 \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u0435.");
                return;
            }
        }
        Iterator<Item> iter = items.iterator();
        Item item = iter.next();
        if (!admin.isGM() && !itemnew.isEmpty()) {
            item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(newItemId));
            PacketSendUtility.sendYellowMessageOnCenter(admin, "\u0412\u0438\u0434 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0435\u043d!");
            admin.getInventory().decreaseByItemId(newItemId, 1);
        } else {
            item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(newItemId));
            PacketSendUtility.sendYellowMessageOnCenter(admin, "\u0412\u0438\u0434 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0435\u043d!");
        }
    }

    public void ss(final Player admin, final int toll, final int itemId, final List<Item> items) {
        final long tolls = admin.getClientConnection().getAccount().getToll();
        RequestResponseHandler responseHandler = new RequestResponseHandler(admin) {
            public void acceptRequest(Creature p2, Player p) {
                if (tolls < toll) {
                    PacketSendUtility.sendMessage(admin, "\u0423 \u0432\u0430\u0441 \u043d\u0435 \u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043f\u043e\u0438\u043d\u0442\u043e\u0432 \u0434\u043b\u044f \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0432\u0438\u0434\u0430.\u041d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " + toll + ". \u041d\u0430 \u0432\u0430\u0448\u0435\u043c \u0441\u0447\u0435\u0442\u0443: " + tolls + "");
                    return;
                }
                //p.getClientConnection().getAccount().setToll(tolls - toll);
                //decToll(p, toll);
                p.getClientConnection().getAccount().setToll(tolls - toll);
                InGameShopEn.getInstance().delToll(admin, toll);
                Iterator<Item> iter = items.iterator();
                Item item = iter.next();
                item.setItemSkinTemplate(DataManager.ITEM_DATA.getItemTemplate(itemId));
                PacketSendUtility.sendYellowMessageOnCenter(admin, "\u0412\u0438\u0434 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0438\u0437\u043c\u0435\u043d\u0435\u043d!");
                PacketSendUtility.sendYellowMessageOnCenter(admin, "\u0417\u0430 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u0435 \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0432\u0438\u0434\u0430 \u0441 \u0432\u0430\u0441 \u0431\u044b\u043b\u043e \u0441\u043f\u0438\u0441\u0430\u043d\u043e " + toll + " \u043f\u043e\u0438\u043d\u0442\u043e\u0432");
            }

            public void denyRequest(Creature p2, Player p) {
            }
        };
        boolean requested = admin.getResponseRequester().putRequest(902247, responseHandler);
        if (requested) {
            PacketSendUtility.sendPacket(admin, new SM_QUESTION_WINDOW(902247, 0, 0, "\u0414\u043b\u044f \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u0432\u043d\u0435\u0448\u043d\u0435\u0433\u043e \u0432\u0438\u0434\u0430 \u0432\u0430\u043c \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c\u043e " + toll + " \u043f\u043e\u0438\u043d\u0442\u043e\u0432. \u041d\u0430 \u0432\u0430\u0448\u0435\u043c \u0441\u0447\u0435\u0442\u0443 : " + tolls + ". \u0416\u0435\u043b\u0430\u0435\u0442\u0435 \u0438\u0437\u043c\u0435\u043d\u0438\u0442\u044c \u0432\u043d\u0435\u0448\u043d\u0438\u0439 \u0432\u0438\u0434?"));
        }
    }

    @Override
    public void onFail(Player admin, String message) {
        PacketSendUtility.sendMessage(admin, "syntax .reskin <Link@ | \u0412\u0430\u0448 \u0438\u0442\u0435\u043c ID> <Link@ | \u0412\u043d\u0435\u0448\u043a\u0430 ID>");
    }

    public void decToll(Player p, int cnt) {
        int rfinal = (int) (p.getPlayerAccount().getToll() - cnt);
        if (LoginServer.getInstance().sendPacket(new SM_ACCOUNT_TOLL_INFO(rfinal, p.getAcountName()))) {
            p.getPlayerAccount().setToll(rfinal);
            PacketSendUtility.sendPacket(p, new SM_TOLL_INFO(rfinal));
        }
    }
}
