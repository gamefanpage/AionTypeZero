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

import com.aionengine.loginserver.network.gameserver.GsClientPacket;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.clientpackets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author -Nemesiss-
 */
public class GsPacketHandlerFactory {

    /**
     * logger for this class
     */
    private static final Logger log = LoggerFactory.getLogger(GsPacketHandlerFactory.class);

    /**
     * Reads one packet from given ByteBuffer
     *
     * @param data
     * @param client
     * @return GsClientPacket object from binary data
     */
    public static GsClientPacket handle(ByteBuffer data, GsConnection client) {
        GsClientPacket msg = null;
        GsConnection.State state = client.getState();
        int id = data.get() & 0xff;

        switch (state) {
            case CONNECTED: {
                switch (id) {
                    case 0:
                        msg = new CM_GS_AUTH();
                        break;
                    case 13:
                        msg = new CM_MAC();
                        break;
                    default:
                        unknownPacket(state, id);
                }
                break;
            }
            case AUTHED: {
                switch (id) {
                    case 1:
                        msg = new CM_ACCOUNT_AUTH();
                        break;
                    case 2:
                        msg = new CM_ACCOUNT_RECONNECT_KEY();
                        break;
                    case 3:
                        msg = new CM_ACCOUNT_DISCONNECTED();
                        break;
                    case 4:
                        msg = new CM_ACCOUNT_LIST();
                        break;
                    case 5:
                        msg = new CM_LS_CONTROL();
                        break;
                    case 6:
                        msg = new CM_BAN();
                        break;
                    case 8:
                        msg = new CM_GS_CHARACTER();
                        break;
                    case 9:
                        msg = new CM_ACCOUNT_TOLL_INFO();
                        break;
                    case 10:
                        msg = new CM_MACBAN_CONTROL();
                        break;
                    case 11:
                        msg = new CM_PREMIUM_CONTROL();
                        break;
                    case 12:
                        msg = new CM_GS_PONG();
                        break;
                    case 13:
                        msg = new CM_MAC();
                        break;
                    case 14:
                        msg = new CM_PTRANSFER_CONTROL();
                        break;
                    default:
                        unknownPacket(state, id);
                }
                break;
            }
        }

        if (msg != null) {
            msg.setConnection(client);
            msg.setBuffer(data);
        }

        return msg;
    }

    /**
     * Logs unknown packet.
     *
     * @param state
     * @param id
     */
    private static void unknownPacket(GsConnection.State state, int id) {
        log.warn(String.format("Unknown packet recived from Game Server: 0x%02X state=%s", id, state.toString()));
    }
}
