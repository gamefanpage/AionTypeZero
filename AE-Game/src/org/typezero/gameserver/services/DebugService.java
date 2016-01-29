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

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class DebugService {

	private static final Logger log = LoggerFactory.getLogger(DebugService.class);

	private static final int ANALYZE_PLAYERS_INTERVAL = 30 * 60 * 1000;

	public static final DebugService getInstance() {
		return SingletonHolder.instance;
	}

	private DebugService() {
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				analyzeWorldPlayers();
			}

		}, ANALYZE_PLAYERS_INTERVAL, ANALYZE_PLAYERS_INTERVAL);
		log.info("DebugService started. Analyze iterval: " + ANALYZE_PLAYERS_INTERVAL);
	}

	private void analyzeWorldPlayers() {
		log.info("Starting analysis of world players at " + System.currentTimeMillis());

		Iterator<Player> playersIterator = World.getInstance().getPlayersIterator();
		while (playersIterator.hasNext()) {
			Player player = playersIterator.next();

			/**
			 * Check connection
			 */
			AionConnection connection = player.getClientConnection();
			if (connection == null) {
				log.warn(String.format("[DEBUG SERVICE] Player without connection: "
					+ "detected: ObjId %d, Name %s, Spawned %s", player.getObjectId(), player.getName(), player.isSpawned()));
				continue;
			}

			/**
			 * Check CM_PING packet
			 */
			long lastPingTimeMS = connection.getLastPingTimeMS();
			long pingInterval = System.currentTimeMillis() - lastPingTimeMS;
			if (lastPingTimeMS > 0 && pingInterval > 300000) {
				log.warn(String.format("[DEBUG SERVICE] Player with large ping interval: "
					+ "ObjId %d, Name %s, Spawned %s, PingMS %d", player.getObjectId(), player.getName(), player.isSpawned(),
					pingInterval));
			}
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DebugService instance = new DebugService();
	}
}
