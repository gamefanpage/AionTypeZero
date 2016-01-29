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

package org.typezero.gameserver.model.templates.bonus;

/**
 * @author Wakizashi
 */

public enum InventoryBonusType {
	BOSS, // %Quest_L_boss; siege related?
	COIN, // %Quest_L_coin
	ENCHANT,
	FOOD, // %Quest_L_food
	FORTRESS, // %Quest_L_fortress; sends promotion mails with medals?
	GOODS, // %Quest_L_Goods
	ISLAND, // %Quest_L_3_island; siege related?
	MAGICAL, // %Quest_L_magical
	MANASTONE, // %Quest_L_matter_option
	MASTER_RECIPE, // %Quest_ta_l_master_recipe
	MATERIAL, // %Quest_L_material
	MEDAL, // %Quest_L_medal
	MEDICINE, // %Quest_L_medicine; potions and remedies
	MOVIE, // %Quest_L_Christmas; cut scenes
	NONE,
	RECIPE, // %Quest_L_Recipe
	REDEEM, // %Quest_L_Rnd_Redeem and %Quest_L_redeem
	TASK, // %Quest_L_task; craft related
}
