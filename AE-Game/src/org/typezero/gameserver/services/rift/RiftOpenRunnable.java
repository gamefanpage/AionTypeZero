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

package org.typezero.gameserver.services.rift;

import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.services.RiftService;
import org.typezero.gameserver.utils.ThreadPoolManager;
import java.util.Map;

/**
 * @author Source
 */
public class RiftOpenRunnable implements Runnable {

	private final int worldId;
	private final boolean guards;

	public RiftOpenRunnable(int worldId, boolean guards) {
		this.worldId = worldId;
		this.guards = guards;
	}

	@Override
	public void run() {
		Map<Integer, RiftLocation> locations = RiftService.getInstance().getRiftLocations();
		for (RiftLocation loc : locations.values()) {
			if (loc.getWorldId() == worldId) {
				RiftService.getInstance().openRifts(loc, guards);
			}
		}

		// Scheduled rifts close
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				RiftService.getInstance().closeRifts();
			}

		}, RiftService.getInstance().getDuration() * 3540 * 1000);
		// Broadcast rift spawn on map
		RiftInformer.sendRiftsInfo(worldId);
	}

}
