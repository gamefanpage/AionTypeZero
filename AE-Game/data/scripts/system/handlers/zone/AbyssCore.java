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

package zone;

import java.io.IOException;

import javolution.util.FastMap;

import org.typezero.gameserver.controllers.observer.CollisionDieActor;
import org.typezero.gameserver.geoEngine.GeoWorldLoader;
import org.typezero.gameserver.geoEngine.math.Matrix3f;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.geoEngine.scene.Node;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.handler.ZoneHandler;
import org.typezero.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation("CORE_400010000")
public class AbyssCore implements ZoneHandler {

	FastMap<Integer, CollisionDieActor> observed = new FastMap<Integer, CollisionDieActor>();

	private Node geometry;

	public AbyssCore() {
		try {
			this.geometry = (Node) GeoWorldLoader.loadMeshs("data/geo/models/na_ab_lmark_col_01a.mesh").values().toArray()[0];
			this.geometry.setTransform(new Matrix3f(1.15f, 0, 0, 0, 1.15f, 0, 0, 0, 1.15f),
																 new Vector3f(2140.104f, 1925.5823f, 2303.919f), 1f);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		geometry.updateModelBound();
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		Creature acting = creature.getActingCreature();
		if (acting instanceof Player && !((Player) acting).isGM()) {

			CollisionDieActor observer = new CollisionDieActor(creature, geometry);
			creature.getObserveController().addObserver(observer);
			observed.put(creature.getObjectId(), observer);
		}
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		Creature acting = creature.getActingCreature();
		if (acting instanceof Player && !((Player) acting).isGM()) {
			CollisionDieActor observer = observed.get(creature.getObjectId());
			if (observer != null) {
				creature.getObserveController().removeObserver(observer);
				observed.remove(creature.getObjectId());
			}
		}
	}

}
