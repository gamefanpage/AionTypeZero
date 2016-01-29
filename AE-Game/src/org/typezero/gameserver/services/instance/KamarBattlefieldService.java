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

package org.typezero.gameserver.services.instance;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.services.CronService;
import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.AutoGroupService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import java.util.Iterator;
import javolution.util.FastList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Sharlatan
 */
public class KamarBattlefieldService {

    private static final Logger log = LoggerFactory.getLogger(KamarBattlefieldService.class);
    private boolean registerAvailable;
    private final FastList<Integer> playersWithCooldown = FastList.newInstance();
    public static final byte minLevel = 60, capLevel = 66;
    public static final int maskId = 107;

    public void start() {
        String[] times = AutoGroupConfig.KAMAR_TIMES.split("\\|");
        for (String cron : times) {
            CronService.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    startKamarRegistration();
                }
            }, cron);
            log.info("Scheduled Battlefield: based on cron expression: " + cron + " Duration: " + AutoGroupConfig.KAMAR_TIMER + " in minutes");
        }
    }

    private void startUregisterKamarTask() {
        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                registerAvailable = false;
                playersWithCooldown.clear();
                AutoGroupService.getInstance().unRegisterInstance(maskId);
                Iterator<Player> iter = World.getInstance().getPlayersIterator();
                while (iter.hasNext()) {
                    Player player = iter.next();
                    if (player.getLevel() > minLevel) {
                        int instanceMaskId = getInstanceMaskId(player);
                        if (instanceMaskId > 0) {
                            PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, SM_AUTO_GROUP.wnd_EntryIcon, true));
                        }
                    }
                }
            }
        }, AutoGroupConfig.KAMAR_TIMER * 60 * 1000);
    }

    private void startKamarRegistration() {
        this.registerAvailable = true;
        startUregisterKamarTask();
        Iterator<Player> iter = World.getInstance().getPlayersIterator();
        while (iter.hasNext()) {
            Player player = iter.next();
            if (player.getLevel() > minLevel && player.getLevel() < capLevel) {
                int instanceMaskId = getInstanceMaskId(player);
                if (instanceMaskId > 0) {
                    PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, SM_AUTO_GROUP.wnd_EntryIcon));
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_OPEN_IDKAMAR);
                }
            }
        }
    }

    public boolean isKamarAvailable() {
        return this.registerAvailable;
    }

    public byte getInstanceMaskId(Player player) {
        int level = player.getLevel();
        if (level < minLevel || level >= capLevel) {
            return 0;
        }

        return maskId;
    }

    public void addCoolDown(Player player) {
        this.playersWithCooldown.add(player.getObjectId());
    }

    public boolean hasCoolDown(Player player) {
        return this.playersWithCooldown.contains(player.getObjectId());
    }

    public void showWindow(Player player, byte instanceMaskId) {
        if (getInstanceMaskId(player) != instanceMaskId) {
            return;
        }
        if (!this.playersWithCooldown.contains(player.getObjectId())) {
            PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId));
        }
    }

    public boolean isKamarAvialable() {
        return this.registerAvailable;
    }

    private static class SingletonHolder {

        protected static final KamarBattlefieldService instance = new KamarBattlefieldService();
    }

    public static KamarBattlefieldService getInstance() {
        return SingletonHolder.instance;
    }
}
