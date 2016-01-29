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

package com.aionengine.loginserver.controller;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.utils.NetworkUtils;
import com.aionengine.loginserver.dao.BannedIpDAO;
import com.aionengine.loginserver.model.BannedIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;

/**
 * Class that controlls all ip banning activity
 *
 * @author SoulKeeper
 */
public class BannedIpController {

    /**
     * Logger for this class.
     */
    private static final Logger log = LoggerFactory.getLogger(BannedIpController.class);

    /**
     * List of banned ip adresses
     */
    private static Set<BannedIP> banList;

    public static void start() {
        clean();
        load();
    }

    private static void clean() {
        getDAO().cleanExpiredBans();
    }

    /**
     * Loads list of banned ips
     */
    public static void load() {
        reload();
    }

    /**
     * Loads list of banned ips
     */
    public static void reload() {
        // we are not going to make ip ban every minute, so it's ok to simplify a concurrent code a bit
        banList = getDAO().getAllBans();
        log.info("BannedIpController loaded " + banList.size() + " IP bans.");
    }

    /**
     * Checks if ip (or mask) is banned
     *
     * @param ip ip address to check for ban
     * @return is it banned or not
     */
    public static boolean isBanned(String ip) {
        for (BannedIP ipBan : banList) {
            if (ipBan.isActive() && NetworkUtils.checkIPMatching(ipBan.getMask(), ip))
                return true;
        }
        return false;
    }

    /**
     * Bans ip or mask for infinite period of time
     *
     * @param ip ip to ban
     * @return was ip banned or not
     */
    public static boolean banIp(String ip) {
        return banIp(ip, null);
    }

    /**
     * Bans ip (or mask)
     *
     * @param ip         ip to ban
     * @param expireTime ban expiration time, null = never expires
     * @return was ip banned or not
     */
    public static boolean banIp(String ip, Timestamp expireTime) {
        if (ip.equals("127.0.0.1"))
            return false;

        BannedIP ipBan = new BannedIP();
        ipBan.setMask(ip);
        ipBan.setTimeEnd(expireTime);
        banList.add(ipBan);
        try {
            getDAO().insert(ipBan);
            return true;
        } catch (Exception e) {
            log.warn("Ip " + ip + " is already banned.");
            return false;
        }
    }

    /**
     * Adds or updates ip ban. Changes are reflected in DB
     *
     * @param ipBan banned ip to add or change
     * @return was it updated or not
     */
    public static boolean addOrUpdateBan(BannedIP ipBan) {
        if (ipBan.getId() == null) {
            if (getDAO().insert(ipBan)) {
                banList.add(ipBan);
                return true;
            }
            return false;
        }
        return getDAO().update(ipBan);
    }

    /**
     * Removes ip ban.
     *
     * @param ip ip to unban
     * @return returns true if ip was successfully unbanned
     */
    public static boolean unbanIp(String ip) {
        Iterator<BannedIP> it = banList.iterator();
        while (it.hasNext()) {
            BannedIP ipBan = it.next();
            if (ipBan.getMask().equals(ip)) {
                if (getDAO().remove(ipBan)) {
                    it.remove();
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * Retuns {@link com.aionengine.loginserver.dao.BannedIpDAO} , just a shortcut
     *
     * @return {@link com.aionengine.loginserver.dao.BannedIpDAO}
     */
    private static BannedIpDAO getDAO() {
        return DAOManager.getDAO(BannedIpDAO.class);
    }
}
