package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;

/**
 * @author KID
 */
public class CM_GS_PONG extends GsClientPacket {
    private byte serverId;
    private int pid;

    @Override
    protected void readImpl() {
        serverId = (byte) readC();
        pid = readD();
    }

    @Override
    protected void runImpl() {
        GameServerTable.pong(serverId, pid);
    }
}
