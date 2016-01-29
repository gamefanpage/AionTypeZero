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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.world.WorldMapType;

/**
 * This packet is notify client what map should be loaded.
 *
 * @author -Nemesiss-
 */
public class SM_PLAYER_SPAWN extends AionServerPacket {

	/**
	 * Player that is entering game.
	 */
	private final Player player;

	/**
	 * Constructor.
	 *
	 * @param player
	 */
	public SM_PLAYER_SPAWN(Player player) {
		super();
		this.player = player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(player.getWorldId());
		writeD(player.getWorldId()); // world + chnl
		writeD(0x00); // unk
		writeC(WorldMapType.getWorld(player.getWorldId()).isPersonal() ? 1 : 0);
		writeF(player.getX()); // x
		writeF(player.getY()); // y
		writeF(player.getZ()); // z
		writeC(player.getHeading()); // heading
		writeD(0); // new 2.5
		writeD(0); // new 2.5
        if (player.getWorldId() == 300030000 && player.isInGroup2()) {
        	writeD(3);
        } else if (player.getWorldId() == 320100000 && player.isInGroup2()) {
        	writeD(4);
        } else if (player.getWorldId() == 300100000 && player.isInGroup2()|| player.getWorldId() == 300040000 && player.isInGroup2()|| player.getWorldId() == 300160000 && player.isInGroup2()|| player.getWorldId() == 300150000 && player.isInGroup2()|| player.getWorldId() == 300300000 && player.isInGroup2()) {
        	writeD(6);
        } else if (player.getWorldId() == 300220000 && player.isInGroup2()|| player.getWorldId() == 300600000 && player.isInGroup2()|| player.getWorldId() == 300260000 && player.isInGroup2()|| player.getWorldId() == 300270000 && player.isInGroup2()|| player.getWorldId() == 300380000 && player.isInGroup2()|| player.getWorldId() == 300520000 && player.isInGroup2()) {
        	writeD(5);
        } else if (player.getWorldId() == 301330000 && player.isInGroup2()) {
        	writeD(14);
        }  else {
        	writeD(0);
        }
		writeC(0); // 0 or 1 new 3.0
		writeD(0); // 4.0
	}

}
