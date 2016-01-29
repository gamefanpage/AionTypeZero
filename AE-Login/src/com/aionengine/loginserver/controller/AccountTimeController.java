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
import com.aionengine.loginserver.dao.AccountPlayTimeDAO;
import com.aionengine.loginserver.dao.AccountTimeDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.AccountTime;

import java.sql.Timestamp;

/**
 * This class is for account time controlling. When character logins any server, it should get its day online time and
 * rest time. Some aion ingame feautres also depend on player's online time
 *
 * @author EvilSpirit
 */
public class AccountTimeController {

    /**
     * Update account time when character logins. The following field are being updated: - LastLoginTime (set to
     * CurrentTime) - RestTime (set to (RestTime + (CurrentTime-LastLoginTime - SessionDuration))
     *
     * @param account
     */
    public static void updateOnLogin(Account account) {
        AccountTime accountTime = account.getAccountTime();

        /**
         * It seems the account was just created, so new accountTime should be created too
         */
        if (accountTime == null) {
            accountTime = new AccountTime();
        }

        int lastLoginDay = getDays(accountTime.getLastLoginTime().getTime());
        int currentDay = getDays(System.currentTimeMillis());

        /**
         * The character from that account was online not today, so it's account timings should be nulled.
         */
        if (lastLoginDay < currentDay) {
            DAOManager.getDAO(AccountPlayTimeDAO.class).update(account.getId(), accountTime);
            accountTime.setAccumulatedOnlineTime(0);
            accountTime.setAccumulatedRestTime(0);
        } else {
            long restTime = System.currentTimeMillis() - accountTime.getLastLoginTime().getTime()
                    - accountTime.getSessionDuration();

            accountTime.setAccumulatedRestTime(accountTime.getAccumulatedRestTime() + restTime);

        }

        accountTime.setLastLoginTime(new Timestamp(System.currentTimeMillis()));

        DAOManager.getDAO(AccountTimeDAO.class).updateAccountTime(account.getId(), accountTime);
        account.setAccountTime(accountTime);
    }

    /**
     * Update account time when character logouts. The following field are being updated: - SessionTime (set to
     * CurrentTime - LastLoginTime) - AccumulatedOnlineTime (set to AccumulatedOnlineTime + SessionTime)
     *
     * @param account
     */
    public static void updateOnLogout(Account account) {
        AccountTime accountTime = account.getAccountTime();

        accountTime.setSessionDuration(System.currentTimeMillis() - accountTime.getLastLoginTime().getTime());
        accountTime.setAccumulatedOnlineTime(accountTime.getAccumulatedOnlineTime() + accountTime.getSessionDuration());
        DAOManager.getDAO(AccountTimeDAO.class).updateAccountTime(account.getId(), accountTime);
        account.setAccountTime(accountTime);
    }

    /**
     * Checks if account is already expired or not
     *
     * @param account
     * @return true, if account is expired, false otherwise
     */
    public static boolean isAccountExpired(Account account) {
        AccountTime accountTime = account.getAccountTime();

        return accountTime != null && accountTime.getExpirationTime() != null
                && accountTime.getExpirationTime().getTime() < System.currentTimeMillis();
    }

    /**
     * Checks if account is restricted by penalty or not
     *
     * @param account
     * @return true, is penalty is active, false otherwise
     */
    public static boolean isAccountPenaltyActive(Account account) {
        AccountTime accountTime = account.getAccountTime();

        // 1000 is 'infinity' value
        return accountTime != null
                && accountTime.getPenaltyEnd() != null
                && (accountTime.getPenaltyEnd().getTime() == 1000 || accountTime.getPenaltyEnd().getTime() >= System
                .currentTimeMillis());
    }

    /**
     * Get days from time presented in milliseconds
     *
     * @param millis time in ms
     * @return days
     */
    public static int getDays(long millis) {
        return (int) (millis / 1000 / 3600 / 24);
    }
}
