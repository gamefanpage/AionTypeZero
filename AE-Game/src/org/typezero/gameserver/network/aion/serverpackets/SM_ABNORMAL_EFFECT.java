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

import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 */
public class SM_ABNORMAL_EFFECT extends AionServerPacket {

	private int effectedId;
	private int effectType = 1;// 1: creature 2: effected is player
	private int abnormals;
	private Collection<Effect> filtered;

	public SM_ABNORMAL_EFFECT(Creature effected, int abnormals, Collection<Effect> effects) {
		this.abnormals = abnormals;
		this.effectedId = effected.getObjectId();
		this.filtered = effects;

		if (effected instanceof Player)
			effectType = 2;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(effectedId);
		writeC(effectType); //unk
		writeD(0); // unk
		writeD(abnormals); // unk
		writeD(0); // unk
		writeC(127); // unk 4.5
		writeH(filtered.size()); // effects size

		for (Effect effect : filtered) {
			switch(effectType) {
				case 2:
					writeD(effect.getEffectorId());
				case 1:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
					writeC(effect.getTargetSlot());
					writeD(effect.getRemainingTime());
					break;
				default:
					writeH(effect.getSkillId());
					writeC(effect.getSkillLevel());
			}
		}
	}
}
