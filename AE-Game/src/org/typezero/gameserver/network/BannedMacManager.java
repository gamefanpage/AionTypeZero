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
import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.network.loginserver.serverpackets.SM_MACBAN_CONTROL;

/**
 *
 * @author KID
 *
 */
public class BannedMacManager {
	private static BannedMacManager manager = new BannedMacManager();
	private final Logger log = LoggerFactory.getLogger(BannedMacManager.class);
	public static BannedMacManager getInstance() {
		return manager;
	}

	private Map<String, BannedMacEntry> bannedList = new FastMap<String, BannedMacEntry>();

	public final void banAddress(String address, long newTime, String details) {
		BannedMacEntry entry;
		if (bannedList.containsKey(address)) {
			if (bannedList.get(address).isActiveTill(newTime)) {
				return;
			} else {
				entry = bannedList.get(address);
				entry.updateTime(newTime);
			}
		} else
			entry = new BannedMacEntry(address, newTime);

		entry.setDetails(details);

		bannedList.put(address, entry);

		log.info("banned "+address+" to "+entry.getTime().toString()+" for "+details);
		LoginServer.getInstance().sendPacket(new SM_MACBAN_CONTROL((byte)1, address, newTime, details));
	}

	public final boolean unbanAddress(String address, String details) {
		if (bannedList.containsKey(address)) {
			bannedList.remove(address);
			log.info("unbanned "+address+" for "+details);
			LoginServer.getInstance().sendPacket(new SM_MACBAN_CONTROL((byte)0, address, 0, details));
			return true;
		}
		else
			return false;
	}

	public final boolean isBanned(String address) {
		if (bannedList.containsKey(address))
			return this.bannedList.get(address).isActive();
		else
			return false;
	}

	public final void dbLoad(String address, long time, String details) {
		this.bannedList.put(address, new BannedMacEntry(address, new Timestamp(time), details));
	}

	public void onEnd() {
		log.info("Loaded "+this.bannedList.size()+" banned mac addesses");
	}
}
