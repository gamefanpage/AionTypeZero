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
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.common.legacy.LootDistribution;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.model.team2.common.legacy.LootRuleType;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;

/**
 * @author Lyahim, Simple, xTz
 */
public class CM_DISTRIBUTION_SETTINGS extends AionClientPacket {

	@SuppressWarnings("unused")
	private int unk1;
	private int lootrul;
	private int misc;
	private LootRuleType lootrules;
	private LootDistribution autodistribution;
	private int common_item_above;
	private int superior_item_above;
	private int heroic_item_above;
	private int fabled_item_above;
	private int ethernal_item_above;
	@SuppressWarnings("unused")
	private int unk2;
	private int autodistr;

	public CM_DISTRIBUTION_SETTINGS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		unk1 = readD();
		lootrul = readD();
		switch (lootrul) {
			case 0:
				lootrules = LootRuleType.FREEFORALL;
				break;
			case 1:
				lootrules = LootRuleType.ROUNDROBIN;
				break;
			case 2:
				lootrules = LootRuleType.LEADER;
				break;
			default:
				lootrules = LootRuleType.FREEFORALL;
				break;
		}
		misc = readD();
		common_item_above = readD();
		superior_item_above = readD();
		heroic_item_above = readD();
		fabled_item_above = readD();
		ethernal_item_above = readD();
		autodistr = readD();
		unk2 = readD();

		switch (autodistr) {
			case 0:
				autodistribution = LootDistribution.NORMAL;
				break;
			case 2:
				autodistribution = LootDistribution.ROLL_DICE;
				break;
			case 3:
				autodistribution = LootDistribution.BID;
				break;
			default:
				autodistribution = LootDistribution.NORMAL;
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player leader = getConnection().getActivePlayer();

		PlayerGroup group = leader.getPlayerGroup2();
		if (group != null) {
			PlayerGroupService.changeGroupRules(group, new LootGroupRules(lootrules, autodistribution, common_item_above,
				superior_item_above, heroic_item_above, fabled_item_above, ethernal_item_above, misc));
		}
		org.typezero.gameserver.model.team2.alliance.PlayerAlliance alliance = leader.getPlayerAlliance2();
		if (alliance != null) {
			PlayerAllianceService.changeGroupRules(alliance, new LootGroupRules(lootrules, autodistribution,
				common_item_above, superior_item_above, heroic_item_above, fabled_item_above, ethernal_item_above, misc));
		}
	}
}
