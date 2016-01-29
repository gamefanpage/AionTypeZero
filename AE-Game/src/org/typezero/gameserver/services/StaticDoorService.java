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


package org.typezero.gameserver.services;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.staticdoor.StaticDoorState;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wakizashi
 */
public class StaticDoorService {

	private static final Logger log = LoggerFactory.getLogger(StaticDoorService.class);

	public static StaticDoorService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final StaticDoorService instance = new StaticDoorService();
	}

	public void openStaticDoor(final Player player, int doorId) {
		if (player.getAccessLevel() >= 3)
			PacketSendUtility.sendMessage(player, "door id : " + doorId);

		StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null){
			log.warn("Not spawned door worldId: "+ player.getWorldId()+" doorId: "+doorId);
			return;
		}
		int keyId = door.getObjectTemplate().getKeyId();

		if (player.getAccessLevel() >= 3)
			PacketSendUtility.sendMessage(player, "key id : " + keyId);

		if (checkStaticDoorKey(player, doorId, keyId)) {
			door.setOpen(true);
		}
		else
			log.info("Opening door without key ...");
	}

	public void changeStaticDoorState(final Player player, int doorId, boolean open, int state) {
		StaticDoor door = player.getPosition().getWorldMapInstance().getDoors().get(doorId);
		if (door == null){
			PacketSendUtility.sendMessage(player, "Door is not spawned!");
			return;
		}
		door.changeState(open, state);
		String currentStates = "";
		for (StaticDoorState st : StaticDoorState.values()) {
			if (st == StaticDoorState.NONE)
				continue;
			if (door.getStates().contains(st))
				currentStates += st.toString() + ", ";
		}
		if ("".equals(currentStates))
			currentStates = "NONE";
		else
			currentStates = currentStates.substring(0, currentStates.length() - 2);
		PacketSendUtility.sendMessage(player, "Door states now are: " + currentStates);
	}

	public boolean checkStaticDoorKey(Player player, int doorId, int keyId) {
		if (player.getAccessLevel() >= AdminConfig.DOORS_OPEN)
			return true;

		if (keyId == 0)
			return true;

		if (keyId == 1)
			return false;

		if (!player.getInventory().decreaseByItemId(keyId, 1)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_OPEN_DOOR_NEED_KEY_ITEM);
			PacketSendUtility.sendYellowMessage(player, MuiService.getInstance().getMessage("STATICDOORSERVICE", keyId));
			return false;
		}

		return true;
	}
}
