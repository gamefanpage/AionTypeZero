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

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import org.typezero.gameserver.services.SkillLearnService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SkillLearnAction")
public class SkillLearnAction extends AbstractItemAction {

	@XmlAttribute
	protected int skillid;
	@XmlAttribute
	protected int level;
	@XmlAttribute(name = "class")
	protected PlayerClass playerClass;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		// 1. check player level
		if (player.getCommonData().getLevel() < level)
			return false;

		PlayerClass pc = player.getCommonData().getPlayerClass();
		if (!validateClass(pc))
			return false;

		// 4. check player race and Race.PC_ALL
		Race race = parentItem.getItemTemplate().getRace();
		if (player.getRace() != race && race != Race.PC_ALL)
			return false;
		// 5. check whether this skill is already learned
		if (player.getSkillList().isSkillPresent(skillid))
			return false;

		return true;
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		// item animation and message
		ItemTemplate itemTemplate = parentItem.getItemTemplate();
		// PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.USE_ITEM(itemTemplate.getDescription()));
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player,
			new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), parentItem.getObjectId(), itemTemplate.getTemplateId()), true);

		// add skill
		SkillLearnService.learnSkillBook(player, skillid);

		// remove book from inventory (assuming its not stackable)
		Item item = player.getInventory().getItemByObjId(parentItem.getObjectId());
		player.getInventory().delete(item);
	}

	private boolean validateClass(PlayerClass pc) {
		boolean result = false;
		// 2. check if current class is second class and book is for starting class
		if (!pc.isStartingClass() && PlayerClass.getStartingClassFor(pc).ordinal() == playerClass.ordinal())
			result = true;
		// 3. check player class and SkillClass.ALL
		if (pc == playerClass || playerClass == PlayerClass.ALL)
			result = true;

		return result;
	}
}
