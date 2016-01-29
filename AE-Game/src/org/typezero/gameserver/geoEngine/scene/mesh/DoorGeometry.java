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

package org.typezero.gameserver.geoEngine.scene.mesh;

import java.util.BitSet;

import org.typezero.gameserver.geoEngine.collision.Collidable;
import org.typezero.gameserver.geoEngine.collision.CollisionResults;
import org.typezero.gameserver.geoEngine.math.Ray;
import org.typezero.gameserver.geoEngine.scene.Geometry;
import org.typezero.gameserver.geoEngine.scene.Mesh;

/**
 * @author MrPoke, Rolandas
 */
public class DoorGeometry extends Geometry {

	BitSet instances = new BitSet();
	private boolean foundTemplate = false;

	public DoorGeometry(String name, Mesh mesh) {
		super(name, mesh);
	}

	public void setDoorState(int instanceId, boolean isOpened) {
		instances.set(instanceId, isOpened);
	}

	@Override
	public int collideWith(Collidable other, CollisionResults results) {
		if (foundTemplate && instances.get(results.getInstanceId()))
			return 0;
		if (other instanceof Ray) {
			// no collision if inside arena spheres, so just check volume
			return getWorldBound().collideWith(other, results);
		}
		return super.collideWith(other, results);
	}

	public boolean isFoundTemplate() {
		return foundTemplate;
	}

	public void setFoundTemplate(boolean foundTemplate) {
		this.foundTemplate = foundTemplate;
	}

	@Override
	public void updateModelBound() {
		// duplicate call distorts world bounds, thus do only once
		if (worldBound == null) {
			mesh.updateBound();
			worldBound = getModelBound().transform(cachedWorldMat, worldBound);
		}
	}
}
