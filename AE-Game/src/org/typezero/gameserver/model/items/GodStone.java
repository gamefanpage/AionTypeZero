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

package org.typezero.gameserver.model.items;

import com.aionemu.commons.utils.Rnd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.EnchantsConfig;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.templates.item.GodstoneInfo;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class GodStone extends ItemStone {

	private static final Logger log = LoggerFactory.getLogger(GodStone.class);

	private final GodstoneInfo godstoneInfo;
	private ActionObserver actionListener;
	private final int probability;
	private final int probabilityLeft;
	private final ItemTemplate godItem;

	public GodStone(int itemObjId, int itemId, PersistentState persistentState) {
		super(itemObjId, itemId, 0, persistentState);
		ItemTemplate itemTemplate = DataManager.ITEM_DATA.getItemTemplate(itemId);
		godItem = itemTemplate;
		godstoneInfo = itemTemplate.getGodstoneInfo();

		if (godstoneInfo != null) {
			probability = godstoneInfo.getProbability();
			probabilityLeft = godstoneInfo.getProbabilityleft();
		}
		else {
			probability = 0;
			probabilityLeft = 0;
			log.warn("CHECKPOINT: Godstone info missing for item : " + itemId);
		}

	}

	/**
	 * @param player
	 */
	public void onEquip(final Player player) {
		if (godstoneInfo == null || godItem == null)
			return;

		final Item equippedItem = player.getEquipment().getEquippedItemByObjId(getItemObjId());
		final long equipmentSlot = equippedItem.getEquipmentSlot();
		int forcedManaStoneRate = player.getGameStats().getStat(StatEnum.PROC_REDUCE_RATE, 0).getCurrent();
		final int handProbability = (equipmentSlot == ItemSlot.MAIN_HAND.getSlotIdMask() ? probability : probabilityLeft) + forcedManaStoneRate;
		actionListener = new ActionObserver(ObserverType.ATTACK) {

			@Override
			public void attack(Creature creature) {
				if (handProbability > Rnd.get(0, 1000)) {
					Skill skill = SkillEngine.getInstance().getSkill(player, godstoneInfo.getSkillid(),
						godstoneInfo.getSkilllvl(), player.getTarget(), godItem);
					skill.setFirstTargetRangeCheck(false);
					if (skill.canUseSkill()) {
						Effect effect = new Effect(player, creature, skill.getSkillTemplate(), 1, 0, godItem);
						effect.initialize();
						effect.applyEffect();
						effect = null;
                        if (godItem.getCategory() == ItemCategory.MYSTIC_GODSTONE && Rnd.get(1, 1000) < EnchantsConfig.MYSTIC_GODSTONE_BREAK_CHANCE * 10f) {
                            equippedItem.removeGodStone();
                            equippedItem.setPersistentState(PersistentState.UPDATE_REQUIRED);
                            PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
                            ItemPacketService.updateItemAfterInfoChange(player, equippedItem);
                            PacketSendUtility.sendMessage(player, "Мистический божественный камень удален!");
                        }

					}
				}
			}
		};

		player.getObserveController().addObserver(actionListener);
	}

	/**
	 * @param player
	 */
	public void onUnEquip(Player player) {
		if (actionListener != null)
			player.getObserveController().removeObserver(actionListener);

	}
}
