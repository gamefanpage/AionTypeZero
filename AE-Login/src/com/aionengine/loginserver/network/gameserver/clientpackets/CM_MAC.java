package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import org.slf4j.LoggerFactory;

/**
 * @author nrg
 */
public class CM_MAC extends GsClientPacket {

    private int accountId;
    private String address;

    @Override
    protected void readImpl() {
        accountId = readD();
        address = readS();
    }

    @Override
    protected void runImpl() {
        if (!AccountController.refreshAccountsLastMac(accountId, address))
            LoggerFactory.getLogger(CM_MAC.class).error("[WARN] We just weren't able to update account_data.last_mac for accountId " + accountId);
    }
}
