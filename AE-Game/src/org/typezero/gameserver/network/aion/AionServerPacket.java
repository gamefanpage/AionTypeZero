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

package org.typezero.gameserver.network.aion;

import java.nio.ByteBuffer;

import com.aionemu.commons.network.packet.BaseServerPacket;
import org.typezero.gameserver.network.Crypt;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * Base class for every GS -> Aion Server Packet.
 *
 * @author -Nemesiss-
 */
public abstract class AionServerPacket extends BaseServerPacket {
	/**
	 * Constructs new server packet
	 */
	protected AionServerPacket() {
		super();
		setOpcode(ServerPacketsOpcodes.getOpcode(getClass()));
	}

	/**
	 * Write packet opcodec and two additional bytes
	 *
	 * @param buf
	 * @param value
	 */
	private final void writeOP(int value) {
		/** obfuscate packet id */
		int op = Crypt.encodeOpcodec(value);
		buf.putShort((short)(op));
		/** put static server packet code */
		buf.put(Crypt.staticServerPacketCode);

		/** for checksum? */
		buf.putShort((short) (~op));
	}

	public final void write(AionConnection con) {
		write(con, buf);
	}

	/**
	 * Write and encrypt this packet data for given connection, to given buffer.
	 *
	 * @param con
	 * @param buf
	 */
	public final void write(AionConnection con, ByteBuffer buffer) {
		if (con.getState().equals(AionConnection.State.IN_GAME) && con.getActivePlayer().getPlayerAccount().getMembership() == 10) {
			if (!this.getPacketName().equals("SM_MESSAGE")) {
				PacketSendUtility.sendMessage(con.getActivePlayer(), "0x" + Integer.toHexString(this.getOpcode()).toUpperCase() + " : " + this.getPacketName());
			}
		}

		this.setBuf(buffer);
		buf.putShort((short) 0);
		writeOP(getOpcode());
		writeImpl(con);
		buf.flip();
		buf.putShort((short) buf.limit());
		ByteBuffer b = buf.slice();
		buf.position(0);
		con.encrypt(b);
	}

	/**
	 * Write data that this packet represents to given byte buffer.
	 *
	 * @param con
	 * @param buf
	 */
	protected void writeImpl(AionConnection con) {

	}

	public final ByteBuffer getBuf()
	{
		return this.buf;
	}

	/**
	 * Write String to buffer
	 *
	 * @param text
	 * @param size
	 */
	protected final void writeS(String text, int size) {
		if (text == null) {
			buf.put(new byte[size]);
		}
		else {
			final int len = text.length();
			for (int i = 0; i < len; i++)
				buf.putChar(text.charAt(i));
			buf.put(new byte[size-(len*2)]);
		}
	}

	protected void writeNameId(int nameId)
	{
		writeH(0x24);
		writeD(nameId);
		writeH(0x00);
	}
}
