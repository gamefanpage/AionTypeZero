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

import javax.xml.bind.annotation.XmlAttribute;

import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.Storage;
import org.typezero.gameserver.model.templates.item.Acquisition;
import org.typezero.gameserver.model.templates.item.ArmorType;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author Rolandas, Luzien
 */
public class ApExtractAction extends AbstractItemAction {

	@XmlAttribute
	protected ApExtractTarget target;
	@XmlAttribute
	protected float rate;

	@Override
	public boolean canAct(Player player, Item parentItem, Item targetItem) {
		if (targetItem == null || !targetItem.canApExtract())
			return false;
		if (parentItem.getItemTemplate().getLevel() < targetItem.getItemTemplate().getLevel())
			return false;
		if (parentItem.getItemTemplate().getItemQuality() != targetItem.getItemTemplate().getItemQuality())
			return false;

		// TODO: ApExtractTarget.OTHER, ApExtractTarget.ALL. Find out what should go there

		ApExtractTarget type = null;
		switch (targetItem.getItemTemplate().getCategory()) {
			case SWORD:
			case DAGGER:
			case MACE:
			case ORB:
			case SPELLBOOK:
			case BOW:
			case GREATSWORD:
			case POLEARM:
			case STAFF:
			case SHIELD:
			case HARP:
			case GUN:
			case CANNON:
				type = ApExtractTarget.WEAPON;
				break;
			case JACKET:
			case PANTS:
			case SHOES:
			case GLOVES:
			case SHOULDERS:
				type = ApExtractTarget.ARMOR;
				break;
			case NECKLACE:
			case EARRINGS:
			case RINGS:
			case HELMET:
			case BELT:
				type = ApExtractTarget.ACCESSORY;
				break;
			case NONE:
				if (targetItem.getItemTemplate().getArmorType() == ArmorType.WING) {
					type = ApExtractTarget.WING;
					break;
				}
				return false;
			default:
				return false;
		}
		return (target == ApExtractTarget.EQUIPMENT || target == type);
	}

	@Override
	public void act(Player player, Item parentItem, Item targetItem) {
		Acquisition acquisition = targetItem.getItemTemplate().getAcquisition();
		if (acquisition == null || acquisition.getRequiredAp() == 0)
			return;
		int ap = (int) (acquisition.getRequiredAp() * rate);
		Storage inventory = player.getInventory();

		if (inventory.delete(targetItem) != null) {
			if (inventory.decreaseByObjectId(parentItem.getObjectId(), 1))
				AbyssPointsService.addAp(player, ap);
		}
		else
			AuditLogger.info(player, "Possible extract item hack, do not remove item.");
	}

	public ApExtractTarget getTarget() {
		return target;
	}

	public float getRate() {
		return rate;
	}
}
