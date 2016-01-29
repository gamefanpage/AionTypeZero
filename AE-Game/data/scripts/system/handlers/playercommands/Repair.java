package playercommands;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.controllers.PlayerController;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.dataholders.PlayerInitialData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.PlayerCommand;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapType;
import org.typezero.gameserver.world.WorldPosition;

/**
* @author Alex, DoYrdenDzirt
*
*/
public class Repair extends PlayerCommand {

        public Repair() {
                super("repair");
        }

        @Override
        public void execute(Player player, String... params) {
                if (params == null || params.length < 1) {
                        PacketSendUtility.sendMessage(player, ".repair <\u0418\u043c\u044f \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 \u043d\u0430 \u0441\u0432\u043e\u0435\u043c \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0435>");
                        return;
                }
                Player repairPlayer = null;
                String arg = Util.convertName(params[0]);
                PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonDataByName(arg);
                int accountId = DAOManager.getDAO(PlayerDAO.class).getAccountIdByName(arg);

                if (accountId == 0) {
                        PacketSendUtility.sendMessage(player, "\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u0436 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                        return;
                }

                if (player.getName().toLowerCase().equalsIgnoreCase(pcd.getName().toLowerCase())) {
                        PacketSendUtility.sendMessage(player, "\u041d\u0435\u043b\u044c\u0437\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043d\u0430 \u0441\u0435\u0431\u044f");
                        return;
                }

                if (accountId != player.getClientConnection().getAccount().getId()) {
                        PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 \u043d\u0430\u0445\u043e\u0434\u044f\u0449\u0435\u0433\u043e\u0441\u044f \u043d\u0430 \u0434\u0440\u0443\u0433\u043e\u043c \u0430\u043a\u043a\u0430\u0443\u043d\u0442\u0435");
                        return;
                }

                if (pcd.getPosition().getMapId() == WorldMapType.DF_PRISON.getId() || pcd.getPosition().getMapId() == WorldMapType.DE_PRISON.getId()) {
                        PacketSendUtility.sendMessage(player, "\u041d\u0435\u043b\u044c\u0437\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043d\u0430 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 \u0432 \u0442\u044e\u0440\u044c\u043c\u0435!");
                        return;
                }

                PlayerInitialData playerInitialData = DataManager.PLAYER_INITIAL_DATA;
                PlayerInitialData.LocationData ld = playerInitialData.getSpawnLocation(pcd.getRace());
                WorldPosition position = World.getInstance().createPosition(ld.getMapId(), ld.getX(), ld.getY(), ld.getZ(), ld.getHeading(), 0);
                pcd.setPosition(position);
                repairPlayer = new Player(new PlayerController(), pcd, new PlayerAppearance(), player.getClientConnection().getAccount());
                DAOManager.getDAO(PlayerDAO.class).storePlayer(repairPlayer);
                player.getClientConnection().getAccount().getPlayerAccountData(pcd.getPlayerObjId()).setPlayerCommonData(pcd);
                PacketSendUtility.sendMessage(player, "\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u0436 " + pcd.getName() + " \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d, \u0438 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d \u0432 \u043d\u0430\u0447\u0430\u043b\u044c\u043d\u0443\u044e \u0442\u043e\u0447\u043a\u0443 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430");
        }
}
