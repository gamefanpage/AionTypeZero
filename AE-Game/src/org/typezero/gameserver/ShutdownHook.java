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

package org.typezero.gameserver;

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager;
import com.aionemu.commons.utils.concurrent.RunnableStatsManager.SortBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.ShutdownConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.services.PeriodicSaveService;
import org.typezero.gameserver.services.player.PlayerLeaveWorldService;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.gametime.GameTimeManager;
import org.typezero.gameserver.world.World;

import java.util.Iterator;

/**
 * @author lord_rex
 */
public class ShutdownHook extends Thread {

	private static final Logger log = LoggerFactory.getLogger(ShutdownHook.class);

	public static ShutdownHook getInstance() {
		return SingletonHolder.INSTANCE;
	}

	@Override
	public void run() {
		if (ShutdownConfig.HOOK_MODE == 1) {
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.SHUTDOWN);
		}
		else if (ShutdownConfig.HOOK_MODE == 2) {
			shutdownHook(ShutdownConfig.HOOK_DELAY, ShutdownConfig.ANNOUNCE_INTERVAL, ShutdownMode.RESTART);
		}
	}

	public static enum ShutdownMode {
		NONE("terminating"),
		SHUTDOWN("shutting down"),
		RESTART("restarting");

		private final String text;

		private ShutdownMode(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

	private void sendShutdownMessage(int seconds) {
		try {
			Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext())
				return;
			while (onlinePlayers.hasNext()) {
				Player player = onlinePlayers.next();
				if (player != null && player.getClientConnection() != null)
					player.getClientConnection().sendPacket(SM_SYSTEM_MESSAGE.STR_SERVER_SHUTDOWN(String.valueOf(seconds)));
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void sendShutdownStatus(boolean status) {
		try {
			Iterator<Player> onlinePlayers = World.getInstance().getPlayersIterator();
			if (!onlinePlayers.hasNext())
				return;
			while (onlinePlayers.hasNext()) {
				Player player = onlinePlayers.next();
				if (player != null && player.getClientConnection() != null)
					player.getController().setInShutdownProgress(status);
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private void shutdownHook(int duration, int interval, ShutdownMode mode) {
		for (int i = duration; i >= interval; i -= interval) {
			try {
				if (World.getInstance().getPlayersIterator().hasNext()) {
					log.info("Runtime is " + mode.getText() + " in " + i + " seconds.");
					sendShutdownMessage(i);
					sendShutdownStatus(ShutdownConfig.SAFE_REBOOT);
				}
				else {
					log.info("Runtime is " + mode.getText() + " now ...");
					break; // fast exit.
				}

				if (i > interval) {
					sleep(interval * 1000);
				}
				else {
					sleep(i * 1000);
				}
			}
			catch (InterruptedException e) {
				return;
			}
		}

		// Disconnect login server from game.
		LoginServer.getInstance().gameServerDisconnected();

		// Disconnect all players.
		Iterator<Player> onlinePlayers;
		onlinePlayers = World.getInstance().getPlayersIterator();
		while (onlinePlayers.hasNext()) {
			Player activePlayer = onlinePlayers.next();
			try {
				PlayerLeaveWorldService.startLeaveWorld(activePlayer);
			}
			catch (Exception e) {
				log.error("Error while saving player " + e.getMessage());
			}
		}
		log.info("All players are disconnected...");

		RunnableStatsManager.dumpClassStats(SortBy.AVG);
		PeriodicSaveService.getInstance().onShutdown();

		// Save game time.
		GameTimeManager.saveTime();
		// Shutdown of cron service
		CronService.getInstance().shutdown();
		// ThreadPoolManager shutdown
		ThreadPoolManager.getInstance().shutdown();

		// Do system exit.
		if (mode == ShutdownMode.RESTART)
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		else
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);

		log.info("Runtime is " + mode.getText() + " now...");
	}

	/**
	 * @param delay
	 * @param announceInterval
	 * @param mode
	 */
	public void doShutdown(int delay, int announceInterval, ShutdownMode mode) {
		shutdownHook(delay, announceInterval, mode);
	}

	private static final class SingletonHolder {

		private static final ShutdownHook INSTANCE = new ShutdownHook();
	}
}
