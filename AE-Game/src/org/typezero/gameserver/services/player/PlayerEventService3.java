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

import org.typezero.gameserver.configs.main.EventsConfig;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Romanz
 */
public class PlayerEventService3 {

	private static final Logger log = LoggerFactory.getLogger(PlayerEventService.class);

	private PlayerEventService3() {

		final EventCollector visitor = new EventCollector();
		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				World.getInstance().doOnAllPlayers(visitor);
			}

		}, EventsConfig.EVENT_PERIOD3 * 60000, EventsConfig.EVENT_PERIOD3 * 60000);
	}

	private static final class EventCollector implements Visitor<Player> {

		@Override
		public void visit(Player player) {
			int membership = player.getClientConnection().getAccount().getMembership();
			int rate = EventsConfig.EVENT_REWARD_MEMBERSHIP_RATE ? membership + 1 : 1;
			int level = player.getLevel();
			if (membership >= EventsConfig.EVENT_REWARD_MEMBERSHIP2 && level >= EventsConfig.EVENT_REWARD_LEVEL3) {
				try {
					if (player.getInventory().isFull())
						log.warn("[EventReward] player " + player.getName() + " tried to receive item with full inventory.");
					else
						ItemService.addItem(player, ( player.getRace() == Race.ELYOS ? EventsConfig.EVENT_ITEM_ELYOS3 : EventsConfig.EVENT_ITEM_ASMO3 ), EventsConfig.EVENT_ITEM_COUNT3 * rate );
				}
				catch (Exception ex) {
					log.error("Exception during event rewarding of player " + player.getName(), ex);
				}
			}
		}

	};

	public static PlayerEventService3 getInstance() {
		return SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final PlayerEventService3 instance = new PlayerEventService3();
	}

}
