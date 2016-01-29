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
 */

package org.typezero.chatserver.common.netty;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author ATracer
 */
public abstract class BaseServerPacket extends AbstractPacket {

	/**
	 * @param opCode
	 */
	public BaseServerPacket(int opCode) {
		super(opCode);
	}

	/**
	 * @param buf
	 * @param value
	 */
	protected final void writeD(ChannelBuffer buf, int value) {
		buf.writeInt(value);
	}

	/**
	 * @param buf
	 * @param value
	 */
	protected final void writeH(ChannelBuffer buf, int value) {
		buf.writeShort((short) value);
	}

	/**
	 * @param buf
	 * @param value
	 */
	protected final void writeC(ChannelBuffer buf, int value) {
		buf.writeByte((byte) value);
	}

	/**
	 * Write double to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeDF(ChannelBuffer buf, double value) {
		buf.writeDouble(value);
	}

	/**
	 * Write float to buffer.
	 *
	 * @param buf
	 * @param value
	 */
	protected final void writeF(ChannelBuffer buf, float value) {
		buf.writeFloat(value);
	}

	/**
	 * @param buf
	 * @param data
	 */
	protected final void writeB(ChannelBuffer buf, byte[] data) {
		buf.writeBytes(data);
	}

	/**
	 * Write String to buffer
	 *
	 * @param buf
	 * @param text
	 */
	protected final void writeS(ChannelBuffer buf, String text) {
		if (text == null) {
			buf.writeChar('\000');
		} else {
			final int len = text.length();
			for (int i = 0; i < len; i++) {
				buf.writeChar(text.charAt(i));
			}
			buf.writeChar('\000');
		}
	}

	/**
	 * @param buf
	 * @param data
	 */
	protected final void writeQ(ChannelBuffer buf, long data) {
		buf.writeLong(data);
	}
}
