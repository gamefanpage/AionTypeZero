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

import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.TaskId;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PortalCooldownList;
import org.typezero.gameserver.network.aion.serverpackets.SM_INSTANCE_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Tiger
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceTimeClear")
public class InstanceTimeClear extends AbstractItemAction {

	@XmlAttribute(name = "sync_ids")
	protected List<Integer> syncIds;
    public int instanceId;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
        for (Integer syncId : syncIds) {
            if (syncId == instanceId) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void act(final Player player, final Item parentItem, Item targetItem) {
        final PortalCooldownList cools = player.getPortalCooldownList();

		PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(),
			parentItem.getItemId(), 1000, 0, 0));

		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				// TODO: abort is invalid. Should we abort all or only the last syncid?
				player.getController().cancelTask(TaskId.ITEM_USE);
				player.removeItemCoolDown(parentItem.getItemTemplate().getUseLimits().getDelayId());
				PacketSendUtility.sendPacket(player,
					SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(parentItem.getItemTemplate().getNameId())));
				PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), parentItem
					.getItemTemplate().getTemplateId(), 0, 2, 0), true);
				player.getObserveController().removeObserver(this);
			}

		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				if (parentItem.getActivationCount() > 1)
					parentItem.setActivationCount(parentItem.getActivationCount() - 1);
				else
					player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1);
                cools.AddPortCount(InstanceTimeClear.this.instanceId);

				PacketSendUtility.broadcastPacketAndReceive(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(),
					parentItem.getItemId(), 0, 1, 0));
                PacketSendUtility.sendPacket(player, new SM_INSTANCE_INFO(player, false));
			}

		}, 1000));
	}

}
