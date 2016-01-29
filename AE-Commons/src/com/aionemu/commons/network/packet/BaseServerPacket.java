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

package com.aionemu.commons.network.packet;

import java.nio.ByteBuffer;


/**
 * Base class for every Server Packet
 *
 * @author -Nemesiss-
 */
public abstract class BaseServerPacket extends BasePacket {

	/**
	 * ByteBuffer that contains this packet data
	 */
	public ByteBuffer buf;

	/**
	 * Constructs a new server packet with specified id.
	 *
	 * @param opcode packet opcode.
	 */
	protected BaseServerPacket(int opcode) {
		super(PacketType.SERVER, opcode);
	}

	/**
	 * Constructs a new server packet.<br>
	 * If this constructor was used, then {@link #setOpcode(int)} must be called
	 */
	protected BaseServerPacket() {
		super(PacketType.SERVER);
	}

	/**
	 * @param buf the buf to set
	 */
	public void setBuf(ByteBuffer buf) {
		this.buf = buf;
	}

	/**
	 * Write int to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeD(int value) {
		buf.putInt(value);
	}

	/**
	 * Write short to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeH(int value) {
		buf.putShort((short) value);
	}

	/**
	 * Write byte to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeC(int value) {
		buf.put((byte) value);
	}

	/**
	 * Write double to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeDF(double value) {
		buf.putDouble(value);
	}

	/**
	 * Write float to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeF(float value) {
		buf.putFloat(value);
	}

	/**
	 * Write long to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeQ(long value) {
		buf.putLong(value);
	}

	/**
	 * Write String to buffer
	 *
	 * @param buf
	 * @param text
	 */
	protected final void writeS(String text) {
		if (text == null) {
			buf.putChar('\000');
		} else {
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
	protected final void writeB(byte[] data) {
		buf.put(data);
	}
}
