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

import java.util.regex.Pattern;

/**
 * @author nrg
 */
public class NameConfig {

  /**
   * Enables custom names usage.
   */
  @Property(key = "gameserver.name.allow.custom", defaultValue = "false")
  public static boolean ALLOW_CUSTOM_NAMES;

	/**
	 * Character name pattern (checked when character is being created)
	 */
	@Property(key = "gameserver.name.characterpattern", defaultValue = "[a-zA-Z]{2,16}")
	public static Pattern CHAR_NAME_PATTERN;

	/**
	 * Forbidden word sequences Filters charname, miol, legion, chat
	 */
	@Property(key = "gameserver.name.forbidden.sequences", defaultValue = "")
	public static String NAME_SEQUENCE_FORBIDDEN;

	/**
	 * Enable client filter Filters charname, miol, legion, chat
	 */
	@Property(key = "gameserver.name.forbidden.enable.client", defaultValue = "true")
	public static boolean NAME_FORBIDDEN_ENABLE;

	/**
	 * Forbidden Charnames NOTE: Parsed out of aion 3.0 client Filters charname, miol, legion, chat
	 */
	@Property(key = "gameserver.name.forbidden.client", defaultValue = "")
	public static String NAME_FORBIDDEN_CLIENT;
}
