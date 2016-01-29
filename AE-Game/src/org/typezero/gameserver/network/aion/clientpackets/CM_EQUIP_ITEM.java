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

import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Equipment;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import org.typezero.gameserver.restrictions.RestrictionsManager;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Avol modified by ATracer
 */
public class CM_EQUIP_ITEM extends AionClientPacket {

	public long slotRead;
	public int itemUniqueId;
	public int action;

	public CM_EQUIP_ITEM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readC(); // 0/1 = equip/unequip
		slotRead = readQ();
		itemUniqueId = readD();
	}

	@Override
	protected void runImpl() {
		final Player activePlayer = getConnection().getActivePlayer();

		activePlayer.getController().cancelUseItem();

		Equipment equipment = activePlayer.getEquipment();
		Item resultItem = null;

		if (!RestrictionsManager.canChangeEquip(activePlayer))
			return;

		switch (action) {
			case 0:
				if (activePlayer.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_CANT_EQUIP_ITEM_IN_ACTION);
					return;
				}
				resultItem = equipment.equipItem(itemUniqueId, slotRead);
				break;
			case 1:
				if (activePlayer.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_CANT_EQUIP_ITEM_IN_ACTION);
					return;
				}
				resultItem = equipment.unEquipItem(itemUniqueId, slotRead);
				break;
			case 2:
				if (activePlayer.getController().hasTask(TaskId.ITEM_USE) && !activePlayer.getController().getTask(TaskId.ITEM_USE).isDone() || activePlayer.isInPlayerMode(PlayerMode.RIDE)) {
					PacketSendUtility.sendPacket(activePlayer, SM_SYSTEM_MESSAGE.STR_CANT_EQUIP_ITEM_IN_ACTION);
					return;
				}
				equipment.switchHands();
				break;
		}

		if (resultItem != null || action == 2) {
			PacketSendUtility.broadcastPacket(activePlayer, new SM_UPDATE_PLAYER_APPEARANCE(activePlayer.getObjectId(),
				equipment.getEquippedForApparence()), true);
		}
		if (!equipment.isShieldEquipped())
		{
			for (Effect effect : activePlayer.getEffectController().getNoShowEffects())
			{
			   if (effect.isStance())
				   activePlayer.getEffectController().removeNoshowEffect(effect.getSkillId());
			}
		}

	}
}
