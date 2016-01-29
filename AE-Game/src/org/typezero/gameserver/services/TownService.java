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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.TownDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.HouseAddress;
import org.typezero.gameserver.model.templates.housing.HousingLand;
import org.typezero.gameserver.model.town.Town;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOWNS_LIST;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.MapRegion;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author ViAl
 */
public class TownService {

	private static final Logger log = LoggerFactory.getLogger(TownService.class);
	private Map<Integer, Town> elyosTowns;
	private Map<Integer, Town> asmosTowns;

	private static class SingletonHolder {

		protected static final TownService instance = new TownService();
	}

	public static final TownService getInstance() {
		return SingletonHolder.instance;
	}

	private TownService() {
		elyosTowns = DAOManager.getDAO(TownDAO.class).load(Race.ELYOS);
		asmosTowns = DAOManager.getDAO(TownDAO.class).load(Race.ASMODIANS);
		if (elyosTowns.size() == 0 && asmosTowns.size() == 0) {
			for (HousingLand land : DataManager.HOUSE_DATA.getLands()) {
				for (HouseAddress address : land.getAddresses()) {
					if (address.getTownId() == 0)
						continue;
					else {
						Race townRace = DataManager.NPC_DATA.getNpcTemplate(land.getManagerNpcId()).getTribe() == TribeClass.GENERAL ? Race.ELYOS
							: Race.ASMODIANS;
						if ((townRace == Race.ELYOS && !elyosTowns.containsKey(address.getTownId()))
							|| (townRace == Race.ASMODIANS && !asmosTowns.containsKey(address.getTownId()))) {
							Town town = new Town(address.getTownId(), townRace);
							if (townRace == Race.ELYOS)
								elyosTowns.put(town.getId(), town);
							else
								asmosTowns.put(town.getId(), town);
							DAOManager.getDAO(TownDAO.class).store(town);
						}

					}
				}
			}
		}
		log.info("Loaded " + asmosTowns.size() + " elyos towns.");
		log.info("Loaded " + asmosTowns.size() + " asmodians towns.");
	}

	public Town getTownById(int townId) {
		if (elyosTowns.containsKey(townId))
			return elyosTowns.get(townId);
		else
			return asmosTowns.get(townId);
	}

	public int getTownResidence(Player player) {
		House house = player.getActiveHouse();
		if (house == null)
			return 0;
		else
			return house.getAddress().getTownId();
	}

	public int getTownIdByPosition(Creature creature) {
		if(creature instanceof Npc) {
			if(((Npc)creature).getTownId() != 0)
				return ((Npc)creature).getTownId();
		}
		int townId = 0;
		MapRegion region = creature.getPosition().getMapRegion();
		if (region == null) {
			log.warn("TownService: npc " + creature.getName() + " haven't any map region!");
			return 0;
		}
		else {
			List<ZoneInstance> zones = region.getZones(creature);
			for (ZoneInstance zone : zones) {
				townId = zone.getTownId();
				if (townId > 0)
					break;
			}
		}
		return townId;
	}

	public void onEnterWorld(Player player) {
		switch (player.getRace()) {
			case ELYOS:
				if(player.getWorldId() == 700010000)
					PacketSendUtility.sendPacket(player, new SM_TOWNS_LIST(elyosTowns));
				break;
			case ASMODIANS:
				if(player.getWorldId() == 710010000)
					PacketSendUtility.sendPacket(player, new SM_TOWNS_LIST(asmosTowns));
				break;
		}
	}

}
