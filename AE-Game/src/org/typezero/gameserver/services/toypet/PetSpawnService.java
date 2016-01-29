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

package org.typezero.gameserver.services.toypet;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.PeriodicSaveConfig;
import org.typezero.gameserver.controllers.PetController;
import org.typezero.gameserver.dao.PlayerPetsDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.templates.pet.PetDopingBag;
import org.typezero.gameserver.model.templates.pet.PetFunction;
import org.typezero.gameserver.model.templates.pet.PetTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_WAREHOUSE_INFO;
import org.typezero.gameserver.spawnengine.VisibleObjectSpawner;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.sql.Timestamp;

/**
 * @author ATracer
 */
public class PetSpawnService {

	/**
	 * @param player
	 * @param petId
	 */
	public static final void summonPet(Player player, int petId, boolean isManualSpawn) {
		PetCommonData lastPetCommonData;

		if (player.getPet() != null) {
			if (player.getPet().getPetId() == petId) {
				PacketSendUtility.broadcastPacket(player, new SM_PET(3, player.getPet()), true);
				return;
			}

			lastPetCommonData = player.getPet().getCommonData();
			dismissPet(player, isManualSpawn);
		}
		else {
			lastPetCommonData = player.getPetList().getLastUsedPet();
		}

		if (lastPetCommonData != null) {
			// reset mood if other pet is spawned
			if (petId != lastPetCommonData.getPetId())
				lastPetCommonData.clearMoodStatistics();
		}

		player.getController().addTask(
			TaskId.PET_UPDATE,
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new PetController.PetUpdateTask(player),
				PeriodicSaveConfig.PLAYER_PETS * 1000, PeriodicSaveConfig.PLAYER_PETS * 1000));

		Pet pet = VisibleObjectSpawner.spawnPet(player, petId);
		// It means serious error or cheater - why its just nothing say "null"?
		if (pet != null) {
			sendWhInfo(player, petId);

			if (System.currentTimeMillis() - pet.getCommonData().getDespawnTime().getTime() > 10 * 60 * 1000) {
				// reset mood if pet was despawned for longer than 10 mins.
				player.getPet().getCommonData().clearMoodStatistics();
			}

			lastPetCommonData = pet.getCommonData();
			player.getPetList().setLastUsedPetId(petId);
		}
	}

	/**
	 * @param player
	 * @param petId
	 */
	private static void sendWhInfo(Player player, int petId) {
		PetTemplate petTemplate = DataManager.PET_DATA.getPetTemplate(petId);
		PetFunction pf = petTemplate.getWarehouseFunction();
		if (pf != null && pf.getSlots() != 0) {
			int itemLocation = StorageType.getStorageId(pf.getSlots(), 6);
			if (itemLocation != -1) {
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(player.getStorage(itemLocation).getItemsWithKinah(),
					itemLocation, 0, true, player));
				PacketSendUtility.sendPacket(player, new SM_WAREHOUSE_INFO(null, itemLocation, 0, false, player));
			}
		}
	}

	/**
	 * @param player
	 * @param isManualDespawn
	 */
	public static final void dismissPet(Player player, boolean isManualDespawn) {
		Pet toyPet = player.getPet();
		if (toyPet != null) {
			PetFeedProgress progress = toyPet.getCommonData().getFeedProgress();
			if (progress != null) {
				toyPet.getCommonData().setCancelFeed(true);
				DAOManager.getDAO(PlayerPetsDAO.class).saveFeedStatus(player, toyPet.getPetId(),
					progress.getHungryLevel().getValue(), progress.getDataForPacket(),
					toyPet.getCommonData().getRefeedTime());
			}
			PetDopingBag bag = toyPet.getCommonData().getDopingBag();
			if (bag != null && bag.isDirty())
				DAOManager.getDAO(PlayerPetsDAO.class).saveDopingBag(player, toyPet.getPetId(), bag);

			player.getController().cancelTask(TaskId.PET_UPDATE);

			// TODO needs for pet teleportation
			if (isManualDespawn)
				toyPet.getCommonData().setDespawnTime(new Timestamp(System.currentTimeMillis()));

			toyPet.getCommonData().savePetMoodData();

			player.setToyPet(null);
			toyPet.getController().delete();
		}

	}
}
