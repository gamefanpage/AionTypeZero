/*
 * Copyright (c) 2015, TypeZero Engine (game.developpers.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of TypeZero Engine nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package com.aionengine.loginserver.network.aion.serverpackets;

import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.network.aion.AionServerPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;

import java.util.Collection;
import java.util.Map;

/**
 * @author -Nemesiss-
 * @modified cura
 */
public class SM_SERVER_LIST extends AionServerPacket {
    public SM_SERVER_LIST() {
        super(0x04);
    }

    @Override
    protected void writeImpl(LoginConnection con) {
        Collection<GameServerInfo> servers = GameServerTable.getGameServers();
        Map<Integer, Integer> charactersCountOnServer = null;

        int accountId = con.getAccount().getId();
        int maxId = 0;

        charactersCountOnServer = AccountController.getGSCharacterCountsFor(accountId);

        writeC(servers.size());// servers
        writeC(con.getAccount().getLastServer());// last server
        for (GameServerInfo gsi : servers) {
            if (gsi.getId() > maxId)
                maxId = gsi.getId();

            writeC(gsi.getId());// server id
            writeB(gsi.getIPAddressForPlayer(con.getIP())); // server IP
            writeD(gsi.getPort());// port
            writeC(0x00); // age limit
            writeC(0x01);// pvp=1
            writeH(gsi.getCurrentPlayers());// currentPlayers
            writeH(gsi.getMaxPlayers());// maxPlayers
            writeC(gsi.isOnline() ? 1 : 0);// ServerStatus, up=1
            writeD(1);// bits);
            writeC(0);// server.brackets ? 0x01 : 0x00);
        }

        writeH(maxId + 1);
        writeC(0x01);

        for (int i = 1; i <= maxId; i++) {
            if (charactersCountOnServer.containsKey(i))
                writeC(charactersCountOnServer.get(i));
            else
                writeC(0);
        }
    }
}
