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

import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Rhys2002
 */
public class SM_GROUP_LOOT extends AionServerPacket {

	private int groupId;
	private int index;
	private int unk2;
	private int itemId;
	private int unk3;
	private int lootCorpseId;
	private int distributionId;
	private int playerId;
	private long luck;

	/**
	 * @param Player
	 *          Id must be 0 to start the Roll Options
	 */
	public SM_GROUP_LOOT(int groupId, int playerId, int itemId, int lootCorpseId, int distributionId, long luck, int index) {
		this.groupId = groupId;
		this.index = index;
		this.unk2 = 1;
		this.itemId = itemId;
		this.unk3 = 0;
		this.lootCorpseId = lootCorpseId;
		this.distributionId = distributionId;
		this.playerId = playerId;
		this.luck = luck;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(groupId);
		writeD(index);
		writeD(unk2);
		writeD(itemId);
		writeC(unk3);
		writeC(0); // 3.0
		writeC(0); // 3.5
		writeD(lootCorpseId);
		writeC(distributionId);
		writeD(playerId);
		writeD((int)luck);
	}
}
