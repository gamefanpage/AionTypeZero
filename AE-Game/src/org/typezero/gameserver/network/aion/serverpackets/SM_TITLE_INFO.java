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
import org.typezero.gameserver.model.gameobjects.player.title.TitleList;
import org.typezero.gameserver.model.gameobjects.player.title.Title;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author cura, xTz
 * @modified -Enomine-
 */
public class SM_TITLE_INFO extends AionServerPacket {

	private TitleList titleList;
	private int action; // 0: list, 1: self set, 3: broad set
	private int titleId;
	private int bonusTitleId;
	private int playerObjId;

	/**
	 * title list
	 *
	 * @param player
	 */
	public SM_TITLE_INFO(Player player) {
		this.action = 0;
		this.titleList = player.getTitleList();
	}

	/**
	 * self title set
	 *
	 * @param titleId
	 */
	public SM_TITLE_INFO(int titleId) {
		this.action = 1;
		this.titleId = titleId;
	}

	/**
	 * broad title set
	 *
	 * @param player
	 * @param titleId
	 */
	public SM_TITLE_INFO(Player player, int titleId) {
		this.action = 3;
		this.playerObjId = player.getObjectId();
		this.titleId = titleId;
	}

	public SM_TITLE_INFO(boolean flag) {
		this.action = 4;
		this.titleId = flag ? 1 : 0;
	}

	public SM_TITLE_INFO(Player player, boolean flag) {
		this.action = 5;
		this.playerObjId = player.getObjectId();
		this.titleId = flag ? 1 : 0;
	}

	public SM_TITLE_INFO(int action, int bonusTitleId){
		this.action = action;
		this.bonusTitleId = bonusTitleId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);
		switch (action) {
			case 0:
				writeC(0x00);
				writeH(titleList.size());
				for (Title title : titleList.getTitles()) {
					writeD(title.getId());
					writeD(title.getRemainingTime());
				}
				break;
			case 1: // self set
				writeH(titleId);
				break;
			case 3: // broad set
				writeD(playerObjId);
				writeH(titleId);
				break;
			case 4: // Mentor flag self
				writeH(titleId);
				break;
			case 5: // broad set mentor fleg
				writeD(playerObjId);
				writeH(titleId);
				break;
			case 6://Title wich will take BonusStats from
				writeH(bonusTitleId);
		}
	}
}
