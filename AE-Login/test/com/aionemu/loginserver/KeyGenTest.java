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


package com.aionemu.loginserver;

import com.aionengine.loginserver.network.ncrypt.EncryptedRSAKeyPair;
import com.aionengine.loginserver.network.ncrypt.KeyGen;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * This test is for KeyGen initialization and performance checking
 */
public class KeyGenTest {

    /**
     * A test for keygen init
     */
    @Test
    public void testKeyGenInit() {
        try {
            KeyGen.init();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testRSAPreInit() throws Exception {
        EncryptedRSAKeyPair[] RSAKeyPairs = new EncryptedRSAKeyPair[10];

        KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");

        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);

        rsaKeyPairGenerator.initialize(spec);

        for (int i = 0; i < 10; i++) {
            RSAKeyPairs[i] = new EncryptedRSAKeyPair(rsaKeyPairGenerator.generateKeyPair());
        }

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Cipher pRsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            pRsaCipher.init(Cipher.DECRYPT_MODE, RSAKeyPairs[i].getRSAKeyPair().getPrivate());
        }
        long t2 = System.currentTimeMillis();
        System.out.println("RSA init time: " + (t2 - t1));

        byte[] data = new byte[128];

        t1 = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
            rsaCipher.init(Cipher.DECRYPT_MODE, RSAKeyPairs[0].getRSAKeyPair().getPrivate());
            rsaCipher.doFinal(data, 0, 128);
        }
        t2 = System.currentTimeMillis();
        System.out.println("RSA decryption time: " + (t2 - t1));

    }

}
