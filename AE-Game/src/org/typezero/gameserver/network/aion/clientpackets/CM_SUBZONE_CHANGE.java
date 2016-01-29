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

import org.typezero.gameserver.configs.main.WorldConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.zone.ZoneInstance;
import java.util.List;

/**
 * @author Rolandas
 */
public class CM_SUBZONE_CHANGE extends AionClientPacket {

    private int unk;

    public CM_SUBZONE_CHANGE(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        // Always 1, maybe for neutral zones 0 ?
        unk = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        player.revalidateZones();
        if (player.getAccessLevel() >= 5 && WorldConfig.ENABLE_SHOW_ZONEENTER) {
            List<ZoneInstance> zones = player.getPosition().getMapRegion().getZones(player);
            int foundZones = 0;
            for (ZoneInstance zone : zones) {
                if (zone.getZoneTemplate().getZoneType() == ZoneClassName.DUMMY ||
                        zone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER)
                    continue;
                foundZones++;
                PacketSendUtility.sendMessage(player, "Passed zone: unk=" + unk + "; " + zone.getZoneTemplate().getZoneType()
                        + " " + zone.getAreaTemplate().getZoneName().name());
            }
            if (foundZones == 0) {
                PacketSendUtility.sendMessage(player, "Passed unknown zone, unk=" + unk);
                return;
            }
        }
    }

}
