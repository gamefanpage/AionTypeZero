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

import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.teleport.ScrollItem;
import org.typezero.gameserver.model.templates.teleport.ScrollItemLocationList;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.services.teleport.ScrollsTeleporterService;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * @author GiGatR00n
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiReturnAction")
public class MultiReturnAction extends AbstractItemAction {

    @XmlAttribute(name = "id")
    private int id;

    public int getId() {
        return id;
    }

    @Override
    public boolean canAct(Player player, Item parentItem, Item targetItem) {
        return true;
    }

    @Override
    public void act(final Player player, final Item parentItem, Item targetItem) {
    }

    public void act(final Player player, final Item ScrollItem, final int SelectedMapIndex) {

        PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 3000, 0, 0));
        player.getController().cancelTask(TaskId.ITEM_USE);

        final ItemUseObserver observer = new ItemUseObserver() {
            @Override
            public void abort() {
                player.getController().cancelTask(TaskId.ITEM_USE);
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 0, 2, 0));
                player.getObserveController().removeObserver(this);
                player.removeItemCoolDown(ScrollItem.getItemTemplate().getUseLimits().getDelayId());
            }
        };
        player.getObserveController().attach(observer);
        player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                player.getObserveController().removeObserver(observer);
                if (player.getInventory().decreaseByObjectId(ScrollItem.getObjectId(), 1)) {

            		int ScrollItemId = getId();
            		ScrollItem sItem = DataManager.MULTI_RETURN_ITEM_DATA.getScrollItembyId(ScrollItemId);

            		if (sItem != null && sItem.getLocationList() != null) {

            			ScrollItemLocationList LocData = sItem.getLocDatabyId(SelectedMapIndex);
            			if (LocData != null) {

            				int LocCount = sItem.getLocationList().size();
           					if (SelectedMapIndex <= (LocCount - 1)) {

           						int worldId = LocData.getWorldId();
           						int LocId = ScrollsTeleporterService.getScrollLocIdbyWorldId(worldId, player.getRace());

           						if (LocId == 0) {
           							LocId = Integer.parseInt(Integer.toString(worldId).substring(0 , 7));
           						}
           						ScrollsTeleporterService.ScrollTeleprter(player, LocId, worldId);
           					}
            			}
            		}
                }
                PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), ScrollItem.getObjectId(), ScrollItem.getItemTemplate().getTemplateId(), 0, 1, 0));
            }
        }, 3000));
    }
}
