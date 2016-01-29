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

package org.typezero.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlEnum;


/**
 * @author vlog
 */
@XmlEnum
public enum RandomType {

	ENCHANTMENT,
	MANASTONE,
	MANASTONE_COMMON_GRADE_10,
	MANASTONE_COMMON_GRADE_20,
	MANASTONE_COMMON_GRADE_30,
	MANASTONE_COMMON_GRADE_40,
	MANASTONE_COMMON_GRADE_50,
	MANASTONE_COMMON_GRADE_60,
	MANASTONE_COMMON_GRADE_70,
	MANASTONE_RARE_GRADE_20,
	MANASTONE_RARE_GRADE_30,
	MANASTONE_RARE_GRADE_40,
	MANASTONE_RARE_GRADE_50,
	MANASTONE_RARE_GRADE_60,
	MANASTONE_RARE_GRADE_70,
	MANASTONE_LEGEND_GRADE_30,
	MANASTONE_LEGEND_GRADE_40,
	MANASTONE_LEGEND_GRADE_50,
	MANASTONE_LEGEND_GRADE_60,
	MANASTONE_LEGEND_GRADE_70,
	SHINING_MANASTONE_LEGEND_GRADE_70,
	ANCIENT_MANASTONE_RARE_70,
	ANCIENT_MANASTONE_LEGEND_70,
	ANCIENT_MANASTONE_GOLD_70,
	ANCIENT_MANASTONE_EPIC_70,
	EPIC_CRAFT_ITEM_47,
	IDIAN_STONE_50_60,
	ANCIENTITEMS,
	CHUNK_EARTH,
	CHUNK_ROCK, 
	CHUNK_SAND, 
	CHUNK_GEMSTONE,
	SCROLLS,
	POTION,
	GOD_STONE_50,
	GOD_STONE_60,
        GOD_STONE_LEGEND,
        GOD_STONE_UNIQUE,
        RELIC,
        ENCHANTMENT_50_100,
        ENCHANTMENT_100_150,
        MYSTIC_STONE_40,
        MYSTIC_STONE_50,
        MYSTIC_STONE_65,
        MYSTIC_STONE_BONUS;

	private int level;

	private RandomType() {
	}

	private RandomType(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
