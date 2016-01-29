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


package com.aionengine.loginserver.network.ncrypt;

import com.aionemu.commons.utils.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.spec.RSAKeyGenParameterSpec;

/**
 * Key generator. It generates keys or keyPairs for Blowfish and RSA
 *
 * @author -Nemesiss-
 */
public class KeyGen {
    /**
     * Logger for this class.
     */
    protected static final Logger log = LoggerFactory.getLogger(KeyGen.class);

    /**
     * Key generator for blowfish
     */
    private static KeyGenerator blowfishKeyGen;

    /**
     * Public/Static RSA KeyPairs with encrypted modulus N
     */
    private static EncryptedRSAKeyPair[] encryptedRSAKeyPairs;

    /**
     * Initialize Key Generator (Blowfish keygen and RSA keygen)
     *
     * @throws GeneralSecurityException
     */
    public static void init() throws GeneralSecurityException {
        log.info("Initializing Key Generator...");

        blowfishKeyGen = KeyGenerator.getInstance("Blowfish");

        KeyPairGenerator rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");

        RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(1024, RSAKeyGenParameterSpec.F4);
        rsaKeyPairGenerator.initialize(spec);
        encryptedRSAKeyPairs = new EncryptedRSAKeyPair[10];

        for (int i = 0; i < 10; i++) {
            encryptedRSAKeyPairs[i] = new EncryptedRSAKeyPair(
                    rsaKeyPairGenerator.generateKeyPair());
        }

        // Pre-init RSA cipher.. saving about 300ms
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/nopadding");
        rsaCipher.init(Cipher.DECRYPT_MODE, encryptedRSAKeyPairs[0].getRSAKeyPair().getPrivate());
    }

    /**
     * Generate and return blowfish key
     *
     * @return Random generated blowfish key
     */
    public static SecretKey generateBlowfishKey() {
        return blowfishKeyGen.generateKey();
    }

    /**
     * Get common RSA Public/Static Key Pair with encrypted modulus N
     *
     * @return encryptedRSAkeypairs
     */
    public static EncryptedRSAKeyPair getEncryptedRSAKeyPair() {
        return encryptedRSAKeyPairs[Rnd.nextInt(10)];
    }
}
