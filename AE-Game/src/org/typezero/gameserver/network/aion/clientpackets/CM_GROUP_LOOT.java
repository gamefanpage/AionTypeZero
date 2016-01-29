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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.drop.DropDistributionService;

/**
 * @author Rhys2002
 */
public class CM_GROUP_LOOT extends AionClientPacket {

	@SuppressWarnings("unused")
	private int groupId;
	private int index;
	@SuppressWarnings("unused")
	private int unk1;
	private int itemId;
	@SuppressWarnings("unused")
	private int unk2;
	@SuppressWarnings("unused")
	private int unk3;
	private int npcId;
	private int distributionId;
	private int roll;
	private long bid;
	@SuppressWarnings("unused")
	private int unk4;

	public CM_GROUP_LOOT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		groupId = readD();
		index = readD();
		unk1 = readD();
		itemId = readD();
		unk2 = readC();
		unk3 = readC(); // 3.0
		unk4 = readC(); // 3.5
		npcId = readD();
		distributionId = readC();// 2: Roll 3: Bid
		roll = readD();// 0: Never Rolled 1: Rolled
		bid = readQ();// 0: No Bid else bid amount
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		switch (distributionId) {
			case 2:
				DropDistributionService.getInstance().handleRoll(player, roll, itemId, npcId, index);
				break;
			case 3:
				DropDistributionService.getInstance().handleBid(player, bid, itemId, npcId, index);
				break;
		}
	}
}
