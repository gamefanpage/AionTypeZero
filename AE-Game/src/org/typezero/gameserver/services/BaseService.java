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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.base.BaseLocation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import org.typezero.gameserver.services.base.Base;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

import java.util.Map;

import javolution.util.FastMap;

/**
 *
 * @author Source
 */
public class BaseService {

	private static final Logger log = LoggerFactory.getLogger(BaseService.class);
	private final Map<Integer, Base<?>> active = new FastMap<Integer, Base<?>>().shared();
	private Map<Integer, BaseLocation> bases;

	public void initBaseLocations() {
		log.info("Initializing bases world and katalam...");
		bases = DataManager.BASE_DATA.getBaseLocations();
		log.info("Loaded " + bases.size() + " bases.");
	}

	public void initBases() {
		for (BaseLocation base : getBaseLocations().values()) {
			start(base.getId());
		}
	}

	public Map<Integer, BaseLocation> getBaseLocations() {
		return bases;
	}

	public BaseLocation getBaseLocation(int id) {
		return bases.get(id);
	}

	public void start(final int id) {
		final Base<?> base;

		synchronized (this) {
			if (active.containsKey(id)) {
				return;
			}
			base = new Base<BaseLocation>(getBaseLocation(id));
			active.put(id, base);
		}

		base.start();
	}

	public void stop(int id) {
		if (!isActive(id)) {
			log.info("Trying to stop not active base:" + id);
			return;
		}

		Base<?> base;
		synchronized (this) {
			base = active.remove(id);
		}

		if (base == null || base.isFinished()) {
			log.info("Trying to stop null or finished base:" + id);
			return;
		}

		base.stop();
		start(id);
	}

	public void capture(int id, Race race) {
		if (!isActive(id)) {
			log.info("Detecting not active base capture.");
			return;
		}

		getActiveBase(id).setRace(race);
		stop(id);
		broadcastUpdate(getBaseLocation(id));
	}

	public boolean isActive(int id) {
		return active.containsKey(id);
	}

	public Base<?> getActiveBase(int id) {
		return active.get(id);
	}

	public void onEnterBaseWorld(Player player) {
		for (BaseLocation baseLocation : getBaseLocations().values()) {
			if (baseLocation.getWorldId() == player.getWorldId() && isActive(baseLocation.getId())) {
				Base<?> base = getActiveBase(baseLocation.getId());
				PacketSendUtility.sendPacket(player, new SM_NPC_INFO(base.getFlag(), player));
			}
		}
	}

	public void broadcastUpdate(final BaseLocation baseLocation) {
		World.getInstance().getWorldMap(baseLocation.getWorldId()).getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				if (isActive(baseLocation.getId())) {
					Base<?> base = getActiveBase(baseLocation.getId());
					PacketSendUtility.sendPacket(player, new SM_NPC_INFO(base.getFlag(), player));
				}
			}

		});
	}

	public static BaseService getInstance() {
		return BaseServiceHolder.INSTANCE;
	}

	private static class BaseServiceHolder {

		private static final BaseService INSTANCE = new BaseService();
	}

}
