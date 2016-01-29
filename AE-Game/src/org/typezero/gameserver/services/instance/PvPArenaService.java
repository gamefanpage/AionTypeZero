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

package org.typezero.gameserver.services.instance;

import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.model.autogroup.AutoGroupType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.joda.time.DateTime;

/**
 *
 * @author xTz
 */
public class PvPArenaService {

	public static boolean isPvPArenaAvailable(Player player, AutoGroupType agt) {
		if (AutoGroupConfig.START_TIME_ENABLE && !checkTime(agt)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401306, agt.getInstanceMapId()));
			return false;
		}
		if (!checkItem(player, agt)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
			return false;
		}
		// todo check cool down
		return true;
	}

	public static boolean checkItem(Player player, AutoGroupType agt) {
		Storage inventory = player.getInventory();
		if (agt.isPvPFFAArena() || agt.isPvPSoloArena()) {
			return inventory.getItemCountByItemId(186000135) > 0;
		}
		else if (agt.isHarmonyArena()) {
			return inventory.getItemCountByItemId(186000184) > 0;
		}
		else if (agt.isGloryArena()) {
			return inventory.getItemCountByItemId(186000185) >= 3;
		}
		return true;
	}

	private static boolean checkTime(AutoGroupType agt) {
		/*if (agt.isPvPFFAArena() || agt.isPvPSoloArena()) {
			return isPvPArenaAvailable();
		}
		else if (agt.isHarmonyArena()) {
			return isHarmonyArenaAvailable();
		}
		else if (agt.isGloryArena()) {
			return isGloryArenaAvailable();
		}*/
		return true;
	}

	private static boolean isPvPArenaAvailable() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		int day = now.getDayOfWeek();
		if (day == 6 || day == 7) {
			return hour == 0 || hour == 1 || (hour >= 10 && hour <= 23);
		}
		return hour == 0 || hour == 1 || hour == 12 || hour == 13 || (hour >= 18 && hour <= 23);

	}

	private static boolean isHarmonyArenaAvailable() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		int day = now.getDayOfWeek();
		if (day == 6)
		   return hour >= 10 || hour == 1 || hour == 2;
		else if (day == 7)
		   return hour == 0 || hour == 1 || hour >= 10;
		else
		   return (hour >= 10 && hour < 14) || (hour >= 18 && hour <= 23);
	}

	private static boolean isGloryArenaAvailable() {
		DateTime now = DateTime.now();
		int hour = now.getHourOfDay();
		int day = now.getDayOfWeek();
		return (day == 6 || day == 7) && hour >= 20 && hour < 22;
	}

}
