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

package org.typezero.gameserver.services.player;

import com.aionemu.commons.services.CronService;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.SellLimit;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class PlayerLimitService {

	private static FastMap<Integer, Long> sellLimit = new FastMap<Integer, Long>().shared();

	public static boolean updateSellLimit(Player player, long reward) {
		if (!CustomConfig.LIMITS_ENABLED)
			return true;

		int accoutnId = player.getPlayerAccount().getId();
		Long limit = sellLimit.get(accoutnId);
		if (limit == null) {
			limit = (long) (SellLimit.getSellLimit(player.getPlayerAccount().getMaxPlayerLevel()) * player.getRates().getSellLimitRate());
			sellLimit.put(accoutnId, limit);
		}

		if (limit < reward) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DAY_CANNOT_SELL_NPC(limit));
			return false;
		}
		else {
			limit -= reward;
			sellLimit.putEntry(accoutnId, limit);
			return true;
		}
	}

	public void scheduleUpdate() {
		CronService.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				sellLimit.clear();
			}

		}, CustomConfig.LIMITS_UPDATE, true);
	}

	public static PlayerLimitService getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final PlayerLimitService instance = new PlayerLimitService();
	}

}
