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

/**
 * @author ATracer
 */
public class PeriodicSaveConfig {

	/**
	 * Time in seconds for saving player data
	 */
	@Property(key = "gameserver.periodicsave.player.general", defaultValue = "900")
	public static int PLAYER_GENERAL;

	/**
	 * Time in seconds for saving player items and item stones
	 */
	@Property(key = "gameserver.periodicsave.player.items", defaultValue = "900")
	public static int PLAYER_ITEMS;

	/**
	 * Time in seconds for saving legion wh items and item stones
	 */
	@Property(key = "gameserver.periodicsave.legion.items", defaultValue = "1200")
	public static int LEGION_ITEMS;

	/**
	 * Time in seconds for saving broker
	 */
	@Property(key = "gameserver.periodicsave.broker", defaultValue = "1500")
	public static int BROKER;

	@Property(key = "gameserver.periodicsave.player.pets", defaultValue = "5")
	public static int PLAYER_PETS;

}
