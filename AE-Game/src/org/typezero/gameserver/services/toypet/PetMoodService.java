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
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class PetMoodService {

	private static final Logger log = LoggerFactory.getLogger(PetMoodService.class);

	public static void checkMood(Pet pet, int type, int shuggleEmotion) {
		switch (type) {
			case 0:
				startCheckingMood(pet);
				break;
			case 1:
				interactWithPet(pet, shuggleEmotion);
				break;
			case 3:
				requestPresent(pet);
				break;
		}
	}

	/**
	 * @param pet
	 */
	private static void requestPresent(Pet pet) {
		if (pet.getCommonData().getMoodPoints(false) < 9000) {
			log.warn("Requested present before mood fill up: {}", pet.getMaster().getName());
			return;
		}

		if (pet.getCommonData().getGiftRemainingTime() > 0) {
			AuditLogger.info(pet.getMaster(), "Trying to get gift during CD for pet " + pet.getPetId());
			return;
		}

		if (pet.getMaster().getInventory().isFull()) {
			PacketSendUtility.sendPacket(pet.getMaster(), SM_SYSTEM_MESSAGE.STR_WAREHOUSE_FULL_INVENTORY);
			return;
		}

		pet.getCommonData().clearMoodStatistics();
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0));
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 3, 0));
		int itemId = pet.getPetTemplate().getConditionReward();
		if (itemId != 0) {
			ItemService.addItem(pet.getMaster(), pet.getPetTemplate().getConditionReward(), 1);
		}
	}

	/**
	 * @param pet
	 * @param shuggleEmotion
	 */
	private static void interactWithPet(Pet pet, int shuggleEmotion) {
		if (pet.getCommonData() != null) {
			if (pet.getCommonData().increaseShuggleCounter()) {
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 2, shuggleEmotion));
				PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 4, 0)); // Update progress immediately
			}
		}
	}

	/**
	 * @param pet
	 */
	private static void startCheckingMood(Pet pet) {
		PacketSendUtility.sendPacket(pet.getMaster(), new SM_PET(pet, 0, 0));
	}

}
