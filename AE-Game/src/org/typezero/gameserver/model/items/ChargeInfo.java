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

import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class ChargeInfo extends ActionObserver {

	public static final int LEVEL2 = 1000000;
	public static final int LEVEL1 = 500000;

	private int chargePoints;
	private final int attackBurn;
	private final int defendBurn;
	private final Item item;
	private Player player;

	/**
	 * @param chargePoints
	 */
	public ChargeInfo(int chargePoints, Item item) {
		super(ObserverType.ATTACK_DEFEND);
		this.chargePoints = chargePoints;
		this.item = item;
		if (item.getImprovement() != null) {
			this.attackBurn = item.getImprovement().getBurnAttack();
			this.defendBurn = item.getImprovement().getBurnDefend();
		} else {
			this.attackBurn = 0;
			this.defendBurn = 0;
		}
	}

	public int getChargePoints() {
		return this.chargePoints;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int updateChargePoints(int addPoints) {
		int newChargePoints = chargePoints + addPoints;
		if (newChargePoints > LEVEL2) {
			newChargePoints = LEVEL2;
		}
		else if (newChargePoints < 0) {
			newChargePoints = 0;
		}
		if (item.isEquipped() && player != null)
			player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
		item.setPersistentState(PersistentState.UPDATE_REQUIRED);
		this.chargePoints = newChargePoints;
		return newChargePoints;
	}

	@Override
	public void attacked(Creature creature) {
		updateChargePoints(-defendBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}

	@Override
	public void attack(Creature creature) {
		updateChargePoints(-attackBurn);
		Player player = this.player;
		if (player != null) {
			PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, item));
		}
	}

}
