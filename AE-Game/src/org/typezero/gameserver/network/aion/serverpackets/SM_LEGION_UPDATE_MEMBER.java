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
import org.typezero.gameserver.model.team.legion.LegionMemberEx;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author Simple
 */
public class SM_LEGION_UPDATE_MEMBER extends AionServerPacket {

	private static final byte OFFLINE = 0x00;
	private static final byte ONLINE = 0x01;
	private Player player;
	private LegionMemberEx LM;
	private int msgId;
	private String text;
	private byte isOnline;

	public SM_LEGION_UPDATE_MEMBER(Player player, int msgId, String text) {
		this.player = player;
		this.msgId = msgId;
		this.text = text;
		this.isOnline = player.isOnline() ? ONLINE : OFFLINE;
	}

	public SM_LEGION_UPDATE_MEMBER(LegionMemberEx LM, int msgId, String text) {
		this.LM = LM;
		this.msgId = msgId;
		this.text = text;
		this.isOnline = LM.isOnline() ? ONLINE : OFFLINE;
	}

	public SM_LEGION_UPDATE_MEMBER(Player player) {
		this.player = player;
		this.isOnline = OFFLINE;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (player != null) {
			writeD(player.getObjectId());
			writeC(player.getLegionMember().getRank().getRankId());
			writeC(player.getCommonData().getPlayerClass().getClassId());
			writeC(player.getLevel());
			writeD(player.getPosition().getMapId());
			writeC(isOnline);
			writeD(player.isOnline() ? 0 : player.getLastOnline());
			writeD(1); // 3.0
			writeD(msgId);
			writeS(text);
		}
		else if (LM != null) {
			writeD(LM.getObjectId());
			writeC(LM.getRank().getRankId());
			writeC(LM.getPlayerClass().getClassId());
			writeC(LM.getLevel());
			writeD(LM.getWorldId());
			writeC(isOnline);
			writeD(LM.isOnline() ? 0 : LM.getLastOnline());
			writeD(1); // TODO: house settings
			writeD(msgId);
			writeS(text);
		}
	}
}
