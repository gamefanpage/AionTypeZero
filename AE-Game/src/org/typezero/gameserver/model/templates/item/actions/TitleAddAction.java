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

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Hilgert
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TitleAddAction")
public class TitleAddAction extends AbstractItemAction {

	@XmlAttribute
	protected int titleid;
  @XmlAttribute
  protected Integer minutes;

	/*
	 * (non-Javadoc)
	 * @see
	 * org.typezero.gameserver.itemengine.actions.AbstractItemAction#canAct(org.typezero.gameserver.model.gameobjects.player
	 * .Player, org.typezero.gameserver.model.gameobjects.Item, org.typezero.gameserver.model.gameobjects.Item)
	 */
	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (titleid == 0 || parentItem == null) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_COLOR_ERROR);
			return false;
		}
		if (player.getTitleList().contains(titleid)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_TOOLTIP_LEARNED_TITLE);
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.typezero.gameserver.itemengine.actions.AbstractItemAction#act(org.typezero.gameserver.model.gameobjects.player
	 * .Player, org.typezero.gameserver.model.gameobjects.Item, org.typezero.gameserver.model.gameobjects.Item)
	 */
	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_USE_ITEM(new DescriptionId(itemTemplate.getNameId())));
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);

		if (player.getTitleList().addTitle(titleid, false, minutes == null ? 0 : ((int)(System.currentTimeMillis()/1000))+minutes*60)) {
			Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
			player.getInventory().delete(item);
		}
	}
}
