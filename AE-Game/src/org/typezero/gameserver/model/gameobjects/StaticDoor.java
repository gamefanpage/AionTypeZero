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

package org.typezero.gameserver.model.gameobjects;

import org.typezero.gameserver.controllers.StaticObjectController;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.staticdoor.StaticDoorState;
import org.typezero.gameserver.model.templates.staticdoor.StaticDoorTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.geo.GeoService;

import java.util.EnumSet;

/**
 * @author MrPoke, Rolandas
 */
public class StaticDoor extends StaticObject {

	private EnumSet<StaticDoorState> states;
	private String doorName;

	/**
	 * @param objectId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 */
	public StaticDoor(int objectId, StaticObjectController controller, SpawnTemplate spawnTemplate, StaticDoorTemplate objectTemplate,
		int instanceId) {
		super(objectId, controller, spawnTemplate, objectTemplate);
		states = EnumSet.copyOf(getObjectTemplate().getInitialStates());
		if (objectTemplate.getMeshFile() != null) {
			doorName = GeoService.getInstance().getDoorName(spawnTemplate.getWorldId(), objectTemplate.getMeshFile(), objectTemplate.getX(),
				objectTemplate.getY(), objectTemplate.getZ());
		}
	}

	/**
	 * @return the open state from states set
	 */
	public boolean isOpen() {
		return states.contains(StaticDoorState.OPENED);
	}

	public EnumSet<StaticDoorState> getStates() {
		return states;
	}

	/**
	 * @param open
	 *          the open state to set
	 */
	public void setOpen(boolean open) {
		EmotionType emotion;
		int packetState = 0; // not important IMO, similar to internal state
		if (open) {
			emotion = EmotionType.OPEN_DOOR;
			states.remove(StaticDoorState.CLICKABLE);
			states.add(StaticDoorState.OPENED); // 1001
			packetState = 0x9;
		}
		else {
			emotion = EmotionType.CLOSE_DOOR;
			if (getObjectTemplate().getInitialStates().contains(StaticDoorState.CLICKABLE))
				states.add(StaticDoorState.CLICKABLE);
			states.remove(StaticDoorState.OPENED); // 1010
			packetState = 0xA;
		}
		if (doorName != null) {
			GeoService.getInstance().setDoorState(getWorldId(), getInstanceId(), doorName, open);
		}
		// int stateFlags = StaticDoorState.getFlags(states);
		PacketSendUtility.broadcastPacket(this, new SM_EMOTION(this.getSpawn().getStaticId(), emotion, packetState));
	}

	public void changeState(boolean open, int state) {
		state = state & 0xF;
		StaticDoorState.setStates(state, states);
		EmotionType emotion = open ? emotion = EmotionType.OPEN_DOOR : EmotionType.CLOSE_DOOR;
		PacketSendUtility.broadcastPacket(this, new SM_EMOTION(this.getSpawn().getStaticId(), emotion, state));
	}

	@Override
	public StaticDoorTemplate getObjectTemplate() {
		return (StaticDoorTemplate) super.getObjectTemplate();
	}

	public String getDoorName() {
		return doorName;
	}

}
