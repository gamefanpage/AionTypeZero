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

/**
 * Basic superclass for packets.
 * <p/>
 * Created on: 29.06.2009 17:59:25
 *
 * @author Aquanox
 */
public abstract class BasePacket {

	/**
	 * Default packet string representation pattern.
	 *
	 * @see java.util.Formatter
	 * @see String#format(String, Object[])
	 */
	public static final String TYPE_PATTERN = "[%s] 0x%02X %s";

	/**
	 * Packet type field.
	 */
	private final PacketType packetType;

	/**
	 * Packet opcode field
	 */
	private int opcode;

	/**
	 * Constructs a new packet with specified type and id.
	 *
	 * @param packetType Type of packet
	 * @param opcode     Id of packet
	 */
	protected BasePacket(PacketType packetType, int opcode) {
		this.packetType = packetType;
		this.opcode = opcode;
	}

	/**
	 * Constructs a new packet with given type.<br>
	 * If this constructor is used, then setOpcode() must be used just after it.
	 *
	 * @param packetType
	 */
	protected BasePacket(PacketType packetType) {
		this.packetType = packetType;
	}

	/**
	 * Sets opcode of this packet.<br>
	 * <font color='red'>NOTICE: </font> Use only if BasePacket(PacketType) constructor was use
	 *
	 * @param opcode
	 */
	protected void setOpcode(int opcode) {
		this.opcode = opcode;
	}

	/**
	 * Returns packet opcode.
	 *
	 * @return packet id
	 */
	public final int getOpcode() {
		return opcode;
	}

	/**
	 * Returns packet type.
	 *
	 * @return type of this packet.
	 * @see com.aionemu.commons.network.packet.BasePacket.PacketType
	 */
	public final PacketType getPacketType() {
		return packetType;
	}

	/**
	 * Returns packet name.
	 * <p/>
	 * Actually packet name is a simple name of the underlying class.
	 *
	 * @return packet name
	 * @see Class#getSimpleName()
	 */
	public String getPacketName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Enumeration of packet types.
	 */
	public static enum PacketType {
		/**
		 * Server packet
		 */
		SERVER("S"),

		/**
		 * Client packet
		 */
		CLIENT("C");

		/**
		 * String representing packet type.
		 */
		private final String name;

		/**
		 * Constructor.
		 *
		 * @param name
		 */
		private PacketType(String name) {
			this.name = name;
		}

		/**
		 * Returns packet type name.
		 *
		 * @return packet type name.
		 */
		public String getName() {
			return name;
		}
	}

	/**
	 * Returns string representation of this packet based on packet type, opcode and name.
	 *
	 * @return packet type string
	 * @see #TYPE_PATTERN
	 * @see java.util.Formatter
	 * @see String#format(String, Object[])
	 */
	@Override
	public String toString() {
		return String.format(TYPE_PATTERN, getPacketType().getName(), getOpcode(), getPacketName());
	}
}
