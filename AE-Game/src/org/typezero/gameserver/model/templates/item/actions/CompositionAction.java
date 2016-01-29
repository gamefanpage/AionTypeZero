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

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.controllers.observer.ItemUseObserver;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.PacketSendUtility;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: pixfid
 * Date: 7/14/13
 * Time: 5:18 PM
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositionAction")
public class CompositionAction extends AbstractItemAction {
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		return false;
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {

	}

	public boolean canAct(Player player, Item tools, Item first, Item second) {

		if (!tools.getItemTemplate().isCombinationItem())
			return false;

		if (!first.getItemTemplate().isEnchantmentStone())
			return false;

		if (!second.getItemTemplate().isEnchantmentStone())
			return false;

		if (first.getItemCount() < 1 || second.getItemCount() < 1)
			return false;

		if (first.getItemTemplate().getLevel() > 95 || second.getItemTemplate().getLevel() > 95)
			return false;

		return true;
	}

	public void act(final Player player, final Item tools, final Item first, final Item second) {

		PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), tools.getObjectId(), tools.getItemTemplate().getTemplateId(), 5000, 0, 0));
		player.getController().cancelTask(TaskId.ITEM_USE);

		final ItemUseObserver observer = new ItemUseObserver() {

			@Override
			public void abort() {
				player.getController().cancelTask(TaskId.ITEM_USE);
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), tools.getObjectId(), tools.getItemTemplate().getTemplateId(), 0, 2, 0));
				player.getObserveController().removeObserver(this);
			}
		};
		player.getObserveController().attach(observer);
		player.getController().addTask(TaskId.ITEM_USE, ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				player.getObserveController().removeObserver(observer);
				boolean result = player.getInventory().decreaseByObjectId(tools.getObjectId(), 1);
				boolean result1 = player.getInventory().decreaseByObjectId(first.getObjectId(), 1);
				boolean result2 = player.getInventory().decreaseByObjectId(second.getObjectId(), 1);
				if (result && result1 && result2) {
					ItemService.addItem(player, getItemId(calcLevel(first.getItemTemplate().getLevel(), second.getItemTemplate().getLevel())), 1);
				}
				PacketSendUtility.sendPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), tools.getObjectId(), tools.getItemTemplate().getTemplateId(), 0, 1, 0));
			}
		}, 5000));

	}

	private int calcLevel(int first, int second) {
		int value = ((first + second) / 2);
		if (value < 11) {
			value = Rnd.get(1, 20);
		} else {
			int random = Rnd.get(1, 10);
			int bit = Rnd.get(0, 1);
			value = (bit == 0 ? value - random : value + random);
		}
		return value;
	}

	public int getItemId(int value) {
		return 166000000 + value;
	}
}
