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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_EDIT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.network.aion.serverpackets.SM_UPDATE_PLAYER_APPEARANCE;
import org.typezero.gameserver.services.item.ItemPacketService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author IceReaper
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DyeAction")
public class DyeAction extends AbstractItemAction implements IHouseObjectDyeAction {

	@XmlAttribute(name = "color")
	protected String color;
	@XmlAttribute
	private Integer minutes;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem == null) { // no item selected.
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}

		return true;
	}

	private int getColorBGRA() {
		if (color.equals("no")) {
			return 0;
		}
		else {
			int rgb = Integer.parseInt(color, 16);
			return 0xFF | ((rgb & 0xFF) << 24) | ((rgb & 0xFF00) << 8) | ((rgb & 0xFF0000) >>> 8);
		}
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
			return;
		if (targetItem.getItemSkinTemplate().isItemDyePermitted()) {
			if (getColorBGRA() == 0) {
				targetItem.setItemColor(0);
				targetItem.setColorExpireTime(0);
			}
			else {
				targetItem.setItemColor(parentItem.getItemTemplate().getTemplateId());
				if (minutes != null)
					targetItem.setColorExpireTime((int) (System.currentTimeMillis() / 1000 + minutes * 60));
			}

			// item is equipped, so need broadcast packet
			if (player.getEquipment().getEquippedItemByObjId(targetItem.getObjectId()) != null) {
				PacketSendUtility.broadcastPacket(player, new SM_UPDATE_PLAYER_APPEARANCE(player.getObjectId(), player.getEquipment()
					.getEquippedForApparence()), true);
				player.getEquipment().setPersistentState(PersistentState.UPDATE_REQUIRED);
			}

			// item is not equipped
			else
				player.getInventory().setPersistentState(PersistentState.UPDATE_REQUIRED);

			ItemPacketService.updateItemAfterInfoChange(player, targetItem);
		}
	}

	public int getColor() {
		return getColorBGRA();
	}

	@Override
	public boolean canAct(Player player, Item parentItem, HouseObject<?> targetHouseObject) {
		if (targetHouseObject == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (color.equals("no") && targetHouseObject.getColor() == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_ERROR_CANNOTREMOVE);
			return false;
		}
		boolean canPaint = targetHouseObject.getObjectTemplate().getCanDye();
		if (!canPaint)
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_ERROR_CANNOTPAINT);
		return canPaint;
	}

	@Override
	public void act(Player player, Item parentItem, HouseObject<?> targetHouseObject) {
		if (!player.getInventory().decreaseByObjectId(parentItem.getObjectId(), 1))
			return;
		if (color.equals("no")) {
			targetHouseObject.setColor(null);
		}
		else {
			targetHouseObject.setColor(Integer.parseInt(color, 16));
		}
		float x = targetHouseObject.getX();
		float y = targetHouseObject.getY();
		float z = targetHouseObject.getZ();
		int rotation = targetHouseObject.getRotation();
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(7, 0, targetHouseObject.getObjectId()));
		PacketSendUtility.sendPacket(player, new SM_HOUSE_EDIT(5, targetHouseObject.getObjectId(), x, y, z, rotation));
		targetHouseObject.spawn();
		int objectName = targetHouseObject.getObjectTemplate().getNameId();
		if (color.equals("no")) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_REMOVE_SUCCEED(objectName));
		}
		else {
			int paintName = parentItem.getItemTemplate().getNameId();
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_ITEM_PAINT_SUCCEED(objectName, paintName));
		}
	}

}
