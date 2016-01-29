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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.templates.windstreams.Location2D;
import org.typezero.gameserver.model.templates.windstreams.WindstreamTemplate;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.BaseService;
import org.typezero.gameserver.services.SerialKillerService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.TownService;
import org.typezero.gameserver.services.WeatherService;
import org.typezero.gameserver.services.rift.RiftInformer;
import org.typezero.gameserver.spawnengine.InstanceRiftSpawnManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapType;

/**
 * Client is saying that level[map] is ready.
 *
 * @author -Nemesiss-
 * @author Kwazar
 */
public class CM_LEVEL_READY extends AionClientPacket {

	public CM_LEVEL_READY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		if (activePlayer.getHouseRegistry() != null)
			sendPacket(new SM_HOUSE_OBJECTS(activePlayer));
		if (activePlayer.isInInstance()) {
			sendPacket(new SM_INSTANCE_COUNT_INFO(activePlayer.getWorldId(), activePlayer.getInstanceId()));
		}
        if (activePlayer.getItemEffectId() <= 1)
            activePlayer.updataItemEffectId();
		sendPacket(new SM_PLAYER_INFO(activePlayer, false));
		activePlayer.getController().startProtectionActiveTask();
		sendPacket(new SM_MOTION(activePlayer.getObjectId(), activePlayer.getMotions().getActiveMotions()));

		WindstreamTemplate template = DataManager.WINDSTREAM_DATA.getStreamTemplate(activePlayer.getPosition().getMapId());
		Location2D location;
		if (template != null)
			for (int i = 0; i < template.getLocations().getLocation().size(); i++) {
				location = template.getLocations().getLocation().get(i);
				sendPacket(new SM_WINDSTREAM_ANNOUNCE(location.getFlyPathType().getId(), template.getMapid(), location.getId(),
						location.getState()));
			}
		location = null;
		template = null;

		/**
		 * Spawn player into the world.
		 */
		// If already spawned, despawn before spawning into the world
		if (activePlayer.isSpawned())
			World.getInstance().despawn(activePlayer);
		World.getInstance().spawn(activePlayer);

		activePlayer.getController().refreshZoneImpl();

        // because fortress buff should only be avaible in fortress 5011-6011-6021
        SiegeService.getInstance().fortressBuffRemove(activePlayer);

		// SM_SHIELD_EFFECT, SM_ABYSS_ARTIFACT_INFO3
		if (activePlayer.isInSiegeWorld()) {
			SiegeService.getInstance().onEnterSiegeWorld(activePlayer);
		}

		// SM_NPC_INFO
		BaseService.getInstance().onEnterBaseWorld(activePlayer);

		// SM_SERIAL_KILLER
		SerialKillerService.getInstance().onEnterMap(activePlayer);

		// SM_RIFT_ANNOUNCE
		//RiftInformer.sendRiftsInfo(activePlayer);
		InstanceRiftSpawnManager.sendInstanceRiftStatus(activePlayer);

		// SM_UPGRADE_ARCADE
		//sendPacket(new SM_UPGRADE_ARCADE(true));

		// SM_NEARBY_QUESTS
		activePlayer.getController().updateNearbyQuests();

		/**
		 * Loading weather for the player's region
		 */
		WeatherService.getInstance().loadWeather(activePlayer);

		QuestEngine.getInstance().onEnterWorld(new QuestEnv(null, activePlayer, 0, 0));

		activePlayer.getController().onEnterWorld();

		// zone channel message (is this must be here?)
		if (!WorldMapType.getWorld(activePlayer.getWorldId()).isPersonal())
			sendPacket(new SM_SYSTEM_MESSAGE(1390122, activePlayer.getPosition().getInstanceId()));



		activePlayer.getEffectController().updatePlayerEffectIcons();
		sendPacket(SM_CUBE_UPDATE.cubeSize(StorageType.CUBE, activePlayer));

		Pet pet = activePlayer.getPet();
		if (pet != null && !pet.isSpawned())
			World.getInstance().spawn(pet);
		activePlayer.setPortAnimation(0);

		TownService.getInstance().onEnterWorld(activePlayer);
	}

}
