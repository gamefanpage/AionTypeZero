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

package org.typezero.gameserver.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.gametime.GameTimeManager;
import org.typezero.gameserver.world.World;

import java.util.Iterator;

/**
 * @author ATracer
 */
public class GameTimeService {

	private static Logger log = LoggerFactory.getLogger(GameTimeService.class);

	public static final GameTimeService getInstance() {
		return SingletonHolder.instance;
	}

	private final static int GAMETIME_UPDATE = 3 * 60000;

	private GameTimeService() {
		/**
		 * Update players with current game time
		 */
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				log.info("Sending current game time to all players");
				Iterator<Player> iterator = World.getInstance().getPlayersIterator();
				while (iterator.hasNext()) {
					Player next = iterator.next();
					PacketSendUtility.sendPacket(next, new SM_GAME_TIME());
				}
				// Save game time.
				GameTimeManager.saveTime();
			}
		}, GAMETIME_UPDATE, GAMETIME_UPDATE);

		log.info("GameTimeService started. Update interval:" + GAMETIME_UPDATE);
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final GameTimeService instance = new GameTimeService();
	}
}
