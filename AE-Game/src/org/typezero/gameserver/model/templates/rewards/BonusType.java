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

package org.typezero.gameserver.model.templates.rewards;

/**
 * @author Rolandas
 */

public enum BonusType {
	BOSS,						// %Quest_L_boss, items having suffix _g_
	COIN,						// %Quest_L_coin -- not used, 99 lvl quests replaced with trade
	ENCHANT,
	FOOD,						// %Quest_L_food
	FORTRESS,				// %Quest_L_fortress
	GATHER,
	GOODS,					// %Quest_D_Goods
	ISLAND,
	LUNAR,					// %Quest_A_BranchLunarEvent, exchange charms
	MAGICAL,				// %Quest_L_magical -- unknown
	MANASTONE,			// %Quest_L_matter_option
	MASTER_RECIPE,	// %Quest_ws_master_recipe -- not used, 99 lvl quests, heart exchange now
	MATERIAL,				// %Quest_D_material (Only Asmodian)
	MEDAL,					// %Quest_L_medal, fountain rewards
	MEDICINE,				// %Quest_L_medicine; potions and remedies
	MOVIE,					// %Quest_L_Christmas; cut scenes
	NONE,
	RECIPE,					// %Quest_L_Recipe_20a_LF2A (Only Elyos, Theobomos)
	REDEEM,					// %Quest_L_redeem, exchange angel's/demon's eyes + kinah
	RIFT,						// %Quest_L_BranchRiftEvent
	TASK,						// %Quest_L_task; craft related
	WINTER					// %Quest_A_BranchWinterEvent
}
