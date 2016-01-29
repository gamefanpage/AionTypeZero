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
package org.typezero.gameserver.services.siegeservice;

import org.typezero.gameserver.model.gameobjects.siege.SiegeNpc;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeRace;
import java.util.concurrent.Future;

/**
 * @author Luzien
 */
public abstract class Assault<siege extends Siege<?>> {

	protected final SiegeLocation siegeLocation;
	protected final int locationId;
	protected final SiegeNpc boss;
	protected final int worldId;

	protected Future<?> dredgionTask;
	protected Future<?> spawnTask;

	public Assault(Siege<?> siege) {
		this.siegeLocation = siege.getSiegeLocation();
		this.boss = siege.getBoss();
		this.locationId = siege.getSiegeLocationId();
		this.worldId = siege.getSiegeLocation().getWorldId();
	}

	public int getWorldId() {
		return worldId;
	}

	public void startAssault(int delay) {
		scheduleAssault(delay);
	}

	public void finishAssault(boolean captured) {
		if (dredgionTask != null && !dredgionTask.isDone())
			dredgionTask.cancel(true);
		if (spawnTask != null && !spawnTask.isDone())
			spawnTask.cancel(true);

		onAssaultFinish(captured && siegeLocation.getRace().equals(SiegeRace.BALAUR));
	}

	protected abstract void onAssaultFinish(boolean captured);

	protected abstract void scheduleAssault(int delay);

}
