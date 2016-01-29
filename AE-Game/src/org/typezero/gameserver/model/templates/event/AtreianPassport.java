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

package org.typezero.gameserver.model.templates.event;

import org.typezero.gameserver.model.AttendType;
import org.typezero.gameserver.utils.gametime.DateTimeUtil;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * @author Alcapwnd
 */
@XmlRootElement(name = "login_event")
@XmlAccessorType(XmlAccessType.NONE)
public class AtreianPassport {

    /**
     * Location Id.
     */
    @XmlAttribute(name = "id", required = true)
    private int id;
    /**
     * location name.
     */
    @XmlAttribute(name = "name")
    private String name = "";
    @XmlAttribute(name = "active", required = true)
    private int active;
    @XmlAttribute(name = "period_start", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pStart;
    @XmlAttribute(name = "period_end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pEnd;
    @XmlAttribute(name = "attend_type", required = true)
    private AttendType attendType;
    @XmlAttribute(name = "attend_num")
    private int attendNum;
    @XmlAttribute(name = "reward_item", required = true)
    private int rewardItem;
    @XmlAttribute(name = "reward_item_num", required = true)
    private int rewardItemNum = 1;
    @XmlAttribute(name = "reward_item_expire")
    private int rewardItemExpire;
    private int rewardId = 0;
    private boolean finish = false;

    public int getId() {
        return id;
    }

    public int getActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public DateTime getPeriodStart() {
        return DateTimeUtil.getDateTime(pStart.toGregorianCalendar());
    }

    public DateTime getPeriodEnd() {
        return DateTimeUtil.getDateTime(pEnd.toGregorianCalendar());
    }

    public AttendType getAttendType() {
        return attendType;
    }

    public int getAttendNum() {
        return attendNum;
    }

    public int getRewardItem() {
        return rewardItem;
    }

    public int getRewardItemNum() {
        return rewardItemNum;
    }

    public int getRewardItemExpire() {
        return rewardItemExpire;
    }

    public int getRewardId() {
        return rewardId;
    }

    public void setRewardId(int rewardId) {
        this.rewardId = rewardId;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }
}
