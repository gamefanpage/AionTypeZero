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

import java.nio.ByteBuffer;


/**
 * @author -Nemesiss-
 *
 */
public abstract class PacketWriteHelper {

	protected abstract void writeMe(ByteBuffer buf);

	/**
	 * Write int to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeD(ByteBuffer buf, int value) {
		buf.putInt(value);
	}

	/**
	 * Write short to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeH(ByteBuffer buf, int value) {
		buf.putShort((short) value);
	}

	/**
	 * Write byte to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeC(ByteBuffer buf, int value) {
		buf.put((byte) value);
	}

	/**
	 * Write double to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeDF(ByteBuffer buf, double value) {
		buf.putDouble(value);
	}

	/**
	 * Write float to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeF(ByteBuffer buf, float value) {
		buf.putFloat(value);
	}

	/**
	 * Write long to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeQ(ByteBuffer buf, long value) {
		buf.putLong(value);
	}

	/**
	 * Write String to buffer
	 *
	 * @param buf
	 * @param text
	 */
	protected final void writeS(ByteBuffer buf, String text) {
		if (text == null) {
			buf.putChar('\000');
		}
		else {
			final int len = text.length();
			for (int i = 0; i < len; i++)
				buf.putChar(text.charAt(i));
			buf.putChar('\000');
		}
	}

	/**
	 * Write byte array to buffer.
	 *
	 * @param buf
	 * @param data
	 */
	protected final void writeB(ByteBuffer buf, byte[] data) {
		buf.put(data);
	}

	/**
	 * Skip specified amount of bytes
	 *
	 * @param buf
	 * @param bytes
	 */
	protected final void skip(ByteBuffer buf, int bytes)
	{
		buf.put(new byte[bytes]);
	}
}
