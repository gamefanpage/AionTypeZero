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

package com.aionengine.loginserver.network.gameserver;

import com.aionemu.commons.network.AConnection;
import com.aionemu.commons.network.Dispatcher;
import com.aionengine.loginserver.GameServerInfo;
import com.aionengine.loginserver.PingPongThread;
import com.aionengine.loginserver.configs.Config;
import com.aionengine.loginserver.network.factories.GsPacketHandlerFactory;
import com.aionengine.loginserver.utils.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Object representing connection between LoginServer and GameServer.
 *
 * @author -Nemesiss-
 */
public class GsConnection extends AConnection {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(GsConnection.class);

    /**
     * Possible states of GsConnection
     */
    public static enum State {
        /**
         * Means that GameServer just connect, but is not authenticated yet
         */
        CONNECTED,
        /**
         * GameServer is authenticated
         */
        AUTHED
    }

    /**
     * Server Packet "to send" Queue
     */
    private final Deque<GsServerPacket> sendMsgQueue = new ArrayDeque<GsServerPacket>();

    /**
     * Current state of this connection
     */
    private State state;

    /**
     * GameServerInfo for this GsConnection.
     */
    private GameServerInfo gameServerInfo = null;

    private PingPongThread pingThread;

    /**
     * Constructor.
     *
     * @param sc
     * @param d
     * @throws IOException
     */
    public GsConnection(SocketChannel sc, Dispatcher d) throws IOException {
        super(sc, d, 8192 * 8, 8192 * 8);

    }

    /**
     * Called by Dispatcher. ByteBuffer data contains one packet that should be processed.
     *
     * @param data
     * @return True if data was processed correctly, False if some error occurred and connection should be closed NOW.
     */
    @Override
    public boolean processData(ByteBuffer data) {
        GsClientPacket pck = GsPacketHandlerFactory.handle(data, this);

        if (pck != null && pck.read())
            ThreadPoolManager.getInstance().executeLsPacket(pck);

        return true;
    }

    /**
     * This method will be called by Dispatcher, and will be repeated till return false.
     *
     * @param data
     * @return True if data was written to buffer, False indicating that there are not any more data to write.
     */
    @Override
    protected final boolean writeData(ByteBuffer data) {
        synchronized (guard) {
            GsServerPacket packet = sendMsgQueue.pollFirst();
            if (packet == null)
                return false;

            packet.write(this, data);
            return true;
        }
    }

    /**
     * This method is called by Dispatcher when connection is ready to be closed.
     *
     * @return time in ms after witch onDisconnect() method will be called. Always return 0.
     */
    @Override
    protected final long getDisconnectionDelay() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onDisconnect() {
        if (Config.ENABLE_PINGPONG)
            this.pingThread.closeMe();
        log.info(this + " disconnected");
        if (gameServerInfo != null) {
            gameServerInfo.setConnection(null);
            gameServerInfo.clearAccountsOnGameServer();
            gameServerInfo = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onServerClose() {
        // TODO mb some packet should be send to gameserver before closing?
        close(/* packet, */true);
    }

    /**
     * Sends GsServerPacket to this client.
     *
     * @param bp GsServerPacket to be sent.
     */
    public final void sendPacket(GsServerPacket bp) {
        synchronized (guard) {
            /**
             * Connection is already closed or waiting for last (close packet) to be sent
             */
            if (isWriteDisabled())
                return;

            sendMsgQueue.addLast(bp);
            enableWriteInterest();
        }
    }

    /**
     * Its guaranted that closePacket will be sent before closing connection, but all past and future packets wont.
     * Connection will be closed [by Dispatcher Thread], and onDisconnect() method will be called to clear all other
     * things. forced means that server shouldn't wait with removing this connection.
     *
     * @param closePacket Packet that will be send before closing.
     * @param forced      have no effect in this implementation.
     */
    public final void close(GsServerPacket closePacket, boolean forced) {
        synchronized (guard) {
            if (isWriteDisabled())
                return;

            pendingClose = true;
            isForcedClosing = forced;
            sendMsgQueue.clear();
            sendMsgQueue.addLast(closePacket);
            enableWriteInterest();
        }
    }

    /**
     * @return Current state of this connection.
     */
    public State getState() {
        return state;
    }

    /**
     * @param state Set current state of this connection.
     */
    public void setState(State state) {
        this.state = state;
        if (state == State.AUTHED) {
            if (Config.ENABLE_PINGPONG) {
                ThreadPoolManager.getInstance().schedule(pingThread, 5000);
            }
        }
    }

    /**
     * @return GameServerInfo for this GsConnection or null if this GsConnection is not authenticated yet.
     */
    public GameServerInfo getGameServerInfo() {
        return gameServerInfo;
    }

    /**
     * @param gameServerInfo Set GameServerInfo for this GsConnection.
     */
    public void setGameServerInfo(GameServerInfo gameServerInfo) {
        this.gameServerInfo = gameServerInfo;
    }

    /**
     * @return String info about this connection
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GameServer [ID:");
        if (gameServerInfo != null) {
            sb.append(gameServerInfo.getId());
        } else {
            sb.append("null");
        }
        sb.append("] ").append(getIP());
        return sb.toString();
    }

    public void pong(int pid) {
        if (Config.ENABLE_PINGPONG)
            this.pingThread.onResponse(pid);
    }

    @Override
    protected void initialized() {
        // TODO Auto-generated method stub
        state = State.CONNECTED;
        String ip = getIP();

        if (Config.ENABLE_PINGPONG)
            pingThread = new PingPongThread(this);

        log.info("Gameserver connection attemp from: " + ip);
    }
}
