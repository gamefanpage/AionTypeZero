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


import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class SM_ITEM_USAGE_ANIMATION extends AionServerPacket {

	private int playerObjId;
	private int targetObjId;
	private int itemObjId;
	private int itemId;
	private int time;
	private int end;
	private int unk;

	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = 0;
		this.end = 1;
		this.unk = 1;
	}

	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId, int time, int end) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
	}

	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int itemObjId, int itemId, int time, int end, int unk) {
		this.playerObjId = playerObjId;
		this.targetObjId = playerObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
		this.unk = unk;
	}

	public SM_ITEM_USAGE_ANIMATION(int playerObjId, int targetObjId, int itemObjId, int itemId, int time, int end, int unk) {
		this.playerObjId = playerObjId;
		this.targetObjId = targetObjId;
		this.itemObjId = itemObjId;
		this.itemId = itemId;
		this.time = time;
		this.end = end;
		this.unk = unk;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (time > 0) {
			final Player player = World.getInstance().findPlayer(playerObjId);
			final Item item = player.getInventory().getItemByObjId(itemObjId);
			player.setUsingItem(item);
		}

		writeD(playerObjId); // player obj id
		writeD(targetObjId); // target obj id

		writeD(itemObjId); // itemObjId
		writeD(itemId); // item id

		writeD(time); // unk
		writeC(end); // unk
		writeC(0); // unk
		writeC(1);
		writeD(unk);
		writeC(0);//unk
	}
}
