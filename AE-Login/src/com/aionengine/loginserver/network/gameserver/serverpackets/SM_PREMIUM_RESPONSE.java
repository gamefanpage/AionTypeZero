package com.aionengine.loginserver.network.gameserver.serverpackets;

import com.aionengine.loginserver.model.AccountToll;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.GsServerPacket;

/**
 * @author KID, Dr2co
 */
public class SM_PREMIUM_RESPONSE extends GsServerPacket {

    private int requestId;
    private int result;
    private AccountToll tolls;
    private byte type;

    public SM_PREMIUM_RESPONSE(int requestId, int result, AccountToll tolls, byte type) {
        this.requestId = requestId;
        this.result = result;
        this.tolls = tolls;
        this.type = type;
    }

    @Override
    protected void writeImpl(GsConnection con) {
        writeC(10);
        writeD(requestId);
        writeD(result);
        writeC(type);
        switch (type) {
            case 0:
                writeQ(tolls.getToll());
                break;
            case 1:
                writeQ(tolls.getBonusToll());
                break;
        }

    }
}
