package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionengine.loginserver.controller.PremiumController;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;

/**
 * @author KID, Dr2co
 */
public class CM_PREMIUM_CONTROL extends GsClientPacket {

    private int accountId;
    private int requestId;
    private long requiredCost;
    private byte serverId;
    private byte type;

    @Override
    protected void readImpl() {
        accountId = readD();
        requestId = readD();
        requiredCost = readQ();
        serverId = (byte) readC();
        type = (byte) readC();
    }

    @Override
    protected void runImpl() {
        PremiumController.getController().requestBuy(accountId, requestId, requiredCost, serverId, type);
    }
}
