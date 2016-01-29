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

package org.typezero.gameserver.model.templates.item.actions;

import java.util.Iterator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.EnchantService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;

/**
 * @author Nemiroff, Wakizashi, vlog
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnchantItemAction")
public class EnchantItemAction extends AbstractItemAction {

	// Count of required supplements
	@XmlAttribute(name = "count")
	private int count;

	// Min level of enchantable item
	@XmlAttribute(name = "min_level")
	private Integer min_level;

	// Max level of enchantable item
	@XmlAttribute(name = "max_level")
	private Integer max_level;

	@XmlAttribute(name = "manastone_only")
	private boolean manastone_only;

	@XmlAttribute(name = "chance")
	private float chance;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (isSupplementAction())
			return false;
		if (targetItem == null) { // no item selected.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (parentItem == null) {
			return false;
		}
		int msID = parentItem.getItemTemplate().getTemplateId() / 1000000;
		int tID = targetItem.getItemTemplate().getTemplateId() / 1000000;
        if (msID == tID && tID == 140) // Stigma Enchant
            return true;
		if ((msID != 167 && msID != 166) || tID >= 120) {
			return false;
		}
		return true;
	}

	@Override
	public void act(final Player player, final Item parentItem, final Item targetItem) {
		act(player, parentItem, targetItem, null, 1);
	}

	// necessary overloading to not change AbstractItemAction
	public void act(final Player player, final Item parentItem, final Item targetItem, final Item supplementItem,
		final int targetWeapon) {

        if ((parentItem.getItemTemplate().getCategory() != ItemCategory.ENCHANTMENT) &&
                (parentItem.getItemTemplate().getCategory() == ItemCategory.ANCIENT_MANASTONE)) {
            if (targetWeapon == 1)
            {
                if (!targetItem.SpecialIsFull(false)) {}
            }
            else if (targetItem.SpecialIsFull(true)) {
                return;
            }
        }
		if (supplementItem != null
			&& !checkSupplementLevel(player, supplementItem.getItemTemplate(), targetItem.getItemTemplate()))
			return;
		// Current enchant level
		final int currentEnchant = targetItem.getEnchantLevel();
		final boolean isSuccess = isSuccess(player, parentItem, targetItem, supplementItem, targetWeapon);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacketAndReceive(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate()
				.getTemplateId(), 1500, 0, 0));
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				// Item template
				ItemTemplate itemTemplate = parentItem.getItemTemplate();
				// Enchantment stone
				if (itemTemplate.getCategory() == ItemCategory.ENCHANTMENT)
					EnchantService.enchantItemAct(player, parentItem, targetItem, supplementItem, currentEnchant, isSuccess);
				// Stigma
                else if (itemTemplate.getCategory() == ItemCategory.STIGMA && parentItem.getItemTemplate().getCategory() == targetItem.getItemTemplate().getCategory())
                    EnchantService.enchantStigmaAct(player, parentItem, targetItem, currentEnchant, isSuccess);
				// Manastone
				else
					EnchantService.socketManastoneAct(player, parentItem, targetItem, supplementItem, targetWeapon, isSuccess);

				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(),
					parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, isSuccess ? 1 : 2, 0));
				if (CustomConfig.ENABLE_ENCHANT_ANNOUNCE) {
					if ((itemTemplate.getCategory() == ItemCategory.ENCHANTMENT) && targetItem.getEnchantLevel() == 15 && isSuccess) {
						Iterator<Player> iter = World.getInstance().getPlayersIterator();
						while (iter.hasNext()) {
							Player player2 = iter.next();
							if (player2.getRace() == player.getRace()) {
								PacketSendUtility.sendPacket(player2, SM_SYSTEM_MESSAGE.STR_MSG_ENCHANT_ITEM_SUCCEEDED_15(
									player.getName(), targetItem.getItemTemplate().getNameId()));
							}
						}
					}
				}
			}

		}, 1500));
	}

	/**
	 * Check, if the item enchant will be successful
	 *
	 * @param player
	 * @param parentItem
	 *          the enchantment-/manastone to insert
	 * @param targetItem
	 *          the current item to enchant
	 * @param supplementItem
	 *          the item to increase the enchant chance (if exists)
	 * @param targetWeapon
	 *          the fused weapon (if exists)
	 * @param currentEnchant
	 *          current enchant level
	 * @return true if successful
	 */
	private boolean isSuccess(final Player player, final Item parentItem, final Item targetItem,
		final Item supplementItem, final int targetWeapon) {
		if (parentItem.getItemTemplate() != null) {
			// Item template
			ItemTemplate itemTemplate = parentItem.getItemTemplate();
			// Enchantment stone
			if (itemTemplate.getCategory() == ItemCategory.ENCHANTMENT ||
                    (parentItem.getItemTemplate().getCategory() == targetItem.getItemTemplate().getCategory() && itemTemplate.getCategory() == ItemCategory.STIGMA)) {
				return EnchantService.enchantItem(player, parentItem, targetItem, supplementItem);
			}
			// Manastone
			return EnchantService.socketManastone(player, parentItem, targetItem, supplementItem, targetWeapon);
		}
		return false;
	}

	public int getCount() {
		return count;
	}

	public int getMaxLevel() {
		return max_level != null ? max_level : 0;
	}

	public int getMinLevel() {
		return min_level != null ? min_level : 0;
	}

	public boolean isManastoneOnly() {
		return manastone_only;
	}

	public float getChance() {
		return chance;
	}

	boolean isSupplementAction() {
		return getMinLevel() > 0 || getMaxLevel() > 0 || getChance() > 0 || isManastoneOnly();
	}

	private boolean checkSupplementLevel(final Player player, final ItemTemplate supplementTemplate,
		final ItemTemplate targetItemTemplate) {
		// Is item manastone? True - check if player can use supplement
		if (supplementTemplate.getCategory() != ItemCategory.ENCHANTMENT) {
			// Check if max item level is ok for the enchant
			int minEnchantLevel = targetItemTemplate.getLevel();
			int maxEnchantLevel = targetItemTemplate.getLevel();

			EnchantItemAction action = supplementTemplate.getActions().getEnchantAction();
			if (action != null) {
				if (action.getMinLevel() != 0)
					minEnchantLevel = action.getMinLevel();
				if (action.getMaxLevel() != 0)
					maxEnchantLevel = action.getMaxLevel();
			}

			if (minEnchantLevel <= targetItemTemplate.getLevel() && maxEnchantLevel >= targetItemTemplate.getLevel())
				return true;

			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_ENCHANT_ASSISTANT_NO_RIGHT_ITEM);
			return false;
		}
		return true;
	}

}
