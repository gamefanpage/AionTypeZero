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

package org.typezero.gameserver.network.aion.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.tradelist.TradeListTemplate;
import org.typezero.gameserver.model.templates.tradelist.TradeNpcType;
import org.typezero.gameserver.model.trade.RepurchaseList;
import org.typezero.gameserver.model.trade.TradeList;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.PrivateStoreService;
import org.typezero.gameserver.services.RepurchaseService;
import org.typezero.gameserver.services.TradeService;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 *
 * @author Cures
 */
public class CM_BUY_KINAH_REAL extends AionClientPacket {

    private static final Logger log = LoggerFactory.getLogger(CM_BUY_KINAH_REAL.class);
    private int kinah;
    private int unk2;
    private int stavka;
    private int unk4;
    private int time;

    public CM_BUY_KINAH_REAL(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        kinah = readD(); // 1 ==100000000 Вводить сумму 1 проводить будет 100 миллионов
        unk2 = readD();
        stavka = readD(); // Сколько толов хочеш за 100 миллионов золотых
        unk4 = readD();
        time = readD(); // На сколько времени поставить 24 или 48 часов

    }

    @Override
    protected void runImpl() {
    log.info("Info byte: " + " kinah: " + kinah + " unk2: " + unk2 + " stavka: " + stavka + " unk4: " + unk4 + " time: " + time);
    }
}
