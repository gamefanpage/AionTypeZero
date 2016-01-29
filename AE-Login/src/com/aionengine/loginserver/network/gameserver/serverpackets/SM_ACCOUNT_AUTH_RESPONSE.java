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


package com.aionengine.loginserver.network.gameserver.serverpackets;

import com.aionengine.loginserver.model.AccountMembership;
import com.aionengine.loginserver.model.AccountTime;
import com.aionengine.loginserver.model.AccountToll;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.GsServerPacket;

/**
 * In this packet LoginServer is answering on GameServer request about valid authentication data and also sends account
 * name of user that is authenticating on GameServer.
 *
 * @author -Nemesiss-, Dr2co
 */
public class SM_ACCOUNT_AUTH_RESPONSE extends GsServerPacket {

    /**
     * Account id
     */
    private final int accountId;

    /**
     * True if account is authenticated.
     */
    private final boolean ok;

    /**
     * account name
     */
    private final String accountName;

    /**
     * Access level
     */
    private final byte accessLevel;

    /**
     * Membership
     */
    private final AccountMembership membership;

    /**
     * TOLL
     */
    private final AccountToll toll;

    /**
     * Constructor.
     *
     * @param accountId
     * @param ok
     * @param accountName
     * @param accessLevel
     * @param membership
     * @param toll
     */
    public SM_ACCOUNT_AUTH_RESPONSE(int accountId, boolean ok, String accountName, byte accessLevel, AccountMembership membership, AccountToll toll) {
        this.accountId = accountId;
        this.ok = ok;
        this.accountName = accountName;
        this.accessLevel = accessLevel;
        this.membership = membership;
        this.toll = toll;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(GsConnection con) {
        writeC(1);
        writeD(accountId);
        writeC(ok ? 1 : 0);

        if (ok) {
            writeS(accountName);

            AccountTime accountTime = con.getGameServerInfo().getAccountFromGameServer(accountId).getAccountTime();

            writeQ(accountTime.getAccumulatedOnlineTime());
            writeQ(accountTime.getAccumulatedRestTime());
            writeC(accessLevel);
            writeC(membership.getMembership());
            writeQ(membership.getMembershipExpire() == null ? 0 : membership.getMembershipExpire().getTime());
            writeC(membership.getCraftship());
            writeQ(membership.getCraftshipExpire() == null ? 0 : membership.getCraftshipExpire().getTime());
            writeC(membership.getApship());
            writeQ(membership.getApshipExpire() == null ? 0 : membership.getApshipExpire().getTime());
            writeC(membership.getCollectionship());
            writeQ(membership.getCollectionshipExpire() == null ? 0 : membership.getCollectionshipExpire().getTime());
            writeQ(toll.getToll());
            writeQ(toll.getBonusToll());
        }
    }
}
