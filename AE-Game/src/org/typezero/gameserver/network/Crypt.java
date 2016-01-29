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

package org.typezero.gameserver.network;

import com.aionemu.commons.utils.Rnd;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crypt will encrypt server packet and decrypt client packet.
 *
 * @author hack99
 * @author kao
 * @author -Nemesiss-
 */
public class Crypt {

	private final static Logger log = LoggerFactory.getLogger(Crypt.class);

	/**
	 * Second byte of server packet must be equal to this
	 */
	public final static byte staticServerPacketCode = 0x44;

	/**
	 * Crypt is enabled after first server packet was send.
	 */
	private boolean isEnabled;

	private EncryptionKeyPair packetKey = null;

	/**
	 * Enable crypt key - generate random key that will be used to encrypt second server packet [first one is unencrypted]
	 * and decrypt client packets. This method is called from SM_KEY server packet, that packet sends key to aion client.
	 *
	 * @return "false key" that should by used by aion client to encrypt/decrypt packets.
	 */
	public final int enableKey() {
		if (packetKey != null)
			throw new KeyAlreadySetException();

		/** rnd key - this will be used to encrypt/decrypt packet */
		int key = Rnd.nextInt();

		packetKey = new EncryptionKeyPair(key);

		log.debug("new encrypt key: " + packetKey);

		/** false key that will be sent to aion client in SM_KEY packet */
		return (key ^ 0xCD92E4DF) + 0x3FF2CCCF;
	}

	/**
	 * Decrypt client packet from this ByteBuffer.
	 *
	 * @param buf
	 * @return true if decryption was successful.
	 */
	public final boolean decrypt(ByteBuffer buf) {
		if (!isEnabled) {
			log.debug("if encryption wasn't enabled, then maybe it's client reconnection, so skip packet");
			return true;
		}

		return packetKey.decrypt(buf);
	}

	/**
	 * Encrypt server packet from this ByteBuffer.
	 *
	 * @param buf
	 */
	public final void encrypt(ByteBuffer buf) {
		if (!isEnabled) {
			/** first packet is not encrypted */
			isEnabled = true;
			log.debug("packet is not encrypted... send in SM_KEY");
			return;
		}

		packetKey.encrypt(buf);
	}

	/**
	 * Server packet opcodec obfuscation.
	 *
	 * @param op
	 * @return obfuscated opcodec
	 */
	public static final int encodeOpcodec(int op) {
		return (op + 0xCF) ^ 0xDF;
	}
}
