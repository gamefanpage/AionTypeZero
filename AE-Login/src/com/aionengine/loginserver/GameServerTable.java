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

package com.aionengine.loginserver;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.IPRange;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionengine.loginserver.dao.GameServersDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.network.gameserver.GsAuthResponse;
import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.serverpackets.SM_REQUEST_KICK_ACCOUNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * GameServerTable contains list of GameServers registered on this LoginServer. GameServer may by online or down.
 *
 * @author -Nemesiss-
 */
public class GameServerTable {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(GameServerTable.class);

    /**
     * Map<Id,GameServer>
     */
    private static Map<Byte, GameServerInfo> gameservers;

    /**
     * Return collection contains all registered [up/down] GameServers.
     *
     * @return collection of GameServers.
     */
    public static Collection<GameServerInfo> getGameServers() {
        return Collections.unmodifiableCollection(gameservers.values());
    }

    /**
     * Load GameServers from database.
     */
    public static void load() {
        gameservers = getDAO().getAllGameServers();
        log.info("GameServerTable loaded " + gameservers.size() + " registered GameServers.");
    }

    /**
     * Register GameServer if its possible.
     *
     * @param gsConnection   Connection object
     * @param requestedId    id of server that was requested
     * @param defaultAddress default network address from server, usually internet address
     * @param ipRanges       mapping of various ip ranges, usually used for local area networks
     * @param port           port that is used by server
     * @param maxPlayers     maximum amount of players
     * @param password       server password that is specified configs, used to check if gs can auth on ls
     * @return GsAuthResponse
     */
    public static GsAuthResponse registerGameServer(GsConnection gsConnection, byte requestedId, byte[] defaultAddress,
                                                    List<IPRange> ipRanges, int port, int maxPlayers, String password) {
        GameServerInfo gsi = gameservers.get(requestedId);

        /**
         * This id is not Registered at LoginServer.
         */
        if (gsi == null) {
            log.info(gsConnection + " requestedID=" + requestedId + " not aviable!");
            return GsAuthResponse.NOT_AUTHED;
        }

        /**
         * Check if this GameServer is not already registered.
         */
        if (gsi.getConnection() != null)
            return GsAuthResponse.ALREADY_REGISTERED;

        /**
         * Check if password and ip are ok.
         */
        if (!gsi.getPassword().equals(password) || !NetworkUtils.checkIPMatching(gsi.getIp(), gsConnection.getIP())) {

            log.info(gsi.getPassword() + " " + password);
            log.info(gsConnection + " wrong ip or password!");
            return GsAuthResponse.NOT_AUTHED;
        }

        gsi.setDefaultAddress(defaultAddress);
        gsi.setIpRanges(ipRanges);
        gsi.setPort(port);
        gsi.setMaxPlayers(maxPlayers);
        gsi.setConnection(gsConnection);

        gsConnection.setGameServerInfo(gsi);
        return GsAuthResponse.AUTHED;
    }

    /**
     * Returns GameSererInfo object for given gameserverId.
     *
     * @param gameServerId
     * @return GameSererInfo object for given gameserverId.
     */
    public static GameServerInfo getGameServerInfo(byte gameServerId) {
        return gameservers.get(gameServerId);
    }

    /**
     * Check if account is already in use on any GameServer. If so - kick account from GameServer.
     *
     * @param acc account to check
     * @return true is account is logged in on one of GameServers
     */
    public static boolean isAccountOnAnyGameServer(Account acc) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.isAccountOnGameServer(acc.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method, used to kick account from any gameServer if it's logged in
     *
     * @param account account to kick
     */
    public static void kickAccountFromGameServer(Account account) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.isAccountOnGameServer(account.getId())) {
                gsi.getConnection().sendPacket(new SM_REQUEST_KICK_ACCOUNT(account.getId()));
                break;
            }
        }
    }

    /**
     * Retuns {@link com.aionengine.loginserver.dao.GameServersDAO} , just a shortcut
     *
     * @return {@link com.aionengine.loginserver.dao.GameServersDAO}
     */
    private static GameServersDAO getDAO() {
        return DAOManager.getDAO(GameServersDAO.class);
    }

    public static void pong(byte serverId, int pid) {
        for (GameServerInfo gsi : getGameServers()) {
            if (gsi.getId() == serverId) {
                gsi.getConnection().pong(pid);
                break;
            }
        }
    }
}
