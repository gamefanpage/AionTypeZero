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

import org.typezero.gameserver.model.gameobjects.player.PortalCooldown;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import javolution.util.FastMap;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PortalCooldownList;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

import java.util.Map;

/**
 * @author nrg
 */
public class SM_INSTANCE_INFO extends AionServerPacket {

    private Player player;
    private boolean isAnswer;
    private int cooldownId;

    public SM_INSTANCE_INFO(Player player, int instanceId, boolean isAnswer)
    {
        this.player = player;
        this.isAnswer = isAnswer;
        this.cooldownId = instanceId;
    }

    public SM_INSTANCE_INFO(Player player, boolean isAnswer)
    {
        this.player = player;
        this.isAnswer = isAnswer;
        this.cooldownId = 0;
    }

    @Override
    protected void writeImpl(AionConnection con) {
        boolean isLean = player.isInTeam() && player.getCurrentTeam().isLeader(player);
        writeC(!isAnswer ? 2 : isLean && cooldownId == 0 ? 1 : 0);
        writeC(cooldownId);
        writeD(0x0); //unk1
        writeH(1);
        writeD(player.getObjectId());
        PortalCooldownList cooldownList = player.getPortalCooldownList();
        FastMap<Integer, InstanceCooltime> allcooltimes = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimes();
        if (cooldownId == 0) {
            writeH(allcooltimes.size());
            for (Map.Entry<Integer, InstanceCooltime> e : allcooltimes.entrySet()) {
                writeD(e.getKey());
                writeD(0);
                PortalCooldown pc = cooldownList.getPortalCooldown(e.getKey());
                if (pc != null) {
                    if (pc.getCooltime() == 0)
                        writeD(0);
                    else
                        writeD((int)((pc.getCooltime() - System.currentTimeMillis()) / 1000));

                    int count = e.getValue().getCount() - pc.getCount();
                    writeD(e.getValue().getCount());
                    writeD(count == 0 ? 0 : -count);
                } else {
                    writeD(0);
                    writeD(e.getValue().getCount());
                    writeD(0);
                }
            }
        } else {
            writeH(1);
            writeD(cooldownId);

            writeD(0);
            if (cooldownList.getPortalCooldown(cooldownId).getCooltime() == 0L) {
                writeD(0);
            } else {
                writeD((int)((cooldownList.getPortalCooldown(cooldownId).getCooltime() - System.currentTimeMillis()) / 1000L));
            }
            int count = (allcooltimes.get(cooldownId)).getCount() - cooldownList.getPortalCooldown(cooldownId).getCount();
            writeD(allcooltimes.get(cooldownId).getCount());
            writeD(count == 0 ? 0 : -count);
        }
        writeS(player.getName());

    }
}
