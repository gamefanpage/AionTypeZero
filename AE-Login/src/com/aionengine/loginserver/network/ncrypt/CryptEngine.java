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

/**
 * Crypto engine for ecnrypting/decrypting packets, error handling and verifying
 * checksum
 *
 * @author EvilSpirit
 */
public class CryptEngine {
    /**
     * A key
     */
    private byte[] key = {(byte) 0x6b, (byte) 0x60, (byte) 0xcb, (byte) 0x5b,
            (byte) 0x82, (byte) 0xce, (byte) 0x90, (byte) 0xb1, (byte) 0xcc,
            (byte) 0x2b, (byte) 0x6c, (byte) 0x55, (byte) 0x6c, (byte) 0x6c,
            (byte) 0x6c, (byte) 0x6c};
    /**
     * Tells you whether the key is updated or not
     */
    private boolean updatedKey = false;
    /**
     * A secret blowfish cipher
     */
    private BlowfishCipher cipher;

    /**
     * Default constructor. Initialize the Blowfish Cipher with an initial
     * static key to encrypt the first packet sent to the client
     */
    public CryptEngine() {
        cipher = new BlowfishCipher(key);
    }

    /**
     * Update the key for packet encryption/decryption with the Blowfish Cipher
     *
     * @param newKey new Blowfish Key
     */
    public void updateKey(byte[] newKey) {
        this.key = newKey;
    }

    /**
     * Decrypt given data
     *
     * @param data   byte array to be decrypted
     * @param offset byte array offset
     * @param length byte array length
     * @return true, if decrypted packet has valid checksum, false overwise
     */
    public boolean decrypt(byte[] data, int offset, int length) {
        cipher.decipher(data, offset, length);

        return verifyChecksum(data, offset, length);
    }

    /**
     * Encrypt given data
     *
     * @param data   byte array to be encrypted
     * @param offset byte array offset
     * @param length byte array length
     * @return length of encrypted byte array
     */
    public int encrypt(byte[] data, int offset, int length) {
        length += 4;

        // the key is not updated, so the first packet should be encrypted with
        // initial key
        if (!updatedKey) {
            length += 4;
            length += 8 - length % 8;
            encXORPass(data, offset, length, Rnd.nextInt());
            cipher.cipher(data, offset, length);
            cipher.updateKey(key);
            updatedKey = true;
        } else {
            length += 8 - length % 8;
            appendChecksum(data, offset, length);
            cipher.cipher(data, offset, length);
        }

        return length;
    }

    /**
     * Verify checksum in a packet
     *
     * @param data   byte array - encrypted packet
     * @param offset byte array offset
     * @param length byte array size
     * @return true, if checksum is ok, false overwise
     */
    private boolean verifyChecksum(byte[] data, int offset, int length) {
        if ((length & 3) != 0 || (length <= 4)) {
            return false;
        }

        long chksum = 0;
        int count = length - 4;
        long check;
        int i;

        for (i = offset; i < count; i += 4) {
            check = data[i] & 0xff;
            check |= data[i + 1] << 8 & 0xff00;
            check |= data[i + 2] << 0x10 & 0xff0000;
            check |= data[i + 3] << 0x18 & 0xff000000;
            chksum ^= check;
        }

        check = data[i] & 0xff;
        check |= data[i + 1] << 8 & 0xff00;
        check |= data[i + 2] << 0x10 & 0xff0000;
        check |= data[i + 3] << 0x18 & 0xff000000;
        check = data[i] & 0xff;
        check |= data[i + 1] << 8 & 0xff00;
        check |= data[i + 2] << 0x10 & 0xff0000;
        check |= data[i + 3] << 0x18 & 0xff000000;

        return 0 == chksum;
    }

    /**
     * add checksum to the end of the packet
     *
     * @param raw    byte array - encrypted packet
     * @param offset byte array offset
     * @param length byte array size
     */
    private void appendChecksum(byte[] raw, int offset, int length) {
        long chksum = 0;
        int count = length - 4;
        long ecx;
        int i;

        for (i = offset; i < count; i += 4) {
            ecx = raw[i] & 0xff;
            ecx |= raw[i + 1] << 8 & 0xff00;
            ecx |= raw[i + 2] << 0x10 & 0xff0000;
            ecx |= raw[i + 3] << 0x18 & 0xff000000;
            chksum ^= ecx;
        }

        ecx = raw[i] & 0xff;
        ecx |= raw[i + 1] << 8 & 0xff00;
        ecx |= raw[i + 2] << 0x10 & 0xff0000;
        ecx |= raw[i + 3] << 0x18 & 0xff000000;
        raw[i] = (byte) (chksum & 0xff);
        raw[i + 1] = (byte) (chksum >> 0x08 & 0xff);
        raw[i + 2] = (byte) (chksum >> 0x10 & 0xff);
        raw[i + 3] = (byte) (chksum >> 0x18 & 0xff);
    }

    /**
     * First packet encryption with XOR key (integer - 4 bytes)
     *
     * @param data   byte array to be encrypted
     * @param offset byte array offset
     * @param length byte array length
     * @param key    integer value as key
     */
    private void encXORPass(byte[] data, int offset, int length, int key) {
        int stop = length - 8;
        int pos = 4 + offset;
        int edx;
        int ecx = key;

        while (pos < stop) {
            edx = (data[pos] & 0xFF);
            edx |= (data[pos + 1] & 0xFF) << 8;
            edx |= (data[pos + 2] & 0xFF) << 16;
            edx |= (data[pos + 3] & 0xFF) << 24;
            ecx += edx;
            edx ^= ecx;
            data[pos++] = (byte) (edx & 0xFF);
            data[pos++] = (byte) (edx >> 8 & 0xFF);
            data[pos++] = (byte) (edx >> 16 & 0xFF);
            data[pos++] = (byte) (edx >> 24 & 0xFF);
        }

        data[pos++] = (byte) (ecx & 0xFF);
        data[pos++] = (byte) (ecx >> 8 & 0xFF);
        data[pos++] = (byte) (ecx >> 16 & 0xFF);
        data[pos] = (byte) (ecx >> 24 & 0xFF);
    }
}
