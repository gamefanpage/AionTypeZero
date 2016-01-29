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

package com.aionengine.loginserver.dao;

import com.aionemu.commons.database.dao.DAO;
import com.aionengine.loginserver.model.BannedIP;

import java.sql.Timestamp;
import java.util.Set;

/**
 * DAO that manages Banned IPs
 *
 * @author SoulKeeper
 */
public abstract class BannedIpDAO implements DAO {

    /**
     * Inserts ip mask to database, returns BannedIP object that represents inserted mask or null if error.<br>
     * Expire time is null so ban never expires.<br>
     *
     * @param mask ip mask to ban
     * @return BannedIP object represetns mask or null if error happened
     */
    public abstract BannedIP insert(String mask);

    /**
     * Inserts ip mask to database with given expireTime.<br>
     * Null is allowed for expire time in case of infinite ban.<br>
     * Returns object that represents ip mask or null in case of error.<br>
     *
     * @param mask       ip mask to ban
     * @param expireTime expiration time of ban
     * @return object that represetns added ban or null in case of error
     */
    public abstract BannedIP insert(String mask, Timestamp expireTime);

    /**
     * Inserts BannedIP object to database.<br>
     * ID of object must be NULL.<br>
     * If insert was successfull - sets the assigned id to BannedIP object and returns true.<br>
     * In case of error returns false without modification of bannedIP object.<br>
     *
     * @param bannedIP record to add to db
     * @return true in case of success or false
     */
    public abstract boolean insert(BannedIP bannedIP);

    /**
     * Updates BannedIP object.<br>
     * ID of object must NOT be null.<br>
     * In case of success returns true.<br>
     * In case of error returns false.<br>
     *
     * @param bannedIP record to update
     * @return true in case of success or false in other case
     */
    public abstract boolean update(BannedIP bannedIP);

    /**
     * Removes ban by mask.<br>
     * Returns true in case of success, false othervise.<br>
     *
     * @param mask ip mask to remove
     * @return true in case of success, false in other case
     */
    public abstract boolean remove(String mask);

    /**
     * Removes BannedIP record by ID. Id must not be null.<br>
     * Returns true in case of success, false in case of error
     *
     * @param bannedIP record to unban
     * @return true if removeas wass successfull, false in case of error
     */
    public abstract boolean remove(BannedIP bannedIP);

    /**
     * Returns all bans from database.
     *
     * @return all bans from database.
     */
    public abstract Set<BannedIP> getAllBans();

    public abstract void cleanExpiredBans();

    /**
     * Returns class name that will be uses as unique identifier for all DAO classes
     *
     * @return class name
     */
    @Override
    public final String getClassName() {
        return BannedIpDAO.class.getName();
    }
}
