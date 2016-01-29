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

package org.typezero.gameserver.services.serialkillers;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.calc.StatOwner;
import org.typezero.gameserver.model.stats.calc.functions.IStatFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatAddFunction;
import org.typezero.gameserver.model.stats.calc.functions.StatRateFunction;
import org.typezero.gameserver.model.templates.serial_killer.RankPenaltyAttr;
import org.typezero.gameserver.model.templates.serial_killer.RankRestriction;
import org.typezero.gameserver.skillengine.change.Func;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dtem
 */
public class SerialKillerDebuff implements StatOwner {

	private List<IStatFunction> functions = new ArrayList<IStatFunction>();
	private RankRestriction rankRestriction;

	public void applyEffect(Player player, int rank) {
		if (rank == 0)
			return;

		rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(rank);

		if (hasDebuff()) {
			endEffect(player);
		}

		for (RankPenaltyAttr rankPenaltyAttr : rankRestriction.getPenaltyAttr()) {
			if (rankPenaltyAttr.getFunc().equals(Func.PERCENT))
				functions.add(new StatRateFunction(rankPenaltyAttr.getStat(), rankPenaltyAttr.getValue(), true));
			else
				functions.add(new StatAddFunction(rankPenaltyAttr.getStat(), rankPenaltyAttr.getValue(), true));
		}
		player.getGameStats().addEffect(this, functions);
	}


	public boolean hasDebuff() {
		return !functions.isEmpty();
	}

	public void endEffect(Player player) {
		functions.clear();
		player.getGameStats().endEffect(this);
	}

}
