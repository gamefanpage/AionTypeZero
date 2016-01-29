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
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.serverpackets.SM_GROUP_DATA_EXCHANGE;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.Collection;

/**
 * @author xTz
 */
public class CM_GROUP_DATA_EXCHANGE extends AionClientPacket {

	private int groupType;
	private int action;
	private int unk2;
	private int dataSize;
	private byte[] data;

	public CM_GROUP_DATA_EXCHANGE(int opcode, AionConnection.State state, AionConnection.State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readC();

		switch (action) {
			case 1:
				dataSize = readD();
				break;
			default:
				groupType = readC();
				unk2 = readC();
				dataSize = readD();
				break;
		}
		if (dataSize > 0 && dataSize <= 5086)
			data = readB(dataSize);
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null || data == null) {
			return;
		}

		if (action == 1) {
			PacketSendUtility.broadcastPacket(player, new SM_GROUP_DATA_EXCHANGE(data));
			return;
		}

		Collection<Player> players = null;
		switch (groupType) {
			case 0:
				if (player.isInGroup2()) {
					players = player.getPlayerGroup2().getOnlineMembers();
				}
				break;
			case 1:
				if (player.isInAlliance2()) {
					players = player.getPlayerAllianceGroup2().getOnlineMembers();
				}
				break;
			case 2:
				if (player.isInLeague()) {
					players = player.getPlayerAllianceGroup2().getOnlineMembers();
				}
				break;
		}

		if (players != null) {
			for (Player member : players) {
				if (!member.equals(player)) {
					PacketSendUtility.sendPacket(member, new SM_GROUP_DATA_EXCHANGE(data, action, unk2));
				}
			}
		}
	}

}
