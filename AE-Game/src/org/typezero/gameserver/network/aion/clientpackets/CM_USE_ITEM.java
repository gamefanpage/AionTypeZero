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

import java.util.ArrayList;

import org.typezero.gameserver.model.templates.item.ItemUseLimits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.actions.AbstractItemAction;
import org.typezero.gameserver.model.templates.item.actions.IHouseObjectDyeAction;
import org.typezero.gameserver.model.templates.item.actions.ItemActions;
import org.typezero.gameserver.model.templates.item.actions.MultiReturnAction;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.HandlerResult;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 */
public class CM_USE_ITEM extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_USE_ITEM.class);

	public int uniqueItemId;
	public int type, targetItemId, returnId;

	public CM_USE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		uniqueItemId = readD();
		type = readC();
		if (type == 2) {
			targetItemId = readD();
		}
        if (type == 6) {
            returnId = readD();
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();

		if (player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}

		Item item = player.getInventory().getItemByObjId(uniqueItemId);
		Item targetItem = player.getInventory().getItemByObjId(targetItemId);
		HouseObject<?> targetHouseObject = null;

		if (item == null) {
			log.warn(String.format("CHECKPOINT: null item use action: %d %d", player.getObjectId(), uniqueItemId));
			return;
		}

		if (targetItem == null)
			targetItem = player.getEquipment().getEquippedItemByObjId(targetItemId);
		if (targetItem == null && player.getHouseRegistry() != null)
			targetHouseObject = player.getHouseRegistry().getObjectByObjId(targetItemId);

		if (item.getItemTemplate().getTemplateId() == 165000001 && targetItem.getItemTemplate().canExtract()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return;
		}


		// check use item multicast delay exploit cast (spam)
		if (player.isCasting()) {
			// PacketSendUtility.sendMessage(this.getOwner(),
			// "You must wait until cast time finished to use skill again.");
			player.getController().cancelCurrentSkill();
			// On retail the item is cancelling the current skill and then procs normally
			// return;
		}

		if (!RestrictionsManager.canUseItem(player, item))
			return;

		if (item.getItemTemplate().getRace() != Race.PC_ALL && item.getItemTemplate().getRace() != player.getRace()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_RACE);
			return;
		}

		int requiredLevel = item.getItemTemplate().getRequiredLevel(player.getCommonData().getPlayerClass());
		if (requiredLevel == -1) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_CLASS);
			return;
		}

		if (requiredLevel > player.getLevel()) {
			PacketSendUtility.sendPacket(player,
				SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_TOO_LOW_LEVEL_MUST_BE_THIS_LEVEL(item.getNameId(), requiredLevel));
			return;
		}

		HandlerResult result = QuestEngine.getInstance().onItemUseEvent(new QuestEnv(null, player, 0, 0), item);
		if (result == HandlerResult.FAILED)
			return; // don't remove item

		ItemActions itemActions = item.getItemTemplate().getActions();
		ArrayList<AbstractItemAction> actions = new ArrayList<AbstractItemAction>();

		if (itemActions == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_IS_NOT_USABLE);
			return;
		}

		for (AbstractItemAction itemAction : itemActions.getItemActions()) {
			// check if the item can be used before placing it on the cooldown list.
			if (targetHouseObject != null && itemAction instanceof IHouseObjectDyeAction) {
				IHouseObjectDyeAction action = (IHouseObjectDyeAction) itemAction;
				if (action != null && action.canAct(player, item, targetHouseObject))
					actions.add(itemAction);
			}
			else if (itemAction.canAct(player, item, targetItem))
				actions.add(itemAction);
		}

		if (actions.size() == 0) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_IS_NOT_USABLE);
			return;
		}

		// Store Item CD in server Player variable.
		// Prevents potion spamming, and relogging to use kisks/aether jelly/long CD items.
		ItemUseLimits limits = item.getItemTemplate().getUseLimits();
		if (player.isItemUseDisabled(limits)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANT_USE_UNTIL_DELAY_TIME);
			return;
		}

		if (limits.getGenderPermitted() != null && limits.getGenderPermitted() != player.getGender()) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_ITEM_INVALID_GENDER);
			return;
		}
		int useDelay = player.getItemCooldown(item.getItemTemplate());
		if (useDelay > 0) {
			player.addItemCoolDown(item.getItemTemplate().getUseLimits().getDelayId(), System.currentTimeMillis() + useDelay,
				useDelay / 1000);
		}

		// notify item use observer
		player.getObserveController().notifyItemuseObservers(item);

		for (AbstractItemAction itemAction : actions) {
			if (targetHouseObject != null && itemAction instanceof IHouseObjectDyeAction) {
				IHouseObjectDyeAction action = (IHouseObjectDyeAction) itemAction;
                action.act(player, item, targetHouseObject);
            } else if (type == 6) {

                // Multi Returns Items (Scroll Teleporter)
            	if (itemAction instanceof MultiReturnAction){
            		MultiReturnAction action = (MultiReturnAction) itemAction;
            		int SelectedMapIndex = returnId;
            		action.act(player, item, SelectedMapIndex);
            	}
            } else {
                itemAction.act(player, item, targetItem);
            }
		}
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(item.getItemTemplate().getNameId())));
	}
}
