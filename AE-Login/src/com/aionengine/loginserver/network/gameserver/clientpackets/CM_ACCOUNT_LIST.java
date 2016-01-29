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

import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;

/**
 * Reads the list of accoutn id's that are logged to game server
 *
 * @author SoulKeeper
 */
public class CM_ACCOUNT_LIST extends GsClientPacket {

    /**
     * Array with accounts that are logged in
     */
    private String[] accountNames;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        accountNames = new String[readD()];
        for (int i = 0; i < accountNames.length; i++) {
            accountNames[i] = readS();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        for (String s : accountNames) {
            Account a = AccountController.loadAccount(s);
            if (GameServerTable.isAccountOnAnyGameServer(a)) {
                this.getConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(a.getId()));
                continue;
            }
            getConnection().getGameServerInfo().addAccountToGameServer(a);
        }
    }
}
