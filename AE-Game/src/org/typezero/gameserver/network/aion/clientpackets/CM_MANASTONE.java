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
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.model.templates.item.actions.EnchantItemAction;
import org.typezero.gameserver.model.templates.item.actions.GodstoneAction;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.EnchantService;
import org.typezero.gameserver.services.item.ItemSocketService;
import org.typezero.gameserver.services.trade.PricesService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer, Wakizashi
 *
 */
public class CM_MANASTONE extends AionClientPacket {

	private int npcObjId;
	private int slotNum;
	private int actionType;
	private int targetFusedSlot;
	private int stoneUniqueId;
	private int targetItemUniqueId;
	private int supplementUniqueId;
	@SuppressWarnings("unused")
	private ItemCategory actionCategory;

	/**
	 * @param opcode
	 */
	public CM_MANASTONE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		actionType = readC();
		targetFusedSlot = readC();
		targetItemUniqueId = readD();
		switch (actionType) {
			case 1:
			case 2:
			case 4:
			case 8:
				stoneUniqueId = readD();
				supplementUniqueId = readD();
				break;
			case 3:
				slotNum = readC();
				readC();
				readH();
				npcObjId = readD();
				break;
		}
	}

	@Override
    protected void runImpl() {
        Player player = getConnection().getActivePlayer();
        VisibleObject obj = player.getKnownList().getObject(npcObjId);

        switch (actionType) {
            case 1: // enchant stone
            case 2: // add manastone
                EnchantItemAction action = new EnchantItemAction();
                Item manastone = player.getInventory().getItemByObjId(stoneUniqueId);
                Item targetItem = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
                if (targetItem == null) {
                    targetItem = player.getInventory().getItemByObjId(targetItemUniqueId);
                }
                if (action.canAct(player, manastone, targetItem)) {
                    Item supplement = player.getInventory().getItemByObjId(supplementUniqueId);
                    if (supplement != null) {
                        if (supplement.getItemId() / 100000 != 1661) { // suppliment id check
                            return;
                        }
                    }
                    action.act(player, manastone, targetItem, supplement, targetFusedSlot);
                }
                break;
            case 3: // remove manastone
                long price = PricesService.getPriceForService(500, player.getRace());
                if (player.getInventory().getKinah() < price) {
                    PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_NOT_ENOUGH_KINA(price));
                    return;
                }
                if (obj != null && obj instanceof Npc && MathUtil.isInRange(player, obj, 7)) {
                    player.getInventory().decreaseKinah(price);
                    if (targetFusedSlot == 1) {
                        ItemSocketService.removeManastone(player, targetItemUniqueId, slotNum);
                    } else {
                        ItemSocketService.removeFusionstone(player, targetItemUniqueId, slotNum);
                    }
                }
                break;
            case 4: // add godstone
                Item godStone = player.getInventory().getItemByObjId(stoneUniqueId);
                Item targetItemGod = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
                if (targetItemGod == null) {
                	targetItemGod = player.getInventory().getItemByObjId(targetItemUniqueId);
                }
                GodstoneAction godAction = new GodstoneAction();
                if (godAction.canAct(player, godStone, targetItemGod)) {
                    godAction.act(player, godStone, targetItemGod);
                }
                break;
            case 8: // amplification
            	Item amplyMaterial = player.getInventory().getItemByObjId(supplementUniqueId);
            	Item targetItemAmply = player.getEquipment().getEquippedItemByObjId(targetItemUniqueId);
                if (targetItemAmply == null) {
                	targetItemAmply = player.getInventory().getItemByObjId(targetItemUniqueId);
                }
            	Item tool = player.getInventory().getItemByObjId(stoneUniqueId);
            	EnchantService.amplifyItem(player, targetItemAmply, amplyMaterial, tool);
            	break;
        }
    }
}
