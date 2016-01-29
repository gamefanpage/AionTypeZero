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

import com.aionemu.commons.database.dao.DAOManager;
import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.controller.BannedIpController;
import com.aionengine.loginserver.dao.AccountDAO;
import com.aionengine.loginserver.dao.AccountTimeDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.AccountTime;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_BAN_RESPONSE;

import java.sql.Timestamp;

/**
 * The universal packet for account/IP bans
 *
 * @author Watson
 */
public class CM_BAN extends GsClientPacket {

    /**
     * Ban type 1 = account 2 = IP 3 = Full ban (account and IP)
     */
    private byte type;

    /**
     * Account to ban
     */
    private int accountId;

    /**
     * IP or mask to ban
     */
    private String ip;

    /**
     * Time in minutes. 0 = infinity; If time < 0 then it's unban command
     */
    private int time;

    /**
     * Object ID of Admin, who request the ban
     */
    private int adminObjId;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        this.type = (byte) readC();
        this.accountId = readD();
        this.ip = readS();
        this.time = readD();
        this.adminObjId = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        boolean result = false;

        // Ban account
        if ((type == 1 || type == 3) && accountId != 0) {
            Account account = null;

            // Find account on GameServers
            for (GameServerInfo gsi : GameServerTable.getGameServers()) {
                if (gsi.isAccountOnGameServer(accountId)) {
                    account = gsi.getAccountFromGameServer(accountId);
                    break;
                }
            }

            // 1000 is 'infinity' value
            Timestamp newTime = null;
            if (time >= 0)
                newTime = new Timestamp(time == 0 ? 1000 : System.currentTimeMillis() + time * 60000);

            if (account != null) {
                AccountTime accountTime = account.getAccountTime();
                accountTime.setPenaltyEnd(newTime);
                account.setAccountTime(accountTime);
                result = true;
            } else {
                AccountTime accountTime = DAOManager.getDAO(AccountTimeDAO.class).getAccountTime(accountId);
                accountTime.setPenaltyEnd(newTime);
                result = DAOManager.getDAO(AccountTimeDAO.class).updateAccountTime(accountId, accountTime);
            }
        }

        // Ban IP
        if (type == 2 || type == 3) {
            if (accountId != 0) // If we got account ID, then ban last IP
            {
                String newip = DAOManager.getDAO(AccountDAO.class).getLastIp(accountId);
                if (!newip.isEmpty())
                    ip = newip;
            }
            if (!ip.isEmpty()) {
                // Unban first. For banning it needs to update time
                if (BannedIpController.isBanned(ip)) {
                    // Result set for unban request
                    result = BannedIpController.unbanIp(ip);
                }
                if (time >= 0) // Ban
                {
                    Timestamp newTime = time != 0 ? new Timestamp(System.currentTimeMillis() + time * 60000) : null;
                    result = BannedIpController.banIp(ip, newTime);
                }
            }
        }

        // Now kick account
        if (accountId != 0) {
            AccountController.kickAccount(accountId);
        }

        // Respond to GS
        sendPacket(new SM_BAN_RESPONSE(type, accountId, ip, time, adminObjId, result));
    }
}
