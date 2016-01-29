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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseClientPacket extends AbstractPacket {

	private static final Logger log = LoggerFactory.getLogger(BaseClientPacket.class);
	private ChannelBuffer buf;

	/**
	 * @param channelBuffer
	 * @param opCode
	 */
	public BaseClientPacket(ChannelBuffer channelBuffer, int opCode) {
		super(opCode);
		this.buf = channelBuffer;
	}

	public int getRemainingBytes() {
		return buf.readableBytes();
	}

	/**
	 * Perform packet read
	 *
	 * @return boolean
	 */
	public boolean read() {
		try {
			readImpl();
			if (getRemainingBytes() > 0) {
				log.debug("Packet " + this + " not fully readed!");
			}
			return true;
		} catch (Exception ex) {
			log.error("Reading failed for packet " + this, ex);
			return false;
		}

	}

	/**
	 * Perform packet action
	 */
	public void run() {
		try {
			runImpl();
		} catch (Exception ex) {
			log.error("Running failed for packet " + this, ex);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl();

	/**
	 * Read int from this packet buffer.
	 *
	 * @return int
	 */
	protected final int readD() {
		try {
			return buf.readInt();
		} catch (Exception e) {
			log.error("Missing D for: " + this);
		}
		return 0;
	}

	/**
	 * Read byte from this packet buffer.
	 *
	 * @return int
	 */
	protected final int readC() {
		try {
			return buf.readByte() & 0xFF;
		} catch (Exception e) {
			log.error("Missing C for: " + this);
		}
		return 0;
	}

	/**
	 * Read short from this packet buffer.
	 *
	 * @return int
	 */
	protected final int readH() {
		try {
			return buf.readShort() & 0xFFFF;
		} catch (Exception e) {
			log.error("Missing H for: " + this);
		}
		return 0;
	}

	/**
	 * Read double from this packet buffer.
	 *
	 * @return double
	 */
	protected final double readDF() {
		try {
			return buf.readDouble();
		} catch (Exception e) {
			log.error("Missing DF for: " + this);
		}
		return 0;
	}

	/**
	 * Read double from this packet buffer.
	 *
	 * @return double
	 */
	protected final float readF() {
		try {
			return buf.readFloat();
		} catch (Exception e) {
			log.error("Missing F for: " + this);
		}
		return 0;
	}

	/**
	 * Read long from this packet buffer.
	 *
	 * @return long
	 */
	protected final long readQ() {
		try {
			return buf.readLong();
		} catch (Exception e) {
			log.error("Missing Q for: " + this);
		}
		return 0;
	}

	/**
	 * Read String from this packet buffer.
	 *
	 * @return String
	 */
	protected final String readS() {
		StringBuffer sb = new StringBuffer();
		char ch;
		try {
			while ((ch = buf.readChar()) != 0) {
				sb.append(ch);
			}
		} catch (Exception e) {
			log.error("Missing S for: " + this);
		}
		return sb.toString();

	}

	/**
	 * Read n bytes from this packet buffer, n = length.
	 *
	 * @param length
	 * @return byte[]
	 */
	protected final byte[] readB(int length) {
		byte[] result = new byte[length];
		try {
			buf.readBytes(result);
		} catch (Exception e) {
			log.error("Missing byte[] for: " + this);
		}
		return result;
	}
}
