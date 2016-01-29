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

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.ConnectionFactory;
import com.aionemu.commons.network.Dispatcher;
import com.aionengine.loginserver.configs.Config;
import com.aionengine.loginserver.utils.FloodProtector;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * ConnectionFactory implementation that will be creating AionConnections
 *
 * @author -Nemesiss-
 */
public class AionConnectionFactoryImpl implements ConnectionFactory {

    /**
     * Create a new {@link com.aionemu.commons.network.AConnection AConnection} instance.<br>
     *
     * @param socket     that new {@link com.aionemu.commons.network.AConnection AConnection} instance will represent.<br>
     * @param dispatcher to witch new connection will be registered.<br>
     * @return a new instance of {@link com.aionemu.commons.network.AConnection AConnection}<br>
     * @throws IOException
     * @see com.aionemu.commons.network.AConnection
     * @see com.aionemu.commons.network.Dispatcher
     */
    @Override
    public AConnection create(SocketChannel socket, Dispatcher dispatcher) throws IOException {
        if (Config.ENABLE_FLOOD_PROTECTION)
            if (FloodProtector.getInstance().tooFast(socket.socket().getInetAddress().getHostAddress()))
                return null;

        return new LoginConnection(socket, dispatcher);
    }
}
