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

import com.aionengine.loginserver.configs.Config;
import com.aionengine.loginserver.controller.AccountController;
import com.aionengine.loginserver.controller.BannedIpController;
import com.aionengine.loginserver.network.aion.AionAuthResponse;
import com.aionengine.loginserver.network.aion.AionClientPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;
import com.aionengine.loginserver.network.aion.LoginConnection.State;
import com.aionengine.loginserver.network.aion.SessionKey;
import com.aionengine.loginserver.network.aion.serverpackets.SM_LOGIN_FAIL;
import com.aionengine.loginserver.network.aion.serverpackets.SM_LOGIN_OK;
import com.aionengine.loginserver.utils.BruteForceProtector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;

/**
 * @author -Nemesiss-, KID, Lyahim
 */
public class CM_LOGIN extends AionClientPacket {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(CM_LOGIN.class);

    /**
     * byte array contains encrypted login and password.
     */
    private byte[] data;

    /**
     * Constructs new instance of <tt>CM_LOGIN </tt> packet.
     *
     * @param buf
     * @param client
     */
    public CM_LOGIN(ByteBuffer buf, LoginConnection client) {
        super(buf, client, 0x0b);
    }

    @Override
    protected void readImpl() {
        readD();
        if (getRemainingBytes() >= 128) {
            data = readB(128);
        }
    }

    @Override
    protected void runImpl() {
        if (data == null)
            return;

        byte[] decrypted;
        try {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, getConnection().getRSAPrivateKey());
            decrypted = rsaCipher.doFinal(data, 0, 128);
        } catch (GeneralSecurityException e) {
            sendPacket(new SM_LOGIN_FAIL(AionAuthResponse.SYSTEM_ERROR));
            return;
        }
        String user = new String(decrypted, 64, 32).trim().toLowerCase();
        String password = new String(decrypted, 96, 32).trim();

        @SuppressWarnings("unused")
        int ncotp = decrypted[0x7c];
        ncotp |= decrypted[0x7d] << 8;
        ncotp |= decrypted[0x7e] << 16;
        ncotp |= decrypted[0x7f] << 24;

        LoginConnection client = getConnection();
        AionAuthResponse response = AccountController.login(user, password, client);
        switch (response) {
            case AUTHED:
                client.setState(State.AUTHED_LOGIN);
                client.setSessionKey(new SessionKey(client.getAccount()));
                client.sendPacket(new SM_LOGIN_OK(client.getSessionKey()));
                log.debug("" + user + " got authed state");
                break;
            case INVALID_PASSWORD:
                if (Config.ENABLE_BRUTEFORCE_PROTECTION) {
                    String ip = client.getIP();
                    if (BruteForceProtector.getInstance().addFailedConnect(ip)) {
                        Timestamp newTime = new Timestamp(System.currentTimeMillis() + Config.WRONG_LOGIN_BAN_TIME * 60000);
                        BannedIpController.banIp(ip, newTime);
                        log.debug(user + " on " + ip + " banned for " + Config.WRONG_LOGIN_BAN_TIME + " min. bruteforce");
                        client.close(new SM_LOGIN_FAIL(AionAuthResponse.BAN_IP), false);
                    } else {
                        log.debug(user + " got invalid password attemp state");
                        client.sendPacket(new SM_LOGIN_FAIL(response));
                    }
                } else {
                    log.debug(user + " got invalid password attemp state");
                    client.sendPacket(new SM_LOGIN_FAIL(response));
                }
                break;
            default:
                log.debug(user + " got unknown (" + response.toString() + ") attemp state");
                client.close(new SM_LOGIN_FAIL(response), false);
                break;
        }
    }
}
