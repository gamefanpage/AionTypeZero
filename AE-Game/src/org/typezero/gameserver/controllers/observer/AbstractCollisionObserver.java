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

package org.typezero.gameserver.controllers.observer;

import org.typezero.gameserver.geoEngine.collision.CollisionResults;
import org.typezero.gameserver.geoEngine.math.Ray;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.geoEngine.scene.Spatial;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author MrPoke
 * @moved Rolandas
 */
public abstract class AbstractCollisionObserver extends ActionObserver {

	protected Creature creature;
	protected Vector3f oldPos;
	protected Spatial geometry;
	protected byte intentions;
	private AtomicBoolean isRunning = new AtomicBoolean();

	public AbstractCollisionObserver(Creature creature, Spatial geometry, byte intentions) {
		super(ObserverType.MOVE_OR_DIE);
		this.creature = creature;
		this.geometry = geometry;
		this.oldPos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
		this.intentions = intentions;
	}

	@Override
	public void moved() {
		if (!isRunning.getAndSet(true)) {
			ThreadPoolManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					try {
						Vector3f pos = new Vector3f(creature.getX(), creature.getY(), creature.getZ());
						Vector3f dir = oldPos.clone();
						Float limit = pos.distance(dir);
						dir.subtractLocal(pos).normalizeLocal();
						Ray r = new Ray(pos, dir);
						r.setLimit(limit);
						CollisionResults results = new CollisionResults(intentions, true, creature.getInstanceId());
						geometry.collideWith(r, results);
						onMoved(results);
						oldPos = pos;
					}
					finally {
						isRunning.set(false);
					}
				}
			});
		}
	}

	public abstract void onMoved(CollisionResults result);
}
