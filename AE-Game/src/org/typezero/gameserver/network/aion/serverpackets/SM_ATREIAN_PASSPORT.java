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

package org.typezero.gameserver.network.aion.serverpackets;

import org.typezero.gameserver.model.AttendType;
import org.typezero.gameserver.model.templates.event.AtreianPassport;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

import java.sql.Timestamp;
import java.util.Map;


/**
 * @author Alcapwnd
 */
public class SM_ATREIAN_PASSPORT extends AionServerPacket {
    private int stamps;
    private int currentPassportId;
    private int arrival;
    private Timestamp time;
    private Map<Integer, AtreianPassport> passports;
    private int year;
    private int check;

    public SM_ATREIAN_PASSPORT(Map<Integer, AtreianPassport> passports, int stamps, int currentPassportId, Timestamp time, int year, int arrival, int check) {
        this.passports = passports;
        this.stamps = stamps;
        this.currentPassportId = currentPassportId;
        this.time = time;
        this.arrival = arrival;
        this.check = check;
        this.year = year;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        writeH(year);
        writeH(arrival);
        writeH(0);//unk //can be variable
        writeH(this.passports.size());

        for (AtreianPassport atp : passports.values()) {
            writeD(atp.getId());
            writeD(stamps);
            writeH(atp.getRewardId());
            if (atp.isFinish() && atp.getAttendType() == AttendType.CUMULATIVE)
                writeH(1);
            else
                writeH(0);
            writeD((int) time.getTime() / 1000);
            atp.setRewardId(0);
            atp.setFinish(false);
        }
    }
}
