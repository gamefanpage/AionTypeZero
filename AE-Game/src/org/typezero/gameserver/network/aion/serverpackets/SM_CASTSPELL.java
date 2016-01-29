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
 * This packet show casting spell animation.
 *
 * @author alexa026
 * @author rhys2002
 */
public class SM_CASTSPELL extends AionServerPacket {

	private final int attackerObjectId;
	private final int spellId;
	private final int level;
	private final int targetType;
	private final int duration;
	private final boolean isCharge;

	private int targetObjectId;

	private float x;
	private float y;
	private float z;

	public SM_CASTSPELL(int attackerObjectId, int spellId, int level, int targetType, int targetObjectId, int duration, boolean isCharge) {
		this.attackerObjectId = attackerObjectId;
		this.spellId = spellId;
		this.level = level;
		this.targetType = targetType;
		this.targetObjectId = targetObjectId;
		this.duration = duration;
		this.isCharge = isCharge;
	}

	public SM_CASTSPELL(int attackerObjectId, int spellId, int level, int targetType, float x, float y, float z,
		int duration) {
		this(attackerObjectId, spellId, level, targetType, 0, duration, false);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeD(attackerObjectId);
		writeH(spellId);
		writeC(level);

		writeC(targetType);
		switch (targetType) {
			case 0:
			case 3:
			case 4:
				writeD(targetObjectId);
				break;
			case 1:
				writeF(x);
				writeF(y);
				writeF(z);
				break;
			case 2:
				writeF(x);
				writeF(y);
				writeF(z);
				writeD(0);//unk1
				writeD(0);//unk2
				writeD(0);//unk3
				writeD(0);//unk4
				writeD(0);//unk5
				writeD(0);//unk6
				writeD(0);//unk7
				writeD(0);//unk8
		}

		writeH(duration);
		writeC(0x00);//unk
		writeF(0x01);//unk
		writeC(isCharge ? 0x01 : 0x00);//charge?
	}
}
