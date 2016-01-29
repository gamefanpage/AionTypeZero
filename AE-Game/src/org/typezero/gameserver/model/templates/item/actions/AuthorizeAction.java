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

package org.typezero.gameserver.model.templates.item.actions;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemCategory;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * User: Cain
 * Date: 25.08.2014
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="AuthorizeAction")
public class AuthorizeAction extends AbstractItemAction
{
    public boolean canAct(Player player, Item parentItem, Item targetItem)
    {
        if (!targetItem.getItemTemplate().isAccessory() && targetItem.getItemTemplate().getCategory() != ItemCategory.HELMET) {
            return false;
        }
        if (targetItem.getItemTemplate().getAuthorize() == 0) {
            return false;
        }
        if (targetItem.getAuthorize() >= targetItem.getItemTemplate().getAuthorize()) {
            return false;
        }
        return true;
    }

    public void act(final Player player, final Item parentItem, final Item targetItem)
    {
        PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 5000, 0, 0));

        final ItemUseObserver observer = new ItemUseObserver()
        {
            public void abort()
            {
                player.getController().cancelTask(TaskId.ITEM_USE);
                player.getObserveController().removeObserver(this);
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem.getItemTemplate().getTemplateId(), 0, 3, 0));
                ItemPacketService.updateItemAfterInfoChange(player, targetItem);
                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_CANCEL(targetItem.getNameId()));
            }
        };
        player.getObserveController().attach(observer);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable()
            {
                public void run()
                {
                    if (player.getInventory().decreaseByItemId(parentItem.getItemId(), 1L)) {
                        if (!AuthorizeAction.this.isSuccess()) {
                            PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 2, 0));
                            targetItem.setAuthorize(0);
                            if (!targetItem.getItemTemplate().isPlume()){
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_FAILED(targetItem.getNameId()));
                            } else{
                                player.getEquipment().unEquipItem(targetItem.getObjectId(), 0);
                                player.getInventory().decreaseByObjectId(targetItem.getObjectId(), 1L);//delete plume
                                PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_FAILED_TSHIRT(targetItem.getNameId()));
                            }
                        } else {
                            PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), player.getObjectId(), parentItem.getObjectId(), parentItem.getItemId(), 0, 1, 0));
                            targetItem.setAuthorize(targetItem.getAuthorize() + 1);
                            PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_AUTHORIZE_SUCCEEDED(targetItem.getNameId(), targetItem.getAuthorize()));
                        }
                        PacketSendUtility.sendPacket(player, new SM_INVENTORY_UPDATE_ITEM(player, targetItem));
                        player.getObserveController().removeObserver(observer);
                        if (targetItem.isEquipped()) {
                            player.getGameStats().updateStatsVisually();
                        }
                        ItemPacketService.updateItemAfterInfoChange(player, targetItem);
                        if (targetItem.isEquipped()) {
                            player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
                        } else {
                            player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);
                        }
                    }
                }
            }, 5000L));
    }

    public boolean isSuccess()
    {
        return Rnd.get(0, 1000) < 700;
    }
}

