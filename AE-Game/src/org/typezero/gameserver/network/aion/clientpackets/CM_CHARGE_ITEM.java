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
import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.item.ItemChargeService;

/**
 * @author ATracer
 */
public class CM_CHARGE_ITEM extends AionClientPacket {

	private int targetNpcObjectId;
	private int chargeLevel;
	private Collection<Integer> itemIds;

	public CM_CHARGE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		targetNpcObjectId = readD();
		chargeLevel = readC();
		int itemsSize = readH();
		itemIds = new ArrayList<Integer>();
		for (int i = 0; i < itemsSize; i++) {
			itemIds.add(readD());
		}

	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (!player.isTargeting(targetNpcObjectId)) {
			return; // TODO audit?
		}

		for (int itemObjId : itemIds) {
			Item item = player.getInventory().getItemByObjId(itemObjId);
			if (item != null) {
				int itemChargeLevel = item.getChargeLevelMax();
				int possibleChargeLevel = Math.min(itemChargeLevel, chargeLevel);
				if (possibleChargeLevel > 0) {
					if (ItemChargeService.processPayment(player, item, possibleChargeLevel))
						ItemChargeService.chargeItem(player, item, possibleChargeLevel);
				}
			}
		}
	}

}
