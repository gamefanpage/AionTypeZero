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

import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import javolution.util.FastList;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DISPUTE_LAND;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.typezero.gameserver.world.zone.ZoneAttributes;

/**
 * @author Source
 */
public class DisputeLandService {

	private boolean active;
	private FastList<Integer> worlds = new FastList<Integer>();
	private static final int chance = CustomConfig.DISPUTE_RND_CHANCE;
	private static final String rnd = CustomConfig.DISPUTE_RND_SCHEDULE;
	private static final String fxd = CustomConfig.DISPUTE_FXD_SCHEDULE;

	private DisputeLandService() {
	}

	public static DisputeLandService getInstance() {
		return DisputeLandServiceHolder.INSTANCE;
	}

	public void init() {
		if (!CustomConfig.DISPUTE_ENABLED) {
			return;
		}

		// Dispute worldId's
		worlds.add(600020000);
		worlds.add(600020001);
		worlds.add(600030000);

		CronService.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				setActive(chance > Rnd.get(100));

				if (isActive()) {
					// Disable after 30 mins
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							setActive(false);
						}

					}, 1800 * 1000);
				}
			}

		}, rnd);

		CronService.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				// Disable after 5 hours
				setActive(true);

				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						setActive(false);
					}

				}, 5 * 3600 * 1000);
			}

		}, fxd);
	}

	public boolean isActive() {
		if (!CustomConfig.DISPUTE_ENABLED) {
			return false;
		}

		return active;
	}

	public void setActive(boolean value) {
		active = value;
		syncState();
		broadcast();
	}

	private void syncState() {
		for (int world : worlds) {
			if (world == 600020001) {
				continue;
			}

			if (active) {
				World.getInstance().getWorldMap(world).setWorldOption(ZoneAttributes.PVP_ENABLED);
			}
			else {
				World.getInstance().getWorldMap(world).removeWorldOption(ZoneAttributes.PVP_ENABLED);
			}
		}
	}

	private void broadcast(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DISPUTE_LAND(worlds, active));
	}

	private void broadcast() {
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				broadcast(player);
			}

		});
	}

	public void onLogin(Player player) {
		if (!CustomConfig.DISPUTE_ENABLED) {
			return;
		}

		broadcast(player);
	}

	private static class DisputeLandServiceHolder {

		private static final DisputeLandService INSTANCE = new DisputeLandService();
	}

}
