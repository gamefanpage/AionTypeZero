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

import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.PetEmote;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET_EMOTE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class CM_PET_EMOTE extends AionClientPacket {

	private PetEmote emote;
	private int emoteId;

	private float x1;
	private float y1;
	private float z1;

	private byte h;

	private float x2;
	private float y2;
	private float z2;

	private int emotionId;
	private int unk2;

	public CM_PET_EMOTE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		emoteId = readC();
		emote = PetEmote.getEmoteById(emoteId);

		switch (emote) {
			case MOVE_STOP:
				x1 = readF();
				y1 = readF();
				z1 = readF();
				h = readSC();
				break;
			case MOVETO:
				x1 = readF();
				y1 = readF();
				z1 = readF();
				h = readSC();
				x2 = readF();
				y2 = readF();
				z2 = readF();
				break;
			default:
				emotionId = readC();
				unk2 = readC();
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Pet pet = player.getPet();

		if(pet == null)
			return;

		// sometimes client is crazy enough to send -2.4457384E7 as z coordinate
		// TODO (check retail) either its client bug or packet problem somewhere
		// reproducible by flying randomly and falling from long height with fly resume
		if (x1 < 0 || y1 < 0 || z1 < 0) {
			return;
		}
		// log.info("CM_PET_EMOTE emote {}, unk1 {}, unk2 {}", new Object[] { emoteId, unk1, unk2 });
		switch (emote) {
			case UNKNOWN:
				break;
			case ALARM:
				PacketSendUtility.broadcastPacket(player, new SM_PET_EMOTE(pet, emote), true);
				break;
			case MOVE_STOP:
				World.getInstance().updatePosition(pet, x1, y1, z1, h);
				PacketSendUtility.broadcastPacket(player, new SM_PET_EMOTE(pet, emote, x1, y1, z1, h), true);
				break;
			case MOVETO:
				World.getInstance().updatePosition(pet, x1, y1, z1, h);
				pet.getMoveController().setNewDirection(x2, y2, z2, h);
				PacketSendUtility.broadcastPacket(player, new SM_PET_EMOTE(pet, emote, x1, y1, z2, x2, y2, z2, h), true);
				break;
			case FLY:
				PacketSendUtility.broadcastPacket(player, new SM_PET_EMOTE(pet, emote, emotionId, unk2), true);
				break;
			default:
				if (emotionId > 0)
					PacketSendUtility.sendPacket(player, new SM_PET_EMOTE(pet, emote, emotionId, unk2));
				else
					PacketSendUtility.broadcastPacket(player, new SM_PET_EMOTE(pet, emote, 0, unk2), true);
		}
	}
}
