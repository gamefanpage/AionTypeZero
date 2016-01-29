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


package org.typezero.gameserver.model.account;

/**
 * Class for storing account's online and rest time
 *
 * @author EvilSpirit
 */
public class AccountTime {

	/**
	 * Accumulated online time in millis
	 */
	private long accumulatedOnlineTime;

	/**
	 * Accumulated rest(offline) time in millis
	 */
	private long accumulatedRestTime;

	/**
	 * get daily accumulated online time in millis
	 *
	 * @return time in millis
	 */
	public long getAccumulatedOnlineTime() {
		return accumulatedOnlineTime;
	}

	/**
	 * get daily accumulated online time in millis
	 *
	 * @param accumulatedOnlineTime
	 *          time in millis
	 */
	public void setAccumulatedOnlineTime(long accumulatedOnlineTime) {
		this.accumulatedOnlineTime = accumulatedOnlineTime;
	}

	/**
	 * get daily accumulated rest (offline) time since first login
	 *
	 * @return time in millis
	 */
	public long getAccumulatedRestTime() {
		return accumulatedRestTime;
	}

	/**
	 * get daily accumulated rest (offline) time since first login
	 *
	 * @param accumulatedRestTime
	 *          time in millis
	 */
	public void setAccumulatedRestTime(long accumulatedRestTime) {
		this.accumulatedRestTime = accumulatedRestTime;
	}

	/**
	 * Returns hour part rounded down.<br>
	 * For instance if time is 1 hr 32 min - it will return 1 hr
	 *
	 * @return hours part of accumulated online time
	 */
	public int getAccumulatedOnlineHours() {
		return toHours(accumulatedOnlineTime);
	}

	/**
	 * Returns minutes part.<br>
	 * For instance: if time is 1 hr 32 min - it will return 32 min
	 *
	 * @return minutes part of accumulated online time
	 */
	public int getAccumulatedOnlineMinutes() {
		return toMinutes(accumulatedOnlineTime);
	}

	/**
	 * Returns hour part rounded down.<br>
	 * For instance if time is 1 hr 32 min - it will return 1 hr
	 *
	 * @return hours part of accumulated rest time
	 */
	public int getAccumulatedRestHours() {
		return toHours(accumulatedRestTime);
	}

	/**
	 * Returns minutes part.<br>
	 * For instance: if time is 1 hr 32 min - it will return 32 min
	 *
	 * @return minutes part of accumulated rest time
	 */
	public int getAccumulatedRestMinutes() {
		return toMinutes(accumulatedRestTime);
	}

	/**
	 * Converts milliseconds to hours.<br>
	 * For instance if millis = 1 hr 32 min, 1 hour will be returned
	 *
	 * @param millis
	 *          milliseconds
	 * @return hours
	 */
	private static int toHours(long millis) {
		return (int) (millis / 1000) / 3600;
	}

	/**
	 * Converts milliseconds to minutes.<br>
	 * For instance if millis = 1 hr 32 min, 32 min will be returned
	 *
	 * @param millis
	 *          milliseconds
	 * @return minutes
	 */
	private static int toMinutes(long millis) {
		return (int) ((millis / 1000) % 3600) / 60;
	}
}
