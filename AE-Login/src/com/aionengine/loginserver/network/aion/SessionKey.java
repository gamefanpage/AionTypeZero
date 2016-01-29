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

package com.aionengine.loginserver.network.aion;

import com.aionemu.commons.utils.Rnd;
import com.aionengine.loginserver.model.Account;

/**
 * @author -Nemesiss-
 */
public class SessionKey {

    /**
     * accountId - will be used for authentication on Game Server side.
     */
    public final int accountId;
    /**
     * login ok key
     */
    public final int loginOk;
    /**
     * play ok1 key
     */
    public final int playOk1;
    /**
     * play ok2 key
     */
    public final int playOk2;

    /**
     * Create new SesionKey for this Account
     *
     * @param acc
     */
    public SessionKey(Account acc) {
        this.accountId = acc.getId();
        this.loginOk = Rnd.nextInt();
        this.playOk1 = Rnd.nextInt();
        this.playOk2 = Rnd.nextInt();
    }

    /**
     * Create new SesionKey with given values.
     *
     * @param accountId
     * @param loginOk
     * @param playOk1
     * @param playOk2
     */
    public SessionKey(int accountId, int loginOk, int playOk1, int playOk2) {
        this.accountId = accountId;
        this.loginOk = loginOk;
        this.playOk1 = playOk1;
        this.playOk2 = playOk2;
    }

    /**
     * Check if given values are ok.
     *
     * @param accountId
     * @param loginOk
     * @return true if accountId and loginOk match this SessionKey
     */
    public boolean checkLogin(int accountId, int loginOk) {
        return this.accountId == accountId && this.loginOk == loginOk;
    }

    /**
     * Check if this SessionKey have the same values.
     *
     * @param key
     * @return true if key match this SessionKey.
     */
    public boolean checkSessionKey(SessionKey key) {
        return (playOk1 == key.playOk1 && accountId == key.accountId && playOk2 == key.playOk2 && loginOk == key.loginOk);
    }
}
