package com.aionengine.loginserver.network.gameserver.serverpackets;

import com.aionengine.loginserver.controller.BannedMacManager;
import com.aionengine.loginserver.model.base.BannedMacEntry;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.GsServerPacket;

import java.util.Map;

/**
 * @author KID
 */
public class SM_MACBAN_LIST extends GsServerPacket {

    private Map<String, BannedMacEntry> bannedList;

    public SM_MACBAN_LIST() {
        this.bannedList = BannedMacManager.getInstance().getMap();
    }

    @Override
    protected void writeImpl(GsConnection con) {
        writeC(9);
        writeD(bannedList.size());

        for (BannedMacEntry entry : bannedList.values()) {
            writeS(entry.getMac());
            writeQ(entry.getTime().getTime());
            writeS(entry.getDetails());
        }
    }
}
