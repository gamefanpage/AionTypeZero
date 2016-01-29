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

package org.typezero.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.StaticObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.recipe.RecipeTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_CRAFT_ANIMATION;
import org.typezero.gameserver.network.aion.serverpackets.SM_CRAFT_UPDATE;
import org.typezero.gameserver.services.craft.CraftService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Mr. Poke, synchro2
 */
public class CraftingTask extends AbstractCraftTask {

	protected RecipeTemplate recipeTemplate;
	protected ItemTemplate itemTemplate;
	protected int critCount;
	protected boolean crit = false;
	protected int maxCritCount;
	private int bonus;

	/**
	 * @param requestor
	 * @param responder
	 * @param successValue
	 * @param failureValue
	 */
	public CraftingTask(Player requestor, StaticObject responder, RecipeTemplate recipeTemplate, int skillLvlDiff, int bonus) {
		super(requestor, responder, skillLvlDiff);
		this.recipeTemplate = recipeTemplate;
		this.maxCritCount = recipeTemplate.getComboProductSize();
		this.bonus = bonus;
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractCraftTask#onFailureFinish()
	 */
	@Override
	protected void onFailureFinish() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
				currentSuccessValue, currentFailureValue, 6));
		PacketSendUtility.broadcastPacket(requestor,
				new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 3), true);
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractCraftTask#onSuccessFinish()
	 */
	@Override
	protected boolean onSuccessFinish() {
		if (crit && recipeTemplate.getComboProduct(critCount) != null) {
			PacketSendUtility.sendPacket(requestor,
					new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 3));
			onInteractionStart();
			return false;
		}
		else {
			PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
					currentSuccessValue, currentFailureValue, 5));
			PacketSendUtility.broadcastPacket(requestor,
					new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
			CraftService.finishCrafting(requestor, recipeTemplate, critCount, bonus);
			return true;
		}
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractCraftTask#sendInteractionUpdate()
	 */
	@Override
	protected void sendInteractionUpdate() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate,
				currentSuccessValue, currentFailureValue, 1));
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractInteractionTask#onInteractionAbort()
	 */
	@Override
	protected void onInteractionAbort() {
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 4));
		PacketSendUtility.broadcastPacket(requestor,
				new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), 0, 2), true);
		requestor.setCraftingTask(null);
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractInteractionTask#onInteractionFinish()
	 */
	@Override
	protected void onInteractionFinish() {
		requestor.setCraftingTask(null);
	}

	/*
	 * (non-Javadoc) @see
	 * org.typezero.gameserver.skillengine.task.AbstractInteractionTask#onInteractionStart()
	 */
	@Override
	protected void onInteractionStart() {
		currentSuccessValue = 0;
		currentFailureValue = 0;
		checkCrit();

		int chance = requestor.getRates().getCraftCritRate();
		if (maxCritCount > 0) {
			if (critCount > 0 && maxCritCount > 1) {
				chance = requestor.getRates().getComboCritRate();
				House house = requestor.getActiveHouse();
				if (house != null)
					switch (house.getHouseType()) {
						case ESTATE:
						case PALACE:
							chance += 5;
							break;
					default:
						break;
					}
			}

			if ((critCount < maxCritCount) && (Rnd.get(100) < chance)) {
				critCount++;
				crit = true;
			}
		}

		PacketSendUtility.sendPacket(requestor,
				new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, completeValue, completeValue, 0));
		PacketSendUtility.sendPacket(requestor, new SM_CRAFT_UPDATE(recipeTemplate.getSkillid(), itemTemplate, 0, 0, 1));
		PacketSendUtility.broadcastPacket(requestor,
				new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 0), true);
		PacketSendUtility.broadcastPacket(requestor,
				new SM_CRAFT_ANIMATION(requestor.getObjectId(), responder.getObjectId(), recipeTemplate.getSkillid(), 1), true);
	}

	protected void checkCrit() {
		if (crit) {
			crit = false;
			this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getComboProduct(critCount));
		}
		else
			this.itemTemplate = DataManager.ITEM_DATA.getItemTemplate(recipeTemplate.getProductid());
	}

}
