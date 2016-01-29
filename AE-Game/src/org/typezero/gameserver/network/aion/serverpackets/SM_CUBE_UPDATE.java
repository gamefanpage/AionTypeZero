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
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Sweetkr
 */
public class SM_CUBE_UPDATE extends AionServerPacket {

	private int action;
	/**
	 * for action 0 - its storage type<br>
	 * for action 6 - its advanced stigma count
	 */
	private int actionValue;

	private int itemsCount;
	private int npcExpands;
	private int questExpands;

	public static SM_CUBE_UPDATE stigmaSlots(int slots)
	{
		return new SM_CUBE_UPDATE(6, slots);
	}

	public static SM_CUBE_UPDATE cubeSize(StorageType type, Player player)
	{
		int itemsCount = 0;
		int npcExpands = 0;
		int questExpands = 0;
		switch(type) {
			case CUBE:
				itemsCount = player.getInventory().size();
				npcExpands = player.getNpcExpands();
				questExpands = player.getQuestExpands();
				break;
			case REGULAR_WAREHOUSE:
				itemsCount = player.getWarehouse().size();
				npcExpands = player.getWarehouseSize();
				//questExpands = ?? //TODO!
				break;
			case LEGION_WAREHOUSE:
				itemsCount = player.getLegion().getLegionWarehouse().size();
				npcExpands = player.getLegion().getWarehouseLevel();
				break;
		}

		return new SM_CUBE_UPDATE(0, type.ordinal(), itemsCount, npcExpands, questExpands);
	}

	private SM_CUBE_UPDATE(int action, int actionValue, int itemsCount, int npcExpands, int questExpands) {
		this(action, actionValue);
		this.itemsCount = itemsCount;
		this.npcExpands = npcExpands;
		this.questExpands = questExpands;
	}

	private SM_CUBE_UPDATE(int action, int actionValue) {
		this.action = action;
		this.actionValue = actionValue;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		writeC(actionValue);
		switch (action) {
			case 0:
				writeD(itemsCount);
				writeC(npcExpands); // cube size from npc (so max 5 for now)
				writeC(questExpands); // cube size from quest (so max 2 for now)
				writeC(0); // unk - expands from items?
				break;
			case 6:
				break;
			default:
				break;
		}
	}
}
