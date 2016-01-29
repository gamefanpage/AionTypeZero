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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.ExtractedItemsCollection;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.ResultedItem;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_DECOMPOSABLE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EVENT_BUFF;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steve
 */
public class CM_DECOMPOSABLE_ITEMS extends AionClientPacket
{

    private int objectId;
    private int index;

    public CM_DECOMPOSABLE_ITEMS(int opcode, State state, State... restStates)
    {
        super(opcode, state, restStates);
    }

    @Override
    protected void readImpl()
    {
        this.objectId = readD();
        readD();
        this.index = readC();
    }

    private boolean canAcquire(Player player, ResultedItem resultItem) {
        Race race = resultItem.getRace();
        if (race != Race.PC_ALL && !race.equals(player.getRace())) {
            return false;
        }
        PlayerClass playerClass = resultItem.getPlayerClass();

        if (!playerClass.equals(PlayerClass.ALL) && !playerClass.equals(player.getPlayerClass())) {
            return false;
        }
        return true;
    }

    @Override
    protected void runImpl()
    {
        Player player = getConnection().getActivePlayer();
        if (player == null)
        {
            return;
        }

        Object obj = player.getTempStorage(objectId);
        if (obj == null)
        {
            return;
        }


        ResultedItem resultItem = null;
        if (obj instanceof ExtractedItemsCollection)
        {
            ExtractedItemsCollection collection = (ExtractedItemsCollection) obj;
            Storage inventory = player.getInventory();

            List<ResultedItem> itemByType = new ArrayList();

            for (ResultedItem itemcollerction : collection.getItems())
            {
                if (canAcquire(player, itemcollerction))
                {
                    itemByType.add(itemcollerction);
                    continue;
                }
            }

            resultItem = itemByType.get(index);

            int slotReq = calcMaxCountOfSlots(resultItem, player, false);
            int specialSlotreq = calcMaxCountOfSlots(resultItem, player, true);
            if (slotReq > 0 && inventory.getFreeSlots() < slotReq)
            {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
                return;
            }
            if (specialSlotreq > 0 && inventory.getSpecialCubeFreeSlots() < specialSlotreq)
            {
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DECOMPRESS_INVENTORY_IS_FULL);
                return;
            }

        }
        if (resultItem == null)
        {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_NO_TARGET_ITEM);
            return;
        }

        if (!player.getInventory().decreaseByObjectId(objectId, 1))
        {
            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_DECOMPOSE_ITEM_NO_TARGET_ITEM);
            return;
        }

        PacketSendUtility.sendPacket(player, new SM_EVENT_BUFF(player, 0));
        player.clearTempStorage(index);

        ItemService.addItem(player, resultItem.getItemId(), resultItem.getResultCount());
    }

    private int calcMaxCountOfSlots(ResultedItem item, Player player, boolean special)
    {
        int maxCount = 0;
        if (item.getRace().equals(Race.PC_ALL)
                || player.getRace().equals(item.getRace()))
        {
            if (item.getPlayerClass().equals(PlayerClass.ALL)
                    || player.getPlayerClass().equals(item.getPlayerClass()))
            {
                ItemTemplate template = DataManager.ITEM_DATA.getItemTemplate(item.getItemId());
                if (special && template.getExtraInventoryId() > 0)
                {
                    maxCount = item.getCount();
                }
                else if (template.getExtraInventoryId() < 1)
                {
                    maxCount = item.getCount();
                }
            }
        }
        return maxCount;
    }
}
