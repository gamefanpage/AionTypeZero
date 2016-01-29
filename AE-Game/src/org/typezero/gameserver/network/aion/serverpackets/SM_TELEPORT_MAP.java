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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.teleport.TeleporterTemplate;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author alexa026 , orz
 */
public class SM_TELEPORT_MAP extends AionServerPacket {

	private int targetObjectId;
	private Player player;
	private TeleporterTemplate teleport;
	public Npc npc;

	private static final Logger log = LoggerFactory.getLogger(SM_TELEPORT_MAP.class);

	public SM_TELEPORT_MAP(Player player, int targetObjectId, TeleporterTemplate teleport) {
		this.player = player;
		this.targetObjectId = targetObjectId;
		this.npc = (Npc) World.getInstance().findVisibleObject(targetObjectId);
		this.teleport = teleport;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (teleport != null && teleport.getTeleportId() != 0) {
			writeD(targetObjectId);
			writeH(teleport.getTeleportId());
		}
		else {
			PacketSendUtility.sendMessage(player, "\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u043e\u0442\u0441\u0443\u0442\u0441\u0442\u0432\u0443\u0435\u0442 \u0432 npc_teleporter.xml \u0434\u043b\u044f npcid: " + npc.getNpcId());
			log.info(String.format("Missing teleport info with npcid: %d", npc.getNpcId()));
		}
	}
}
