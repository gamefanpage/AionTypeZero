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

package com.aionengine.loginserver.network.aion.clientpackets;

import com.aionengine.loginserver.GameServerTable;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.network.aion.AionAuthResponse;
import com.aionengine.loginserver.network.aion.AionClientPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;
import com.aionengine.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;

import java.nio.ByteBuffer;

/**
 * @author -Nemesiss-
 */
public class CM_SERVER_LIST extends AionClientPacket {

    /**
     * accountId is part of session key - its used for security purposes
     */
    private int accountId;
    /**
     * loginOk is part of session key - its used for security purposes
     */
    private int loginOk;

    /**
     * Constructs new instance of <tt>CM_SERVER_LIST </tt> packet.
     *
     * @param buf
     * @param client
     */
    public CM_SERVER_LIST(ByteBuffer buf, LoginConnection client) {
        super(buf, client, 0x05);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        accountId = readD();
        loginOk = readD();
        readD();// unk
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        LoginConnection con = getConnection();
        if (con.getSessionKey().checkLogin(accountId, loginOk)) {
            if (GameServerTable.getGameServers().size() == 0)
                con.close(new SM_LOGIN_FAIL(AionAuthResponse.NO_GS_REGISTERED), false);
            else
                AccountController.loadGSCharactersCount(accountId);
        } else {
            /**
             * Session key is not ok - inform client that smth went wrong - dc client
             */
            con.close(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR), false);
        }
    }
}
