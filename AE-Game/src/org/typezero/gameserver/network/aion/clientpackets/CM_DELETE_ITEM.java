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

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_EVENT_BUFF;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Avol
 */
public class CM_DELETE_ITEM extends AionClientPacket {

	public int itemObjectId;

	public CM_DELETE_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		itemObjectId = readD();
	}

	@Override
	protected void runImpl() {

		Player player = getConnection().getActivePlayer();
		Storage inventory = player.getInventory();
		Item item = inventory.getItemByObjId(itemObjectId);

		if (item != null) {
			if (!item.getItemTemplate().isBreakable()) {
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_UNBREAKABLE_ITEM(new DescriptionId(item.getNameId())));
			}
            else if (item.getItemEffect() != null) {
                if ((item.getItemTemplate().isNewPlayerBuffItem()) || (item.getItemTemplate().isOldPlayerBuffItem()) || (item.getItemTemplate().isEventBuffItem())) {
                    player.getGameStats().endEffect(item.getItemEffect());
                    inventory.delete(item, ItemDeleteType.DISCARD);
                    player.updataItemEffectId();
                    PacketSendUtility.broadcastPacket(player, new SM_EVENT_BUFF(player, player.getItemEffectId()), true);
                    PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
                }
            }
			else {
				inventory.delete(item, ItemDeleteType.DISCARD);
			}
		}
	}
}
