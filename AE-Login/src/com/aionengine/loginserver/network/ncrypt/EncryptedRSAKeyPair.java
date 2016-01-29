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

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;

/**
 * This class is for storing standard RSA Public/Static keyPairs The main
 * difference that N (Modulus) is encrypted to be transfered on the net with
 * simple scrambling algorythm. So public pair (e, n) , where e is exponent
 * (usually static 3 or 65537) and n is modulus, is encrypted and cannot be
 * applied to cipher some data without deciphering the modulus.
 *
 * @author EvilSpirit
 */
public class EncryptedRSAKeyPair {
    /**
     * KeyPair
     */
    private KeyPair RSAKeyPair;
    /**
     * Byte
     */
    private byte[] encryptedModulus;

    /**
     * Default constructor. Stores RSA key pair and encrypts rsa modulus N
     *
     * @param RSAKeyPair standard RSA KeyPair generated with standard KeyPairGenerator
     *                   {@link java.security.KeyPairGenerator}
     */
    public EncryptedRSAKeyPair(KeyPair RSAKeyPair) {
        this.RSAKeyPair = RSAKeyPair;
        encryptedModulus = encryptModulus(((RSAPublicKey) this.RSAKeyPair.getPublic()).getModulus());
    }

    /**
     * Encrypt RSA modulus N
     *
     * @param modulus RSA modulus from public/private pairs (e,n), (d,n)
     * @return encrypted modulus
     */
    private byte[] encryptModulus(BigInteger modulus) {
        byte[] encryptedModulus = modulus.toByteArray();

        if ((encryptedModulus.length == 0x81) && (encryptedModulus[0] == 0x00)) {
            byte[] temp = new byte[0x80];

            System.arraycopy(encryptedModulus, 1, temp, 0, 0x80);

            encryptedModulus = temp;
        }

        for (int i = 0; i < 4; i++) {
            byte temp = encryptedModulus[i];

            encryptedModulus[i] = encryptedModulus[0x4d + i];
            encryptedModulus[0x4d + i] = temp;
        }

        for (int i = 0; i < 0x40; i++) {
            encryptedModulus[i] = (byte) (encryptedModulus[i] ^ encryptedModulus[0x40 + i]);
        }

        for (int i = 0; i < 4; i++) {
            encryptedModulus[0x0d + i] = (byte) (encryptedModulus[0x0d + i] ^ encryptedModulus[0x34 + i]);
        }

        for (int i = 0; i < 0x40; i++) {
            encryptedModulus[0x40 + i] = (byte) (encryptedModulus[0x40 + i] ^ encryptedModulus[i]);
        }

        return encryptedModulus;
    }

    /**
     * Get default RSA key pair
     *
     * @return RSAKeyPair
     */
    public KeyPair getRSAKeyPair() {
        return RSAKeyPair;
    }

    /**
     * Get encrypted modulus to be transferred on the net.
     *
     * @return encryptedModulus
     */
    public byte[] getEncryptedModulus() {
        return encryptedModulus;
    }
}
