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

import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.shield.Shield;
import org.typezero.gameserver.model.siege.FortressLocation;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.services.ShieldService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.world.World;

import javolution.util.FastMap;

/**
 * @author Source
 */
public class ShieldController extends VisibleObjectController<Shield> {

	FastMap<Integer, ActionObserver> observed = new FastMap<Integer, ActionObserver>().shared();

	@Override
	public void see(VisibleObject object) {
		FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		Player player = (Player) object;

		if (loc.isUnderShield())
			if (loc.getRace() != SiegeRace.getByRace(player.getRace())) {
				ActionObserver observer = ShieldService.getInstance().createShieldObserver(loc.getLocationId(), player);
				if (observer != null) {
					player.getObserveController().addObserver(observer);
					observed.put(player.getObjectId(), observer);
				}
			}
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		FortressLocation loc = SiegeService.getInstance().getFortress(getOwner().getId());
		Player player = (Player) object;

		if (loc.isUnderShield())
			if (loc.getRace() != SiegeRace.getByRace(player.getRace())) {
				ActionObserver observer = observed.remove(player.getObjectId());
				if (observer != null) {
					if (isOutOfRange)
						observer.moved();

					player.getObserveController().removeObserver(observer);
				}
			}
	}

	public void disable() {
		for (FastMap.Entry<Integer, ActionObserver> e = observed.head(), mapEnd = observed.tail(); (e = e.getNext()) != mapEnd;) {
			ActionObserver observer = observed.remove(e.getKey());
			Player player = World.getInstance().findPlayer(e.getKey());
			if (player != null)
				player.getObserveController().removeObserver(observer);
		}
	}

}
