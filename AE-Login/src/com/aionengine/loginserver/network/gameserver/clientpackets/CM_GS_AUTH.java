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

package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionemu.commons.network.IPRange;
import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.network.gameserver.GsAuthResponse;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_GS_AUTH_RESPONSE;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_MACBAN_LIST;
import com.aionengine.loginserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is authentication packet that gs will send to login server for registration.
 *
 * @author -Nemesiss-
 */
public class CM_GS_AUTH extends GsClientPacket {

    private final Logger log = LoggerFactory.getLogger(CM_GS_AUTH.class);
    /**
     * Password for authentication
     */
    private String password;

    /**
     * Id of GameServer
     */
    private byte gameServerId;

    /**
     * Maximum number of players that this Gameserver can accept.
     */
    private int maxPlayers;

    /**
     * Port of this Gameserver.
     */
    private int port;

    /**
     * Default address for server
     */
    private byte[] defaultAddress;

    /**
     * List of IPRanges for this gameServer
     */
    private List<IPRange> ipRanges;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        gameServerId = (byte) readC();

        byte len1 = (byte) readC();
        defaultAddress = readB(len1);
        int size = readD();
        ipRanges = new ArrayList<IPRange>(size);
        for (int i = 0; i < size; i++) {
            ipRanges.add(new IPRange(readB(readC()), readB(readC()), readB(readC())));
        }

        port = readH();
        maxPlayers = readD();
        password = readS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        final GsConnection client = this.getConnection();

        GsAuthResponse resp = GameServerTable.registerGameServer(client, gameServerId, defaultAddress, ipRanges, port, maxPlayers, password);
        switch (resp) {
            case AUTHED:
                log.info("Gameserver #" + gameServerId + " is now online.");
                client.setState(GsConnection.State.AUTHED);
                client.sendPacket(new SM_GS_AUTH_RESPONSE(resp));
                ThreadPoolManager.getInstance().schedule(new Runnable() {
                    @Override
                    public void run() {
                        client.sendPacket(new SM_MACBAN_LIST());
                    }
                }, 500);
                break;

            default:
                client.close(new SM_GS_AUTH_RESPONSE(resp), false);
        }
    }
}
