package com.aionengine.loginserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.dao.PremiumDAO;
import com.aionengine.loginserver.model.AccountToll;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_PREMIUM_RESPONSE;

/**
 * @author KID, Dr2co
 */
public class PremiumController {
	private Logger log = LoggerFactory.getLogger("PREMIUM_CTRL");
	private static PremiumController controller = new PremiumController();

	public static PremiumController getController() {
		return controller;
	}

	public static byte RESULT_FAIL = 1;
	public static byte RESULT_LOW_POINTS = 2;
	public static byte RESULT_OK = 3;
	public static byte RESULT_ADD = 4;

	private PremiumDAO dao;

	public PremiumController() {
		dao = DAOManager.getDAO(PremiumDAO.class);
		log.info("PremiumController is ready for requests.");
	}

	public void requestBuy(int accountId, int requestId, long cost, byte serverId, byte type) {
		AccountToll tolls = this.dao.getTolls(accountId);

		GameServerInfo server = GameServerTable.getGameServerInfo(serverId);
		if (server == null || server.getConnection() == null || !server.isAccountOnGameServer(accountId)) {
			log.error("Account " + accountId + " requested " + requestId + " from gs #" + serverId + " and server is down.");
			return;
		}

		//adding new tolls
		if (cost < 0) {
			switch (type) {
			case 0:
				tolls.setToll(tolls.getToll() + (cost * -1));
				break;
			case 1:
				tolls.setBonusToll(tolls.getBonusToll() + (cost * -1));
				break;
			}
			dao.updateTolls(accountId, tolls, 0, type);
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_ADD, tolls, type));
			return;
		}
		if (tolls.getTollsByType(type) < cost) {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_LOW_POINTS, tolls, type));
			return;
		}

		if (dao.updateTolls(accountId, tolls, cost, type)) {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_OK, tolls, type));
			log.info("Acount " + accountId + " succed in purchasing lot #" + requestId + " for " + cost + " from server #" + serverId);
		} else {
			server.getConnection().sendPacket(new SM_PREMIUM_RESPONSE(requestId, RESULT_FAIL, tolls, type));
			log.info("Acount " + accountId + " failed in purchasing lot #" + requestId + " for " + cost + " from server #" + serverId + ". !updatePoints");
		}
	}

}
