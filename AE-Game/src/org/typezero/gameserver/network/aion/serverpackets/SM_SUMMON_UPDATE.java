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

import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.stats.calc.Stat2;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author ATracer
 */
public class SM_SUMMON_UPDATE extends AionServerPacket {

	private Summon summon;

	public SM_SUMMON_UPDATE(Summon summon) {
		this.summon = summon;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(summon.getLevel());
		writeH(summon.getMode().getId());
		writeD(0);// unk
		writeD(0);// unk
		writeD(summon.getLifeStats().getCurrentHp());

		Stat2 maxHp = summon.getGameStats().getMaxHp();
		writeD(maxHp.getCurrent());

		Stat2 mainHandPAttack = summon.getGameStats().getMainHandPAttack();
		writeD(mainHandPAttack.getCurrent());

		Stat2 pDef = summon.getGameStats().getPDef();
		writeD(pDef.getCurrent());

		Stat2 mResist = summon.getGameStats().getMResist();
		writeH(mResist.getCurrent());

		Stat2 mDef = summon.getGameStats().getMDef();
		writeD(mDef.getCurrent());

		Stat2 accuracy = summon.getGameStats().getMainHandPAccuracy();
		writeH(accuracy.getCurrent());

		Stat2 mainHandPCritical = summon.getGameStats().getMainHandPCritical();
		writeH(mainHandPCritical.getCurrent());

		Stat2 mBoost = summon.getGameStats().getMBoost();
		writeH(mBoost.getCurrent());

		Stat2 suppression = summon.getGameStats().getMBResist();
		writeH(suppression.getCurrent());

		Stat2 mAccuracy = summon.getGameStats().getMainHandMAccuracy();
		writeH(mAccuracy.getCurrent());

		Stat2 mCritical = summon.getGameStats().getMCritical();
		writeH(mCritical.getCurrent());

		Stat2 parry = summon.getGameStats().getParry();
		writeH(parry.getCurrent());

		Stat2 evasion = summon.getGameStats().getEvasion();
		writeH(evasion.getCurrent());

		writeD(maxHp.getBase());
		writeD(mainHandPAttack.getBase());
		writeD(pDef.getBase());
		writeH(mResist.getBase());
		writeD(mDef.getBase());
		writeH(accuracy.getBase());
		writeH(mainHandPCritical.getBase());
		writeH(mBoost.getBase());
		writeH(suppression.getBase());
		writeH(mAccuracy.getBase());
		writeH(mCritical.getBase());
		writeH(parry.getBase());
		writeH(evasion.getBase());
	}

}
