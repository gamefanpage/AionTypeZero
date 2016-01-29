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

import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/*
 * @author sweetkr, Sarynth
 */
public class SM_BIND_POINT_INFO extends AionServerPacket {

	private final int mapId;
	private final float x;
	private final float y;
	private final float z;
	private final Kisk kisk;

	public SM_BIND_POINT_INFO(int mapId, float x, float y, float z, Player player) {
		this.mapId = mapId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.kisk = player.getKisk();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		// Appears 0x04 if bound to a kisk. 0x00 if not.
		writeC((kisk == null ? 0x00 : 0x04));

		writeC(0x01);// unk
		writeD(mapId);// map id
		writeF(x); // coordinate x
		writeF(y); // coordinate y
		writeF(z); // coordinate z
		writeD((kisk == null ? 0x00 : (kisk.isActive() ? kisk.getObjectId() : 0))); // kisk object id
	}

}
