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
 * This class represents banned ip
 *
 * @author SoulKeeper
 */
public class BannedIP {

    /**
     * Returns id of ip ban
     */
    private Integer id;

    /**
     * Returns ip mask
     */
    private String mask;

    /**
     * Returns expiration time
     */
    private Timestamp timeEnd;

    /**
     * Checks if ban is still active
     *
     * @return true if ban is still active
     */
    public boolean isActive() {
        return timeEnd == null || timeEnd.getTime() > System.currentTimeMillis();
    }

    /**
     * Returns ban id
     *
     * @return ban id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets ban id
     *
     * @param id ban id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retuns ip mask
     *
     * @return ip mask
     */
    public String getMask() {
        return mask;
    }

    /**
     * Sets ip mask
     *
     * @param mask ip mask
     */
    public void setMask(String mask) {
        this.mask = mask;
    }

    /**
     * Returns expiration time of ban
     *
     * @return expiration time of ban
     */
    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    /**
     * Sets expiration time of ban
     *
     * @param timeEnd expiration time of ban
     */
    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

    /**
     * Returns true if this ip ban is equal to another. Based on {@link #mask}
     *
     * @param o another ip ban
     * @return true if ban's are equals
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof BannedIP))
            return false;

        BannedIP bannedIP = (BannedIP) o;

        return !(mask != null ? !mask.equals(bannedIP.mask) : bannedIP.mask != null);
    }

    /**
     * Returns ban's hashcode. Based on mask
     *
     * @return ban's hashcode
     */
    @Override
    public int hashCode() {
        return mask != null ? mask.hashCode() : 0;
    }
}
