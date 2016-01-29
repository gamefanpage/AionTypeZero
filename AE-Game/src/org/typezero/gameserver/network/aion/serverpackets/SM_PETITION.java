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


import org.typezero.gameserver.model.Petition;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.PetitionService;

/**
 * @author zdead
 */
public class SM_PETITION extends AionServerPacket {

	private Petition petition;

	public SM_PETITION() {
		this.petition = null;
	}

	public SM_PETITION(Petition petition) {
		this.petition = petition;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		if (petition == null) {
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeH(0x00);
			writeC(0x00);
		}
		else {
			writeC(0x01); // Action ID ?
			writeD(100); // unk (total online players ?)
			writeH(PetitionService.getInstance().getWaitingPlayers(con.getActivePlayer().getObjectId())); // Users
																																																					// waiting for
																																																					// Support
			writeS(Integer.toString(petition.getPetitionId())); // Ticket ID
			writeH(0x00);
			writeC(50); // Total Petitions
			writeC(49); // Remaining Petitions
			writeH(PetitionService.getInstance().calculateWaitTime(petition.getPlayerObjId())); // Estimated minutes
																																																// before GM reply
			writeD(0x00);
		}
	}
}
