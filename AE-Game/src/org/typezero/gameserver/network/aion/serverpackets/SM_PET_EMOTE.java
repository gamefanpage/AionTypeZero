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

import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.PetEmote;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 */
public class SM_PET_EMOTE extends AionServerPacket {

	private Pet pet;
	private PetEmote emote;
	private final float x, y, z, x2, y2, z2;
	private final byte heading;
	private int emotionId, param1;

	public SM_PET_EMOTE(Pet pet, PetEmote emote) {
		this(pet, emote, 0, 0, 0, (byte) 0);
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, byte h) {
		this(pet, emote, x, y, z, 0, 0, 0, h);
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, float x, float y, float z, float x2, float y2, float z2, byte h) {
		this.pet = pet;
		this.emote = emote;
		this.x = x;
		this.y = y;
		this.z = z;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.heading = h;
	}

	public SM_PET_EMOTE(Pet pet, PetEmote emote, int emotionId, int param1) {
		this(pet, emote);
		this.emotionId = emotionId;
		this.param1 = param1;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(pet.getObjectId());
		writeC(emote.getEmoteId());
		switch (emote) {
			case MOVE_STOP:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				break;
			case MOVETO:
				writeF(x);
				writeF(y);
				writeF(z);
				writeC(heading);
				writeF(x2);
				writeF(y2);
				writeF(z2);
				break;
			default:
				writeC(emotionId);
				writeC(param1); // happinessAdded?
				break;
		}
	}
}
