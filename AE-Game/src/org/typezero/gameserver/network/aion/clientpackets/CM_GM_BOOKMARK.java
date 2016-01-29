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

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gm.GmCommands;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;

/**
 * @author xTz
 */
public class CM_GM_BOOKMARK extends AionClientPacket {

    private GmCommands command;
    private String playerName;
    private String[] parts;

    public CM_GM_BOOKMARK(int opcode, State state, State... restStates) {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl() {
        playerName = readS();
        parts = playerName.split(" ");
        command = GmCommands.getValue(parts[0]);
        playerName = parts[1];
    }

    @Override
    protected void runImpl() {
        Player admin = getConnection().getActivePlayer();
        Player player = World.getInstance().findPlayer(Util.convertName(playerName));
        if (admin == null) {
            return;
        }
        if (admin.getAccessLevel() < AdminConfig.GM_PANEL) {
            return;
        }
        if (player == null) {
            PacketSendUtility.sendMessage(admin, "Could not find an online player with that name.");
            return;
        }
        switch (command) {
            case GM_MAIL_LIST:
                //TODO Show mail box
                break;
            case INVENTORY:
                break;
            case TELEPORTTO:
                TeleportService2.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ());
                break;
            case STATUS:
                //TODO Player Status
                break;
            case SEARCH:
                //TODO Target selected
                break;
            case GM_GUILDHISTORY:
                //TODO Player Legion Info
                break;
            case GM_BUDDY_LIST:
                //TODO FRIEND LIST
                break;
            case RECALL:
                TeleportService2.teleportTo(player, admin.getWorldId(), admin.getX(), admin.getY(), admin.getZ());
            default:
                PacketSendUtility.sendMessage(admin, "Invalid command: " + command.name());
                break;
        }
    }
}
