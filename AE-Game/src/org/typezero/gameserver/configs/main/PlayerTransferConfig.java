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
 * @author KID
 */
public class PlayerTransferConfig {
	@Property(key = "ptransfer.max.kinah", defaultValue = "0")
	public static long MAX_KINAH;

	@Property(key = "ptransfer.bindpoint.elyos", defaultValue = "210010000 1212.9423 1044.8516 140.75568 32")
	public static String BIND_ELYOS;

	@Property(key = "ptransfer.bindpoint.asmo", defaultValue = "220010000 571.0388 2787.3420 299.8750 32")
	public static String BIND_ASMO;

	@Property(key = "ptransfer.allow.emotions", defaultValue = "true")
	public static boolean ALLOW_EMOTIONS;

	@Property(key = "ptransfer.allow.motions", defaultValue = "true")
	public static boolean ALLOW_MOTIONS;

	@Property(key = "ptransfer.allow.macro", defaultValue = "true")
	public static boolean ALLOW_MACRO;

	@Property(key = "ptransfer.allow.npcfactions", defaultValue = "true")
	public static boolean ALLOW_NPCFACTIONS;

	@Property(key = "ptransfer.allow.pets", defaultValue = "true")
	public static boolean ALLOW_PETS;

	@Property(key = "ptransfer.allow.recipes", defaultValue = "true")
	public static boolean ALLOW_RECIPES;

	@Property(key = "ptransfer.allow.skills", defaultValue = "true")
	public static boolean ALLOW_SKILLS;

	@Property(key = "ptransfer.allow.titles", defaultValue = "true")
	public static boolean ALLOW_TITLES;

	@Property(key = "ptransfer.allow.quests", defaultValue = "true")
	public static boolean ALLOW_QUESTS;

	@Property(key = "ptransfer.allow.inventory", defaultValue = "true")
	public static boolean ALLOW_INV;

	@Property(key = "ptransfer.allow.warehouse", defaultValue = "true")
	public static boolean ALLOW_WAREHOUSE;

	@Property(key = "ptransfer.allow.stigma", defaultValue = "true")
	public static boolean ALLOW_STIGMA;

	@Property(key = "ptransfer.block.samename", defaultValue = "false")
	public static boolean BLOCK_SAMENAME;

	@Property(key = "ptransfer.server.name.prefix", defaultValue = "_UNK")
	public static String NAME_PREFIX;

	@Property(key = "ptransfer.retransfer.hours", defaultValue = "_UNK")
	public static int REUSE_HOURS;

	@Property(key = "ptransfer.remove.skills.list", defaultValue = "*")
	public static String REMOVE_SKILL_LIST;
}
