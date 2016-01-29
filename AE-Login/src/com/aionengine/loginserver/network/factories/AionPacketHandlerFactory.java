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

package com.aionengine.loginserver.network.factories;

import com.aionengine.loginserver.network.aion.AionClientPacket;
import com.aionengine.loginserver.network.aion.LoginConnection;
import com.aionengine.loginserver.network.aion.LoginConnection.State;
import com.aionengine.loginserver.network.aion.clientpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author -Nemesiss-
 */
public class AionPacketHandlerFactory {

    /**
     * logger for this class
     */
    private static final Logger log = LoggerFactory.getLogger(AionPacketHandlerFactory.class);

    /**
     * Reads one packet from given ByteBuffer
     *
     * @param data
     * @param client
     * @return AionClientPacket object from binary data
     */
    public static AionClientPacket handle(ByteBuffer data, LoginConnection client) {
        AionClientPacket msg = null;
        State state = client.getState();
        int id = data.get() & 0xff;

        switch (state) {
            case CONNECTED: {
                switch (id) {
                    case 0x07:
                        msg = new CM_AUTH_GG(data, client);
                        break;
                    case 0x08:
                        msg = new CM_UPDATE_SESSION(data, client);
                        break;
                    default:
                        unknownPacket(state, id);
                }
                break;
            }
            case AUTHED_GG: {
                switch (id) {
                    case 0x0B:
                        msg = new CM_LOGIN(data, client);
                        break;
                    default:
                        unknownPacket(state, id);
                }
                break;
            }
            case AUTHED_LOGIN: {
                switch (id) {
                    case 0x05:
                        msg = new CM_SERVER_LIST(data, client);
                        break;
                    case 0x02:
                        msg = new CM_PLAY(data, client);
                        break;
                    default:
                        unknownPacket(state, id);
                }
                break;
            }
        }

        return msg;
    }

    /**
     * Logs unknown packet.
     *
     * @param state
     * @param id
     */
    private static void unknownPacket(State state, int id) {
        log.warn(String.format("Unknown packet recived from Aion client: 0x%02X state=%s", id, state.toString()));
    }
}
