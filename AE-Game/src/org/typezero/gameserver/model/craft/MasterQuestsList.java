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
package org.typezero.gameserver.model.craft;

import org.typezero.gameserver.model.Race;

/**
 * @author synchro2
 */
public enum MasterQuestsList {
	COOKING_ELYOS(new int[]{19039, 19038}, Race.ELYOS, 40001),
	COOKING_ASMODIANS(new int[]{29039, 29038}, Race.ASMODIANS, 40001),
	WEAPONSMITHING_ELYOS(new int[]{19009, 19008}, Race.ELYOS, 40002),
	WEAPONSMITHING_ASMODIANS(new int[]{29009, 29008}, Race.ASMODIANS, 40002),
	ARMORSMITHING_ELYOS(new int[]{19015, 19014}, Race.ELYOS, 40003),
	ARMORSMITHING_ASMODIANS(new int[]{29015, 29014}, Race.ASMODIANS, 40003),
	TAILORING_ELYOS(new int[]{19021, 19020}, Race.ELYOS, 40004),
	TAILORING_ASMODIANS(new int[]{29021, 29020}, Race.ASMODIANS, 40004),
	ALCHEMY_ELYOS(new int[]{19033, 19032}, Race.ELYOS, 40007),
	ALCHEMY_ASMODIANS(new int[]{29033, 29032}, Race.ASMODIANS, 40007),
	HANDICRAFTING_ELYOS(new int[]{19027, 19026}, Race.ELYOS, 40008),
	HANDICRAFTING_ASMODIANS(new int[]{29027, 29026}, Race.ASMODIANS, 40008),
	MENUSIER_ELYOS(new int[]{19058, 19057}, Race.ELYOS, 40010),
	MENUSIER_ASMODIANS(new int[]{29058, 29057}, Race.ASMODIANS, 40010);

	private int[] skillsIds;
	private Race race;
	private int craftSkillId;

	private MasterQuestsList(int[] skillsIds, Race race, int craftSkillId) {
		this.skillsIds = skillsIds;
		this.race = race;
		this.craftSkillId = craftSkillId;
	}

	private Race getRace() {
		return race;
	}

	private int getCraftSkillId() {
		return craftSkillId;
	}

	public static int[] getSkillsIds(int craftSkillId, Race race) {
		for (MasterQuestsList mql : values()) {
			if (race.equals(mql.getRace()) && craftSkillId == mql.getCraftSkillId())
				return mql.skillsIds;
		}
		throw new IllegalArgumentException("Invalid craftSkillId: " + craftSkillId + " or race: " + race);
	}
}
