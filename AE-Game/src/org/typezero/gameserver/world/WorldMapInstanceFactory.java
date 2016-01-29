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

package org.typezero.gameserver.world;

import org.typezero.gameserver.instance.InstanceEngine;
import org.typezero.gameserver.instance.handlers.InstanceHandler;

/**
 * @author ATracer
 */
public class WorldMapInstanceFactory {

	/**
	 * @param parent
	 * @param instanceId
	 * @return
	 */
	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId) {
		return createWorldMapInstance(parent, instanceId, 0);
	}

	public static WorldMapInstance createWorldMapInstance(WorldMap parent, int instanceId, int ownerId) {
		WorldMapInstance worldMapInstance = null;
		if (parent.getMapId() == WorldMapType.RESHANTA.getId()) {
			worldMapInstance = new WorldMap3DInstance(parent, instanceId);
		}
		else {
			worldMapInstance = new WorldMap2DInstance(parent, instanceId, ownerId);
		}
		InstanceHandler instanceHandler = InstanceEngine.getInstance().getNewInstanceHandler(parent.getMapId());
		worldMapInstance.setInstanceHandler(instanceHandler);
		return worldMapInstance;
	}
}
