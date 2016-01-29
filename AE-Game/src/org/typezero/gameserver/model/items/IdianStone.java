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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.dao.ItemStoneListDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.bonuses.StatBonusType;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
public class IdianStone extends ItemStone {

	private ActionObserver actionListener;
	private int polishCharge;
	private final int polishSetId;
	private final int polishNumber;
	private final Item item;
	private final ItemTemplate template;
	private final int burnDefend;
	private final int burnAttack;
	private final RandomBonusEffect rndBonusEffect;

	public IdianStone(int itemId, PersistentState persistentState, Item item, int polishNumber, int polishCharge) {
		super(item.getObjectId(), itemId, 0, persistentState);
		this.item = item;
		burnDefend = item.getItemTemplate().getIdianAction().getBurnDefend();
		burnAttack = item.getItemTemplate().getIdianAction().getBurnAttack();
		this.polishCharge = polishCharge;
		this.template = DataManager.ITEM_DATA.getItemTemplate(itemId);
		this.polishNumber = polishNumber;
		polishSetId = template.getActions().getPolishAction().getPolishSetId();
		rndBonusEffect = new RandomBonusEffect(StatBonusType.POLISH, polishSetId, polishNumber);
	}

	public void onEquip(final Player player) {
		if (polishCharge > 0) {
			actionListener = new ActionObserver(ObserverType.ALL) {
				@Override
				public void attacked(Creature creature) {
					decreasePolishCharge(player, true);
				}

				@Override
				public void attack(Creature creature) {
					decreasePolishCharge(player, false);
				}

			};
			player.getObserveController().addObserver(actionListener);
			rndBonusEffect.applyEffect(player);
		}
	}
	
	private synchronized void decreasePolishCharge(Player player, boolean isAttacked) {
		decreasePolishCharge(player, isAttacked, 0);
	}
	
	public synchronized void decreasePolishCharge(Player player, int skillValue) {
		decreasePolishCharge(player, false, skillValue);
	}

	private synchronized void decreasePolishCharge(Player player, boolean isAttacked, int skillValue) {
		int result = 0;
		if (polishCharge <= 0) {
			return;
		}
		if (skillValue == 0)
		result = isAttacked ? burnDefend : burnAttack;
		else
			result = skillValue;
		if (polishCharge - result < 0) {
			polishCharge = 0;
		}
		else {
			polishCharge -= result;
		}
		if (polishCharge == 0) {
			onUnEquip(player);
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401652, new DescriptionId(item.getNameId())));
			item.setIdianStone(null);
			setPersistentState(PersistentState.DELETED);
			DAOManager.getDAO(ItemStoneListDAO.class).storeIdianStones(this);
		}
	}

	public int getPolishNumber() {
		return polishNumber;
	}

	public int getPolishSetId() {
		return polishSetId;
	}

	public int getPolishCharge() {
		return polishCharge;
	}

	public void onUnEquip(Player player) {
		if (actionListener != null) {
			rndBonusEffect.endEffect(player);
			player.getObserveController().removeObserver(actionListener);
			actionListener = null;
		}
	}

}
