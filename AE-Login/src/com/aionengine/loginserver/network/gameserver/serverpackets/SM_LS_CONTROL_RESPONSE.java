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


package com.aionengine.loginserver.network.gameserver.serverpackets;

import com.aionengine.loginserver.network.gameserver.GsConnection;
import com.aionengine.loginserver.network.gameserver.GsServerPacket;


/**
 * @author Aionchs-Wylovech
 */
public class SM_LS_CONTROL_RESPONSE extends GsServerPacket {

    private int type;

    private boolean result;

    private String playerName;

    private int param;

    private String adminName;

    private int accountId;

    public SM_LS_CONTROL_RESPONSE(int type, boolean result, String playerName, int accountId, int param, String adminName) {
        this.type = type;
        this.result = result;
        this.playerName = playerName;
        this.param = param;
        this.adminName = adminName;
        this.accountId = accountId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeImpl(GsConnection con) {
        writeC(4);
        writeC(type);
        writeC(result ? 1 : 0);
        writeS(adminName);
        writeS(playerName);
        writeC(param);
        writeD(accountId);
    }
}
