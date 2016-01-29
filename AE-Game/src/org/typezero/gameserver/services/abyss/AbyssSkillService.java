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

package org.typezero.gameserver.services.abyss;

import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;

/**
 * @author ATracer
 */
public class AbyssSkillService {

	/**
	 * @param player
	 */
	public static final void updateSkills(Player player) {
		AbyssRank abyssRank = player.getAbyssRank();
		if (abyssRank == null) {
			return;
		}
		AbyssRankEnum rankEnum = abyssRank.getRank();
		// remove all abyss skills first
		for (AbyssSkills abyssSkill : AbyssSkills.values()) {
			if (abyssSkill.getRace() == player.getRace()) {
				for (int skillId : abyssSkill.getSkills()) {
					player.getSkillList().removeSkill(skillId);
				}
			}
		}
		// add new skills
		if (abyssRank.getRank().getId() >= AbyssRankEnum.STAR5_OFFICER.getId()) {
			for (int skillId : AbyssSkills.getSkills(player.getRace(), rankEnum)) {
				player.getSkillList().addAbyssSkill(player, skillId, 1);
			}
		}
	}

	/**
	 * @param player
	 */
	public static final void onEnterWorld(Player player) {
		updateSkills(player);
	}
}

enum AbyssSkills {
	SUPREME_COMMANDER(Race.ELYOS, AbyssRankEnum.SUPREME_COMMANDER, 11889, 11898, 11900, 11903, 11904, 11905, 11906),
	COMMANDER(Race.ELYOS, AbyssRankEnum.COMMANDER, 11888, 11898, 11900, 11903, 11904),
	GREAT_GENERAL(Race.ELYOS, AbyssRankEnum.GREAT_GENERAL, 11887, 11897, 11899, 11903),
	GENERAL(Race.ELYOS, AbyssRankEnum.GENERAL, 11886, 11896, 11899),
	STAR5_OFFICER(Race.ELYOS, AbyssRankEnum.STAR5_OFFICER, 11885, 11895),
	SUPREME_COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.SUPREME_COMMANDER, 11894, 11898, 11902, 11903, 11904, 11905, 11906),
	COMMANDER_A(Race.ASMODIANS, AbyssRankEnum.COMMANDER, 11893, 11898, 11902, 11903, 11904),
	GREAT_GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GREAT_GENERAL, 11892, 11897, 11901, 11903),
	GENERAL_A(Race.ASMODIANS, AbyssRankEnum.GENERAL, 11891, 11896, 11901),
	STAR5_OFFICER_A(Race.ASMODIANS, AbyssRankEnum.STAR5_OFFICER, 11890, 11895);

	private int[] skills;
	private AbyssRankEnum rankenum;
	private Race race;

	private AbyssSkills(Race race, AbyssRankEnum rankEnum, int... skills) {
		this.race = race;
		this.rankenum = rankEnum;
		this.skills = skills;
	}

	public Race getRace() {
		return race;
	}

	public int[] getSkills() {
		return skills;
	}

	public static int[] getSkills(Race race, AbyssRankEnum rank) {
		for (AbyssSkills aSkills : values()) {
			if (aSkills.race == race && aSkills.rankenum == rank) {
				return aSkills.skills;
			}
		}
		LoggerFactory.getLogger(AbyssSkills.class).warn("No abyss skills for: " + race + " " + rank);
		return new int[0];
	}
}
