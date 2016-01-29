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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class PetAdoptionService {

	private static final Logger log = LoggerFactory.getLogger(PetAdoptionService.class);

	/**
	 * Create a pet for player (with validation)
	 *
	 * @param player
	 * @param eggObjId
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static void adoptPet(Player player, int eggObjId, int petId, String name, int decorationId) {

		int eggId = player.getInventory().getItemByObjId(eggObjId).getItemId();
		ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(eggId);

		if (!validateAdoption(player, template, petId))
		   return;

		if(!player.getInventory().decreaseByObjectId(eggObjId, 1))
		   return;

		int expireTime = template.getActions().getAdoptPetAction().getExpireMinutes() != 0 ?
				(int) ((System.currentTimeMillis() / 1000) + template.getActions().getAdoptPetAction().getExpireMinutes() * 60) : 0;

		addPet(player, petId, name, decorationId, expireTime);
	}

	/**
	 * Add pet to player
	 *
	 * @param player
	 * @param petId
	 * @param name
	 * @param decorationId
	 */
	public static void addPet(Player player, int petId, String name, int decorationId, int expireTime) {
	   PetCommonData petCommonData = player.getPetList().addPet(player, petId, decorationId, name, expireTime);
		if (petCommonData != null) {
			PacketSendUtility.sendPacket(player, new SM_PET(1, petCommonData));
			if (expireTime > 0)
			   ExpireTimerTask.getInstance().addTask(petCommonData, player);
		}
	}

	private static boolean validateAdoption(Player player, ItemTemplate template, int petId) {
		if (template == null || template.getActions() == null || template.getActions().getAdoptPetAction() == null ||
			template.getActions().getAdoptPetAction().getPetId() != petId) {
			return false;
		}
		if (player.getPetList().hasPet(petId)) {
			log.warn("Duplicate pet adoption");
			return false;
		}
        if (DataManager.PET_DATA.getPetTemplate(petId) == null) {
          log.warn("Trying adopt pet without template. PetId:" + petId);
          return false;
        }
		return true;
	}

	/**
	 * Delete pet
	 *
	 * @param player
	 * @param petId
	 */
	public static void surrenderPet(Player player, int petId) {
		PetCommonData petCommonData = player.getPetList().getPet(petId);
		if (player.getPet() != null && player.getPet().getPetId() == petCommonData.getPetId()) {
			if (petCommonData.getFeedProgress() != null)
				petCommonData.setCancelFeed(true);
			PetSpawnService.dismissPet(player, false);
		}
		player.getPetList().deletePet(petCommonData.getPetId());
		PacketSendUtility.sendPacket(player, new SM_PET(2, petCommonData));
	}

}
