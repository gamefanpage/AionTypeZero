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

package com.aionengine.loginserver.network.gameserver.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionengine.loginserver.dao.AccountDAO;
import com.aionengine.loginserver.dao.PremiumDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.AccountToll;
import com.aionengine.loginserver.network.gameserver.GsClientPacket;

/**
 * @author xTz, Dr2co
 */
public class CM_ACCOUNT_TOLL_INFO extends GsClientPacket {

    private AccountToll toll;
    private byte type;
    private String accountName;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readImpl() {
        type = (byte) readC();
        toll.setToll(readQ());
        toll.setBonusToll(readQ());
        accountName = readS();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runImpl() {
        Account account = DAOManager.getDAO(AccountDAO.class).getAccount(accountName);

        if (account != null)
            DAOManager.getDAO(PremiumDAO.class).updateTolls(account.getId(), toll, 0, type);
    }
}
