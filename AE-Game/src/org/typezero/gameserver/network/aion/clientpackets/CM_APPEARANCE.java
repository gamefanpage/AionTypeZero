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

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.actions.AbstractItemAction;
import org.typezero.gameserver.model.templates.item.actions.CosmeticItemAction;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.RenameService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
public class CM_APPEARANCE extends AionClientPacket {

	private int type;

	private int itemObjId;

	private String name;

	public CM_APPEARANCE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		type = readC();
		readC();
		readH();
		itemObjId = readD();
		switch (type) {
			case 0:
			case 1:
				name = readS();
				break;
		}

	}

	@Override
	protected void runImpl() {
		final Player player = getConnection().getActivePlayer();

		switch (type) {
			case 0: // Change Char Name,
				if (RenameService.renamePlayer(player, player.getName(), name, itemObjId)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400157, name));
				}
				break;
			case 1: // Change Legion Name
				if (RenameService.renameLegion(player, name, itemObjId)) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400158, name));
				}
				break;
			case 2: // cosmetic items
				Item item = player.getInventory().getItemByObjId(itemObjId);
				if (item != null) {
					for (AbstractItemAction action : item.getItemTemplate().getActions().getItemActions()) {
						if (action instanceof CosmeticItemAction) {
							if (!action.canAct(player, null, null)) {
								return;
							}
							action.act(player, null, item);
							break;
						}
					}
				}
				break;
		}
	}
}
