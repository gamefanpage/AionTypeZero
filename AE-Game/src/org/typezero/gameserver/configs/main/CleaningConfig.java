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
 * @author nrg
 */
public class CleaningConfig {

	/**
	 * Enable Database Cleaning
	 */
	@Property(key = "gameserver.cleaning.enable", defaultValue = "false")
	public static boolean CLEANING_ENABLE;

	/**
	 * Period after which inactive chars get deleted
     * It is expressed in number of days
	 */
	@Property(key = "gameserver.cleaning.period", defaultValue = "180")
	public static int CLEANING_PERIOD;

	/**
	 * Number of threads executing the cleaning
	 * If you have many chars to delete you should use a value between 4 and 6
	 */
	@Property(key = "gameserver.cleaning.threads", defaultValue = "2")
	public static int CLEANING_THREADS;

	/**
	 * Maximum amount of chars deleted at one execution
	 * If too many chars are deleted in one run your database will
	 * get strongly fragmented which increases runtime dramatically
	 * Note: 0 for not limitation
	 */
	@Property(key = "gameserver.cleaning.limit", defaultValue = "5000")
	public static int CLEANING_LIMIT;
}
