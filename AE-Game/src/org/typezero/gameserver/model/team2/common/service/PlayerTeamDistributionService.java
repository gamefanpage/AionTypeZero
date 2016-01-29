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

package org.typezero.gameserver.model.team2.common.service;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RewardType;
import org.typezero.gameserver.model.gameobjects.player.XPCape;
import org.typezero.gameserver.model.team2.TemporaryPlayerTeam;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.stats.StatFunctions;
import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ATracer, nrg
 */
public class PlayerTeamDistributionService {

	/**
	 * This method will send a reward if a player is in a team
	 */
	public static void doReward(TemporaryPlayerTeam<?> team, float damagePercent, Npc owner, AionObject winner) {
		if (team == null || owner == null) {
			return;
		}

		// Find team's members and determine highest level
		PlayerTeamRewardStats filteredStats = new PlayerTeamRewardStats(owner);
		team.applyOnMembers(filteredStats);

		// All are dead or not nearby
		if (filteredStats.players.isEmpty() || !filteredStats.hasLivingPlayer) {
			return;
		}

		// Reward mode
		long expReward;
		if (filteredStats.players.size() + filteredStats.mentorCount == 1) {
			expReward = (long) (StatFunctions.calculateSoloExperienceReward(filteredStats.players.get(0), owner));
		}
		else {
			expReward = (long) (StatFunctions.calculateGroupExperienceReward(filteredStats.highestLevel, owner));
		}

		// Party Bonus 2 members 10%, 3 members 20% ... 6 members 50%
		int size = filteredStats.players.size();
		int bonus = 100;
		if (size > 1) {
			bonus = 150 + (size - 2) * 10;
		}

		for (Player member : filteredStats.players) {
			//mentor and dead players shouldn't receive AP/EP/DP
			if (member.isMentor() || member.getLifeStats().isAlreadyDead()) {
				continue;
			}

			// Reward init
			long rewardXp = (long) (expReward * bonus * member.getLevel()) / (filteredStats.partyLvlSum * 100);
			int rewardDp = StatFunctions.calculateGroupDPReward(member, owner);
			float rewardAp = 1;

			// Players 10 levels below highest member get 0 reward.
			if (filteredStats.highestLevel - member.getLevel() >= 10) {
				rewardXp = 0;
				rewardDp = 0;
			}
			else if (filteredStats.mentorCount > 0) {
				int cape = XPCape.values()[(int) member.getLevel()].value();
				if (cape < rewardXp) {
					rewardXp = cape;
				}
			}

			// Dmg percent correction
			rewardXp *= damagePercent;
			rewardDp *= damagePercent;
			rewardAp *= damagePercent;

			member.getCommonData().addExp(rewardXp, RewardType.GROUP_HUNTING, owner.getObjectTemplate().getNameId());

			// DP reward
			member.getCommonData().addDp(rewardDp);

			// AP reward
			if (owner.isRewardAP() && !(filteredStats.mentorCount > 0 && CustomConfig.MENTOR_GROUP_AP)) {
				rewardAp *= StatFunctions.calculatePvEApGained(member, owner);
				int ap = (int) rewardAp / filteredStats.players.size();
				if (ap >= 1) {
					AbyssPointsService.addAp(member, owner, ap);
				}
			}
		}

		// Give Drop
		Player mostDamagePlayer = owner.getAggroList().getMostPlayerDamageOfMembers(team.getMembers(), filteredStats.highestLevel);
		if (mostDamagePlayer == null) {
			return;
		}

		if (winner.equals(team) && (!owner.getAi2().getName().equals("chest") || filteredStats.mentorCount == 0)) {
			DropRegistrationService.getInstance().registerDrop(owner, mostDamagePlayer, filteredStats.highestLevel, filteredStats.players);
		}
	}

	private static class PlayerTeamRewardStats implements Predicate<Player> {

		final List<Player> players = new ArrayList<Player>();
		int partyLvlSum = 0;
		int highestLevel = 0;
		int mentorCount = 0;
		boolean hasLivingPlayer = false;
		Npc owner;

		public PlayerTeamRewardStats(Npc owner) {
			this.owner = owner;
		}

		@Override
		public boolean apply(Player member) {
			if (member.isOnline()) {
				if (MathUtil.isIn3dRange(member, owner, GroupConfig.GROUP_MAX_DISTANCE)) {
					QuestEngine.getInstance().onKill(new QuestEnv(owner, member, 0, 0));

					if (member.isMentor()) {
						mentorCount++;
						return true;
					}

					if (!hasLivingPlayer && !member.getLifeStats().isAlreadyDead())
						hasLivingPlayer = true;

					players.add(member);
					partyLvlSum += member.getLevel();
					if (member.getLevel() > highestLevel)
						highestLevel = member.getLevel();
				}
			}
			return true;
		}

	}

}
