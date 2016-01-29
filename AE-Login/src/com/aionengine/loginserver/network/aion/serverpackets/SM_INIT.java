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

import com.aionengine.loginserver.network.aion.AionServerPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;

import javax.crypto.SecretKey;

/**
 * Format: dd b dddd s d: session id d: protocol revision b: 0x90 bytes : 0x80 bytes for the scrambled RSA public key
 * 0x10 bytes at 0x00 d: unknow d: unknow d: unknow d: unknow s: blowfish key
 */
public final class SM_INIT extends AionServerPacket {

    /**
     * Session Id of this connection
     */
    private final int sessionId;

    /**
     * public Rsa key that client will use to encrypt login and password that will be send in RequestAuthLogin client
     * packet.
     */
    private final byte[] publicRsaKey;
    /**
     * blowfish key for packet encryption/decryption.
     */
    private final byte[] blowfishKey;

    /**
     * Constructor
     *
     * @param client
     * @param blowfishKey
     */
    public SM_INIT(LoginConnection client, SecretKey blowfishKey) {
        this(client.getEncryptedModulus(), blowfishKey.getEncoded(), client.getSessionId());
    }

    /**
     * Creates new instance of <tt>SM_INIT</tt> packet.
     *
     * @param publicRsaKey Public RSA key
     * @param blowfishKey  Blowfish key
     * @param sessionId    Session identifier
     */
    private SM_INIT(byte[] publicRsaKey, byte[] blowfishKey, int sessionId) {
        super(0x00);
        this.sessionId = sessionId;
        this.publicRsaKey = publicRsaKey;
        this.blowfishKey = blowfishKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(LoginConnection con) {
        writeD(sessionId); // session id
        writeD(0x0000c621); // protocol revision
        writeB(publicRsaKey); // RSA Public Key
        // unk
        writeD(0x00);
        writeD(0x00);
        writeD(0x00);
        writeD(0x00);

        writeB(blowfishKey); // BlowFish key
        writeD(197635); // unk
        writeD(2097152); // unk
    }
}
