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

import org.typezero.gameserver.controllers.HouseController;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.HouseDecoration;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.item.ItemTemplate;
import org.typezero.gameserver.model.templates.item.actions.DecorateAction;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_EDIT;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_REGISTRY;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.HousingService;
import org.typezero.gameserver.services.item.HouseObjectFactory;
import org.typezero.gameserver.services.item.ItemPacketService.ItemDeleteType;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.idfactory.IDFactory;

/**
 * @author Rolandas
 */
public class CM_HOUSE_EDIT extends AionClientPacket {

	int action;
	int itemObjectId;
	float x, y, z;
	int rotation;
	int buildingId;

	public CM_HOUSE_EDIT(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		action = readC();
		if (action == 3) {
			itemObjectId = readD();
		}
		else if (action == 4) {
			itemObjectId = readD();
		}
		else if (action == 5) {
			itemObjectId = readD();
			x = readF();
			y = readF();
			z = readF();
			rotation = readH();
		}
		else if (action == 6) {
			itemObjectId = readD();
			x = readF();
			y = readF();
			z = readF();
			rotation = readH();
		}
		else if (action == 7) {
			itemObjectId = readD();
		}
		else if (action == 16) {
			buildingId = readD();
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null)
			return;

		if (action == 1) { // Enter Decoration mode
			sendPacket(new SM_HOUSE_EDIT(action));
			sendPacket(new SM_HOUSE_REGISTRY(action));
			sendPacket(new SM_HOUSE_REGISTRY(action + 1));
		}
		else if (action == 2) { // Exit Decoration mode
			sendPacket(new SM_HOUSE_EDIT(action));
		}
		else if (action == 3) { // Add item
			Item item = player.getInventory().getItemByObjId(itemObjectId);
			if (item == null)
				return;

			ItemTemplate template = item.getItemTemplate();
			player.getInventory().delete(item, ItemDeleteType.REGISTER);

			DecorateAction decorateAction = template.getActions().getDecorateAction();
			if (decorateAction != null) {
				HouseDecoration decor = new HouseDecoration(IDFactory.getInstance().nextId(), decorateAction.getTemplateId());
				player.getHouseRegistry().putCustomPart(decor);
				sendPacket(new SM_HOUSE_EDIT(action, 2, decor.getObjectId()));
			}
			else {
				House house = player.getHouseRegistry().getOwner();
				HouseObject<?> obj = HouseObjectFactory.createNew(house, template);
				player.getHouseRegistry().putObject(obj);
				sendPacket(new SM_HOUSE_EDIT(action, 1, obj.getObjectId()));
			}
		}
		else if (action == 4) { // Delete item
			player.getHouseRegistry().removeObject(itemObjectId);
			sendPacket(new SM_HOUSE_EDIT(action, 1, itemObjectId));
			sendPacket(new SM_HOUSE_EDIT(4, 1, itemObjectId));
		}
		else if (action == 5) { // spawn object
			HouseObject<?> obj = player.getHouseRegistry().getObjectByObjId(itemObjectId);
			if (obj == null) {
				return;
			}
			else {
				obj.setX(x);
				obj.setY(y);
				obj.setZ(z);
				obj.setRotation(rotation);
				sendPacket(new SM_HOUSE_EDIT(action, itemObjectId, x, y, z, rotation));
				obj.spawn();
				player.getHouseRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
				sendPacket(new SM_HOUSE_EDIT(4, 1, itemObjectId));
				QuestEngine.getInstance().onHouseItemUseEvent(new QuestEnv(null, player, 0, 0));
			}
		}
		else if (action == 6) { // move object
			HouseObject<?> obj = player.getHouseRegistry().getObjectByObjId(itemObjectId);
			if (obj == null)
				return;
			sendPacket(new SM_HOUSE_EDIT(action + 1, 0, itemObjectId));
			obj.getController().onDelete();
			obj.setX(x);
			obj.setY(y);
			obj.setZ(z);
			obj.setRotation(rotation);
			if (obj.getPersistentState() == PersistentState.UPDATE_REQUIRED)
				player.getHouseRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
			sendPacket(new SM_HOUSE_EDIT(action - 1, itemObjectId, x, y, z, rotation));
			obj.spawn();
		}
		else if (action == 7) { // despawn object
			HouseObject<?> obj = player.getHouseRegistry().getObjectByObjId(itemObjectId);
			if (obj == null)
				return;
			sendPacket(new SM_HOUSE_EDIT(action, 0, itemObjectId));
			obj.getController().onDelete();
			obj.removeFromHouse();
			obj.clearKnownlist();
			player.getHouseRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
			sendPacket(new SM_HOUSE_EDIT(3, 1, itemObjectId)); // place it back
		}
		else if (action == 14) { // enter renovation mode
			sendPacket(new SM_HOUSE_EDIT(14));
		}
		else if (action == 15) { // exit renovation mode
			sendPacket(new SM_HOUSE_EDIT(15));
		}
		else if (action == 16) {
			House house = player.getHouseRegistry().getOwner();
			if (!removeRenovationCoupon(player, house)) {
				AuditLogger.info(player, "Try house renovation without coupon");
				return;
			}
			HousingService.getInstance().switchHouseBuilding(house, buildingId);
			player.setHouseRegistry(house.getRegistry());
			((HouseController) house.getController()).updateAppearance();
		}
	}

	private boolean removeRenovationCoupon(Player player, House house) {
		int typeId = house.getHouseType().getId();
		if (typeId == 0)
			return false; // studio
		int itemId = (player.getRace().equals(Race.ELYOS) ? 169661004 : 169661008) - typeId;
		if (player.getInventory().getItemCountByItemId(itemId) > 0)
			return player.getInventory().decreaseByItemId(itemId, 1);
		return false;
	}
}
