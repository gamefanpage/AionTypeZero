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

import org.typezero.gameserver.model.siege.FortressLocation;
import org.typezero.gameserver.model.siege.Influence;
import org.typezero.gameserver.model.siege.SourceLocation;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.SiegeService;
import java.util.Map;

public class SM_FORTRESS_STATUS extends AionServerPacket {

	@Override
	protected void writeImpl(AionConnection con) {
		Map<Integer, FortressLocation> fortresses = SiegeService.getInstance().getFortresses();
		Map<Integer, SourceLocation> sources = SiegeService.getInstance().getSources();
		Influence inf = Influence.getInstance();

		writeC(1);
		writeD(SiegeService.getInstance().getSecondsBeforeHourEnd());
		writeF(inf.getGlobalElyosInfluence());
		writeF(inf.getGlobalAsmodiansInfluence());
		writeF(inf.getGlobalBalaursInfluence());
		writeH(6);
		writeD(210050000);
		writeF(inf.getInggisonElyosInfluence());
		writeF(inf.getInggisonAsmodiansInfluence());
		writeF(inf.getInggisonBalaursInfluence());
		writeD(220070000);
		writeF(inf.getGelkmarosElyosInfluence());
		writeF(inf.getGelkmarosAsmodiansInfluence());
		writeF(inf.getGelkmarosBalaursInfluence());
		writeD(400010000);
		writeF(inf.getAbyssElyosInfluence());
		writeF(inf.getAbyssAsmodiansInfluence());
		writeF(inf.getAbyssBalaursInfluence());
		writeD(600030000);
		writeF(inf.getTiamarantaElyosInfluence());
		writeF(inf.getTiamarantaAsmodiansInfluence());
		writeF(inf.getTiamarantaBalaursInfluence());
		writeD(600050000);
		writeF(inf.getKatalamElyosInfluence());
		writeF(inf.getKatalamAsmodiansInfluence());
		writeF(inf.getKatalamBalaursInfluence());
		writeD(600060000);
		writeF(inf.getDanariaElyosInfluence());
		writeF(inf.getDanariaAsmodiansInfluence());
		writeF(inf.getDanariaBalaursInfluence());
		writeH(fortresses.size() + sources.size());

		for (FortressLocation fortress : fortresses.values()) {
			writeD(fortress.getLocationId());
			writeC(fortress.getNextState());
		}

		for (SourceLocation source : sources.values()) {
			writeD(source.getLocationId());
			writeC(source.getNextState());
		}
	}

}
