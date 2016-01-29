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

package com.aionengine.loginserver.network.aion;

import com.aionemu.commons.network.packet.BaseServerPacket;

import java.nio.ByteBuffer;

/**
 * Base class for every LS -> Aion Server Packet.
 *
 * @author -Nemesiss-
 */
public abstract class AionServerPacket extends BaseServerPacket {
    /**
     * Constructs a new server packet with specified id.
     *
     * @param opcode packet opcode.
     */
    protected AionServerPacket(int opcode) {
        super(opcode);
    }

    /**
     * Write and encrypt this packet data for given connection, to given buffer.
     *
     * @param con
     * @param buf
     */
    public final void write(LoginConnection con) {
        buf.putShort((short) 0);
        buf.put((byte) getOpcode());
        writeImpl(con);
        buf.flip();
        buf.putShort((short) 0);
        ByteBuffer b = buf.slice();

        short size = (short) (con.encrypt(b) + 2);
        buf.putShort(0, size);
        buf.position(0).limit(size);
    }

    /**
     * Write data that this packet represents to given byte buffer.
     *
     * @param con
     * @param buf
     */
    protected abstract void writeImpl(LoginConnection con);
}
