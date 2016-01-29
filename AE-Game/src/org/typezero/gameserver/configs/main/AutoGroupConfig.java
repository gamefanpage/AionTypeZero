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
 *
 * @author xTz
 */
public class AutoGroupConfig {

	@Property(key = "gameserver.autogroup.enable", defaultValue = "true")
	public static boolean AUTO_GROUP_ENABLE;

	@Property(key = "gameserver.startTime.enable", defaultValue = "true")
	public static boolean START_TIME_ENABLE;

	@Property(key = "gameserver.dredgion.timer", defaultValue = "120")
	public static long DREDGION_TIMER;

	@Property(key = "gameserver.dredgion2.enable", defaultValue = "true")
	public static boolean DREDGION2_ENABLE;

	@Property(key = "gameserver.dredgion.time", defaultValue = "0 0 0,12,20 ? * *")
	public static String DREDGION_TIMES;

	@Property(key = "gameserver.kamar.timer", defaultValue = "120")
	public static long KAMAR_TIMER;

	@Property(key = "gameserver.kamar.enable", defaultValue = "true")
	public static boolean KAMAR_ENABLE;

	@Property(key = "gameserver.kamar.time", defaultValue = "0 0 0,20 ? * MON,WED,SAT")
	public static String KAMAR_TIMES;
}