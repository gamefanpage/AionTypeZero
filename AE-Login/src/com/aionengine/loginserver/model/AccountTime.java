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

package com.aionengine.loginserver.model;

import java.sql.Timestamp;

/**
 * Class for storing account time data (last login time, last session duration time, accumulated online time today,
 * accumulated rest time today)
 *
 * @author EvilSpirit
 */
public class AccountTime {

    /**
     * Time the account has last logged in
     */
    private Timestamp lastLoginTime;
    /**
     * Time after the account will expired
     */
    private Timestamp expirationTime;
    /**
     * Time when the penalty will end
     */
    private Timestamp penaltyEnd;
    /**
     * The duration of the session
     */
    private long sessionDuration;
    /**
     * Accumulated Online Time
     */
    private long accumulatedOnlineTime;
    /**
     * Accumulated Rest Time
     */
    private long accumulatedRestTime;

    /**
     * Default constructor. Set the lastLoginTime to current time
     */
    public AccountTime() {
        this.lastLoginTime = new Timestamp(System.currentTimeMillis());
    }

    /**
     * @return lastLoginTime
     */
    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @param lastLoginTime
     */
    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * @return sessionDuration
     */
    public long getSessionDuration() {
        return sessionDuration;
    }

    /**
     * @param sessionDuration
     */
    public void setSessionDuration(long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    /**
     * @return accumulatedOnlineTime
     */
    public long getAccumulatedOnlineTime() {
        return accumulatedOnlineTime;
    }

    /**
     * @param accumulatedOnlineTime
     */
    public void setAccumulatedOnlineTime(long accumulatedOnlineTime) {
        this.accumulatedOnlineTime = accumulatedOnlineTime;
    }

    /**
     * @return accumulatedRestTime
     */
    public long getAccumulatedRestTime() {
        return accumulatedRestTime;
    }

    /**
     * @param accumulatedRestTime
     */
    public void setAccumulatedRestTime(long accumulatedRestTime) {
        this.accumulatedRestTime = accumulatedRestTime;
    }

    /**
     * @return expirationTime
     */
    public Timestamp getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime
     */
    public void setExpirationTime(Timestamp expirationTime) {
        this.expirationTime = expirationTime;
    }

    /**
     * @return penaltyEnd
     */
    public Timestamp getPenaltyEnd() {
        return penaltyEnd;
    }

    /**
     * @param penaltyEnd
     */
    public void setPenaltyEnd(Timestamp penaltyEnd) {
        this.penaltyEnd = penaltyEnd;
    }
}
