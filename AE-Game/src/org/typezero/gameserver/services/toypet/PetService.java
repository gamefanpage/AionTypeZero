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

import java.util.Collection;
import java.util.List;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerPetsDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.Pet;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PetCommonData;
import org.typezero.gameserver.model.team2.common.legacy.LootRuleType;
import org.typezero.gameserver.model.templates.item.ItemUseLimits;
import org.typezero.gameserver.model.templates.item.actions.AbstractItemAction;
import org.typezero.gameserver.model.templates.item.actions.ItemActions;
import org.typezero.gameserver.model.templates.pet.FoodType;
import org.typezero.gameserver.model.templates.pet.PetFeedResult;
import org.typezero.gameserver.model.templates.pet.PetFlavour;
import org.typezero.gameserver.model.templates.pet.PetFunction;
import org.typezero.gameserver.model.templates.pet.PetFunctionType;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.services.item.ItemPacketService.ItemUpdateType;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author M@xx, IlBuono, xTz, Rolandas
 */
public class PetService {

	public static final PetService getInstance() {
		return SingletonHolder.instance;
	}

	private PetService() {
	}

	public void renamePet(Player player, String name) {
		Pet pet = player.getPet();
		if (pet != null) {
			pet.getCommonData().setName(name);
			DAOManager.getDAO(PlayerPetsDAO.class).updatePetName(pet.getCommonData());
			PacketSendUtility.broadcastPacket(player, new SM_PET(10, pet), true);
		}
	}

	public void onPlayerLogin(Player player) {
		Collection<PetCommonData> playerPets = player.getPetList().getPets();
		if (playerPets != null && playerPets.size() > 0)
			PacketSendUtility.sendPacket(player, new SM_PET(0, playerPets));
	}

	public void removeObject(int objectId, int count, int action, Player player) {
		Item item = player.getInventory().getItemByObjId(objectId);
		if (item == null || player.getPet() == null || count > item.getItemCount())
			return;

		Pet pet = player.getPet();
		pet.getCommonData().setCancelFeed(false);
		PacketSendUtility.sendPacket(player, new SM_PET(1, action, item.getObjectId(), count, pet));
		PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.START_FEEDING, 0, player.getObjectId()));

		schedule(pet, player, item, count, action);
	}

	private void schedule(final Pet pet, final Player player, final Item item, final int count, final int action) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!pet.getCommonData().getCancelFeed())
					checkFeeding(pet, player, item, count, action);
			}
		}, 2500);
	}

	private void checkFeeding(Pet pet, Player player, Item item, int count, int action) {
		PetCommonData commonData = pet.getCommonData();
		PetFeedProgress progress = commonData.getFeedProgress();

		if (!commonData.getCancelFeed()) {
			PetFunction func = pet.getPetTemplate().getPetFunction(PetFunctionType.FOOD);
			PetFlavour flavour = DataManager.PET_FEED_DATA.getFlavourById(func.getId());
			FoodType foodType = flavour.getFoodType(item.getItemId());
			PetFeedResult reward = null;

			if (flavour.isLovedFood(foodType, item.getItemId()) && progress.getLovedFoodRemaining() == 0)
				foodType = null;

			if (foodType != null) {
				player.getInventory().decreaseItemCount(item, 1, ItemUpdateType.DEC_PET_FOOD);
				reward = flavour.processFeedResult(progress, foodType, item.getItemTemplate().getLevel(), player
					.getCommonData().getLevel());
				if (progress.getHungryLevel() == PetHungryLevel.FULL && reward != null)
					PacketSendUtility.sendPacket(player, new SM_PET(2, action, item.getObjectId(), 0, pet));
				else
					PacketSendUtility.sendPacket(player, new SM_PET(2, action, item.getObjectId(), --count, pet));
			}
			else {
				// non eatable item
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_MSG_TOYPET_FEED_FOOD_NOT_LOVEFLAVOR(pet.getName(), item.getItemTemplate().getNameId()));
				return;
			}

			if (progress.getHungryLevel() == PetHungryLevel.FULL && reward != null) {
				PacketSendUtility.sendPacket(player, new SM_PET(6, action, reward.getItem(), 0, pet));
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
				PacketSendUtility.sendPacket(player, new SM_PET(7, action, 0, 0, pet)); // 2151591961

				ItemService.addItem(player, reward.getItem(), 1);
				commonData.scheduleRefeed(flavour.getCooldDown() * 60000);
				long refeedTime = System.currentTimeMillis() + flavour.getCooldDown() * 60000;
				commonData.setRefeedTime(refeedTime);
				DAOManager.getDAO(PlayerPetsDAO.class).setTime(player, pet.getPetId(), refeedTime);
				progress.reset();
			}
			else if (count > 0)
				schedule(pet, player, item, count, action);
			else {
				PacketSendUtility.sendPacket(player, new SM_PET(5, action, 0, 0, pet));
				PacketSendUtility.sendPacket(player, new SM_EMOTION(player, EmotionType.END_FEEDING, 0, player.getObjectId()));
			}
		}
	}

	/**
	 * Currently only scrolls are can be relocated
	 *
	 * @param player
	 * @param targetSlot
	 * @param destinationSlot
	 */
	public void relocateDoping(Player player, int targetSlot, int destinationSlot) {
		Pet pet = player.getPet();
		if (pet == null || pet.getCommonData().getDopingBag() == null)
			return;
		int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		int targetItem = scrollBag[targetSlot - 2];
		if (destinationSlot - 2 > scrollBag.length - 1) {
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, targetItem, destinationSlot));
			pet.getCommonData().getDopingBag().setItem(0, targetSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, 0, targetSlot));
		}
		else {
			pet.getCommonData().getDopingBag().setItem(scrollBag[destinationSlot - 2], targetSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, scrollBag[destinationSlot - 2], targetSlot));
			pet.getCommonData().getDopingBag().setItem(targetItem, destinationSlot);
			PacketSendUtility.sendPacket(player, new SM_PET(0, targetItem, destinationSlot));
		}
	}

	public void useDoping(final Player player, int action, int itemId, int slot) {
		Pet pet = player.getPet();
		if (pet == null || pet.getCommonData().getDopingBag() == null)
			return;

		if (action < 2) { // add, replace or delete item
			pet.getCommonData().getDopingBag().setItem(itemId, slot);
			action = 0;
		}
		else if (action == 3) { // use item
			List<Item> items = player.getInventory().getItemsByItemId(itemId);
			for (;;) {
				Item useItem = items.get(0);
				ItemActions itemActions = useItem.getItemTemplate().getActions();
				ItemUseLimits limit = new ItemUseLimits();
				int useDelay = player.getItemCooldown(useItem.getItemTemplate()) / 3;
				if (useDelay < 3000)
					useDelay = 3000;
				limit.setDelayId(useItem.getItemTemplate().getUseLimits().getDelayId());
				limit.setDelayTime(useDelay);

				if (player.isItemUseDisabled(limit)) {
					final int useAction = action;
					final int useItemId = itemId;
					final int useSlot = slot;
					// schedule re-check
					ThreadPoolManager.getInstance().schedule(new Runnable() {

						@Override
						public void run() {
							PacketSendUtility.sendPacket(player, new SM_PET(useAction, useItemId, useSlot));
						}
					}, useDelay);
					return;
				}
				if (!RestrictionsManager.canUseItem(player, useItem) || player.isProtectionActive()) {
					// client sends the correct restriction message with that
					player.addItemCoolDown(limit.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
					break;
				}
				player.getController().cancelCurrentSkill();
				for (AbstractItemAction itemAction : itemActions.getItemActions()) {
					if (itemAction.canAct(player, useItem, null))
						itemAction.act(player, useItem, null);
				}
				break;
			}
		}

		PacketSendUtility.sendPacket(player, new SM_PET(action, itemId, slot));

		itemId = pet.getCommonData().getDopingBag().getFoodItem();
		long totalDopes = player.getInventory().getItemCountByItemId(itemId);

		itemId = pet.getCommonData().getDopingBag().getDrinkItem();
		totalDopes += player.getInventory().getItemCountByItemId(itemId);

		int[] scrollBag = pet.getCommonData().getDopingBag().getScrollsUsed();
		for (int i = 0; i < scrollBag.length; i++) {
			if (scrollBag[i] != 0)
				totalDopes += player.getInventory().getItemCountByItemId(scrollBag[i]);
		}

		if (totalDopes == 0) {
			pet.getCommonData().setIsBuffing(false);
			PacketSendUtility.sendPacket(player, new SM_PET(1, false));
		}
	}

	public void activateLoot(Player player, boolean activate) {
		if (player.getPet() == null)
			return;

		if (activate) {
			if (player.isInTeam()) {
				LootRuleType lootType = player.getLootGroupRules().getLootRule();
				if (lootType == LootRuleType.FREEFORALL) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE03);
					return;
				}
			}
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE01);
		}
		player.getPet().getCommonData().setIsLooting(activate);
		PacketSendUtility.sendPacket(player, new SM_PET(activate));
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PetService instance = new PetService();
	}

}
