package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionengine.loginserver.dao.AccountDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.AccountMembershipType;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_LS_CONTROL_RESPONSE;

/**
 * @author Aionchs-Wylovech
 * @author Dr2co
 */
public class CM_LS_CONTROL extends GsClientPacket {

	private String accountName;

	private int param;

	private int type;

	private int day;

	private String playerName;

	private String adminName;

	private boolean result;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		type = readC();
		adminName = readS();
		accountName = readS();
		playerName = readS();
		param = readC();
		day = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Account account = DAOManager.getDAO(AccountDAO.class).getAccount(accountName);

		switch (type) {
		case 1:
			account.setAccessLevel((byte) param);
			break;
		case 2:
		case 3:
		case 4:
		case 5:
			type:
				for (AccountMembershipType types : AccountMembershipType.values()) {
					if (types.getId() == type) {
						account.getMembership().setMemberShipByType(types, (byte) param);
						account.getMembership().updateMemberShipExpire(types, day);
						break type;
					}
				}
		break;
		}

		result = DAOManager.getDAO(AccountDAO.class).updateMembership(account);
		sendPacket(new SM_LS_CONTROL_RESPONSE(type, result, playerName, account.getId(), param, adminName));
	}

}
