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

package org.typezero.gameserver.network;

import java.sql.Timestamp;

/**
 *
 * @author KID
 *
 */
public class BannedMacEntry {
	private String mac, details;
	private Timestamp timeEnd;

	public BannedMacEntry(String address, long newTime) {
		this.mac = address;
		this.updateTime(newTime);
	}

	public BannedMacEntry(String address, Timestamp time, String details) {
		this.mac = address;
		this.timeEnd = time;
		this.details = details;
	}

	public final void setDetails(String details) {
		this.details = details;
	}

	public final void updateTime(long newTime) {
		this.timeEnd = new Timestamp(newTime);
	}

	public final String getMac() {
		return mac;
	}

	public final Timestamp getTime() {
		return timeEnd;
	}

	public final boolean isActive() {
		return timeEnd != null && timeEnd.getTime() > System.currentTimeMillis();
	}

	public final boolean isActiveTill(long time) {
		return timeEnd != null && timeEnd.getTime() > time;
	}

	public final String getDetails() {
		return details;
	}
}
