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

package org.typezero.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CraftConfig {

	/**
	 * Enable craft skills unrestricted level-up
	 */
	@Property(key = "gameserver.craft.skills.unrestricted.levelup.enable", defaultValue = "false")
	public static boolean UNABLE_CRAFT_SKILLS_UNRESTRICTED_LEVELUP;

	@Property(key = "gameserver.craft.skills.delete.excess.enable", defaultValue = "false")
	public static boolean DELETE_EXCESS_CRAFT_ENABLE;

	/**
	 * Maximum number of expert skills a player can have
	 */
	@Property(key = "gameserver.craft.max.expert.skills", defaultValue = "2")
	public static int MAX_EXPERT_CRAFTING_SKILLS;

	/**
	 * Maximum number of master skills a player can have
	 */
	@Property(key = "gameserver.craft.max.master.skills", defaultValue = "1")
	public static int MAX_MASTER_CRAFTING_SKILLS;

	/**
	 * Chance to have a critical procraft (applied on first step)
	 */
	@Property(key = "gameserver.craft.critical.rate.regular", defaultValue = "15")
	public static int CRAFT_CRIT_RATE;
	@Property(key = "gameserver.craft.critical.rate.premium", defaultValue = "15")
	public static int PREMIUM_CRAFT_CRIT_RATE;
	@Property(key = "gameserver.craft.critical.rate.vip", defaultValue = "15")
	public static int VIP_CRAFT_CRIT_RATE;

	/**
	 * Chance to have a combo procraft (applied on second step)
	 */
	@Property(key = "gameserver.craft.combo.rate.regular", defaultValue = "25")
	public static int CRAFT_COMBO_RATE;
	@Property(key = "gameserver.craft.combo.rate.premium", defaultValue = "25")
	public static int PREMIUM_CRAFT_COMBO_RATE;
	@Property(key = "gameserver.craft.combo.rate.vip", defaultValue = "25")
	public static int VIP_CRAFT_COMBO_RATE;
}
