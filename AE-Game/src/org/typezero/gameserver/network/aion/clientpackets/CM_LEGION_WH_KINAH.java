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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionHistoryType;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.team.legion.LegionPermissionsMask;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
public class CM_LEGION_WH_KINAH extends AionClientPacket {

   public CM_LEGION_WH_KINAH(int opcode, State state, State... restStates) {
	  super(opcode, state, restStates);
   }

   private long amount;
   private int operation;

   @Override
   protected void readImpl() {
	  this.amount = readQ();
	  this.operation = readC();
   }

   @Override
   protected void runImpl() {
	  final Player activePlayer = getConnection().getActivePlayer();

	  Legion legion = activePlayer.getLegion();
	  if (legion != null) {
		 LegionMember LM = LegionService.getInstance().getLegionMember(activePlayer.getObjectId());
		 switch (operation) {
			case 0:
			   if (!LM.hasRights(LegionPermissionsMask.WH_DEPOSIT)) {
				  // You do not have the authority to use the Legion warehouse.
				  PacketSendUtility.sendPacket(activePlayer, new SM_SYSTEM_MESSAGE(1300322));
				  return;
			   }
			   if (activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).tryDecreaseKinah(amount)) {
				  activePlayer.getInventory().increaseKinah(amount);
				  LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_WITHDRAW, 2, Long.toString(amount));
			   }
			   break;
			case 1:
			   if (!LM.hasRights(LegionPermissionsMask.WH_WITHDRAWAL)) {
				  // You do not have the authority to use the Legion warehouse.
				  PacketSendUtility.sendPacket(activePlayer, new SM_SYSTEM_MESSAGE(1300322));
				  return;
			   }
			   if (activePlayer.getInventory().tryDecreaseKinah(amount)) {
				  activePlayer.getStorage(StorageType.LEGION_WAREHOUSE.getId()).increaseKinah(amount);
				  LegionService.getInstance().addHistory(legion, activePlayer.getName(), LegionHistoryType.KINAH_DEPOSIT, 2, Long.toString(amount));
			   }
			   break;
		 }
	  }
   }
}
