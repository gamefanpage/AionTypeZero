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

import org.typezero.gameserver.configs.main.InGameShopConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.ingameshop.InGameShopEn;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_CATEGORY_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_IN_GAME_SHOP_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_TOLL_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xTz, KID
 */
public class CM_IN_GAME_SHOP_INFO extends AionClientPacket {
	private int actionId;
	private int categoryId;
	private int listInCategory;
	private String senderName;
	private String senderMessage;

	public CM_IN_GAME_SHOP_INFO(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionId = readC();
		categoryId = readD();
		listInCategory = readD();
		senderName = readS();
		senderMessage = readS();
	}

	@Override
	protected void runImpl() {
		if (InGameShopConfig.ENABLE_IN_GAME_SHOP) {
			Player player = getConnection().getActivePlayer();
			switch (actionId) {
				case 0x01: // item info
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_ITEM(player, categoryId));
					break;
				case 0x02: // change category
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_CATEGORY_LIST(2, categoryId));
					player.inGameShop.setCategory((byte) categoryId);
					break;
				case 0x04: // category list
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_CATEGORY_LIST(0, categoryId));
					break;
				case 0x08: // showcat
					if (categoryId > 1)
						player.inGameShop.setSubCategory((byte) categoryId);

					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_LIST(player, listInCategory, 1));
					PacketSendUtility.sendPacket(player, new SM_IN_GAME_SHOP_LIST(player, listInCategory, 0));
					break;
				case 0x10: // balance
					PacketSendUtility.sendPacket(player, new SM_TOLL_INFO(player.getClientConnection().getAccount().getToll()));
					break;
				case 0x20: // buy
					InGameShopEn.getInstance().acceptRequest(player, categoryId);
					break;
				case 0x40: // gift
					InGameShopEn.getInstance().sendRequest(player, senderName, senderMessage, categoryId);
					break;
			}
		}
	}
}
