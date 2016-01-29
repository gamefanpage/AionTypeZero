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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.PetAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.NameRestrictionService;
import org.typezero.gameserver.services.toypet.PetAdoptionService;
import org.typezero.gameserver.services.toypet.PetMoodService;
import org.typezero.gameserver.services.toypet.PetService;
import org.typezero.gameserver.services.toypet.PetSpawnService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author M@xx, xTz
 */
public class CM_PET extends AionClientPacket {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(CM_PET.class);

	private int actionId;
	private PetAction action;
	private int petId;
	private String petName;
	private int decorationId;
	private int eggObjId;
	private int objectId;
	private int count;
	private int subType;
	private int emotionId;
	private int actionType;
	private int dopingItemId;
	private int dopingAction;
	private int dopingSlot1;
	private int dopingSlot2;
	private int activateLoot;

	@SuppressWarnings("unused")
	private int unk2;
	@SuppressWarnings("unused")
	private int unk3;
	@SuppressWarnings("unused")
	private int unk5;
	@SuppressWarnings("unused")
	private int unk6;

	public CM_PET(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readH();
		action = PetAction.getActionById(actionId);
		switch (action) {
			case ADOPT:
				eggObjId = readD();
				petId = readD();
				unk2 = readC();
				unk3 = readD();
				decorationId = readD();
				unk5 = readD();
				unk6 = readD();
				petName = readS();
				break;
			case SURRENDER:
			case SPAWN:
			case DISMISS:
				petId = readD();
				break;
			case FOOD:
				actionType = readD();
				if (actionType == 3)
					activateLoot = readD();
				else if (actionType == 2) {
					dopingAction = readD();
					if (dopingAction == 0) { // add item
						dopingItemId = readD();
						dopingSlot1 = readD();
					}
					else if (dopingAction == 1) { // remove item
						dopingSlot1 = readD();
						dopingItemId = readD();
					}
					else if (dopingAction == 2) { // move item
						dopingSlot1 = readD();
						dopingSlot2 = readD();
					}
					else if (dopingAction == 3) { // use doping
						dopingItemId = readD();
						dopingSlot1 = readD();
					}
				}
				else {
					objectId = readD();
					count = readD();
					unk2 = readD();
				}
				break;
			case RENAME:
				petId = readD();
				petName = readS();
				break;
			case MOOD:
				subType = readD();
				emotionId = readD();
				break;
			default:
				break;
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null) {
			return;
		}
		Pet pet = player.getPet();
		switch (action) {
			case ADOPT:
				if (NameRestrictionService.isForbiddenWord(petName)) {
					PacketSendUtility.sendMessage(player, "You are trying to use a forbidden name. Choose another one!");
				}
				else {
					PetAdoptionService.adoptPet(player, eggObjId, petId, petName, decorationId);
				}
				break;
			case SURRENDER:
				PetAdoptionService.surrenderPet(player, petId);
				break;
			case SPAWN:
				PetSpawnService.summonPet(player, petId, true);
				break;
			case DISMISS:
				PetSpawnService.dismissPet(player, true);
				break;
			case FOOD:
				if (actionType == 2) {
					// Pet doping
					if (dopingAction == 2)
						PetService.getInstance().relocateDoping(player, dopingSlot1, dopingSlot2);
					else
						PetService.getInstance().useDoping(player, dopingAction, dopingItemId, dopingSlot1);
				}
				else if (actionType == 3) {
					// Pet looting
					PetService.getInstance().activateLoot(player, activateLoot != 0);
				}
				else if (pet != null) {
					if (objectId == 0) {
						pet.getCommonData().setCancelFeed(true);
						PacketSendUtility.sendPacket(player, new SM_PET(4, actionId, 0, 0, player.getPet()));
						PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
					}
					else if (!pet.getCommonData().isFeedingTime()) {
						PacketSendUtility.sendPacket(player, new SM_PET(8, actionId, objectId, count, player.getPet()));
					}
					else
						PetService.getInstance().removeObject(objectId, count, actionId, player);
				}
				break;
			case RENAME:
				/*if (NameRestrictionService.isForbiddenWord(petName)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PET_NOT_AVALIABE_NAME);
				}
				else {
					PetService.getInstance().renamePet(player, petName);
				}*/
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_ALL_CANT_USE);
				break;
			case MOOD:
				if (pet != null
					&& (subType == 0 && pet.getCommonData().getMoodRemainingTime() == 0
						|| (subType == 3 && pet.getCommonData().getGiftRemainingTime() == 0) || emotionId != 0)) {
					PetMoodService.checkMood(pet, subType, emotionId);
				}
			default:
				break;
		}
	}

}
