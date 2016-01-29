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
import org.typezero.gameserver.model.gameobjects.HouseDecoration;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.PartType;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_EDIT;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;

/**
 * @author Rolandas
 */
public class CM_HOUSE_DECORATE extends AionClientPacket {

	int objectId;
	int templateId;
	int lineNr; // Line number (starts from 1 in 3.0 and from 2 in 3.5) of part in House render/update packet

	public CM_HOUSE_DECORATE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		objectId = readD();
		templateId = readD();
		lineNr = readH();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player == null)
			return;

		House house = player.getHouseRegistry().getOwner();

		PartType partType = PartType.getForLineNr(lineNr);
		int floor = lineNr - partType.getStartLineNr();

		if (objectId == 0) {
			// change appearance to default, delete any applied customs finally
			HouseDecoration decor = house.getRegistry().getDefaultPartByType(partType, floor);
			if (decor.isUsed()) {
				return;
			}
			house.getRegistry().setPartInUse(decor, floor);
		}
		else {
			// remove from inventory
			HouseDecoration decor = house.getRegistry().getCustomPartByObjId(objectId);
			house.getRegistry().setPartInUse(decor, floor);
			sendPacket(new SM_HOUSE_EDIT(4, 2, objectId)); // yes, in retail it's sent twice!
		}

		sendPacket(new SM_HOUSE_EDIT(4, 2, objectId));
		house.getRegistry().setPersistentState(PersistentState.UPDATE_REQUIRED);
		((HouseController) house.getController()).updateAppearance();
		QuestEngine.getInstance().onHouseItemUseEvent(new QuestEnv(null, player, 0, 0));
	}

}
