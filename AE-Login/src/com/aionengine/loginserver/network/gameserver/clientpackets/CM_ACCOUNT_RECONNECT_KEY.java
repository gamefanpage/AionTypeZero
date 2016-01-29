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


import com.aionemu.commons.utils.Rnd;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.ReconnectingAccount;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_ACCOUNT_RECONNECT_KEY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This packet is sended by GameServer when player is requesting fast reconnect to login server. LoginServer in response
 * will send reconectKey.
 *
 * @author -Nemesiss-
 */
public class CM_ACCOUNT_RECONNECT_KEY extends GsClientPacket {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(CM_ACCOUNT_RECONNECT_KEY.class);
    /**
     * accoundId of account that will be reconnecting.
     */
    private int accountId;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        accountId = readD();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        int reconectKey = Rnd.nextInt();
        Account acc = this.getConnection().getGameServerInfo().removeAccountFromGameServer(accountId);
        if (acc == null)
            log.info("This shouldnt happend! [Error]");
        else
            AccountController.addReconnectingAccount(new ReconnectingAccount(acc, reconectKey));
        sendPacket(new SM_ACCOUNT_RECONNECT_KEY(accountId, reconectKey));
    }
}
