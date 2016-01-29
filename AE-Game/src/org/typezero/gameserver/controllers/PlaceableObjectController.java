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

package org.typezero.gameserver.controllers;

import javolution.util.FastMap;

import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.housing.PlaceableHouseObject;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_HOUSE_OBJECT;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_OBJECT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author Rolandas
 */
public class PlaceableObjectController<T extends PlaceableHouseObject> extends VisibleObjectController<HouseObject<T>> {

	FastMap<Integer, ActionObserver> observed = new FastMap<Integer, ActionObserver>().shared();

	@Override
	public void see(VisibleObject object) {
		Player p = (Player) object;
		ActionObserver observer = new ActionObserver(ObserverType.MOVE);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
		PacketSendUtility.sendPacket(p, new SM_HOUSE_OBJECT(getOwner()));
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		Player p = (Player) object;
		ActionObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange) {
			observer.moved();
			PacketSendUtility.sendPacket(p, new SM_DELETE_HOUSE_OBJECT(getOwner().getObjectId()));
		}
		p.getObserveController().removeObserver(observer);
	}

	@Override
	public void onDespawn() {
		getOwner().onDespawn();
	}

	@Override
	public void delete() {
		if (getOwner().isSpawned())
			World.getInstance().despawn(getOwner(), false);
		World.getInstance().removeObject(getOwner());
	}

	public void onDialogRequest(Player player) {
		if (!MathUtil.isInRange(getOwner(), player, getOwner().getObjectTemplate().getTalkingDistance() + 2)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_OBJECT_TOO_FAR_TO_USE);
			return;
		}
		getOwner().onDialogRequest(player);
	}
}
