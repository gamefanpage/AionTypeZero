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


package org.typezero.gameserver.world.zone.handler;

import javolution.util.FastMap;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.CollisionMaterialActor;
import org.typezero.gameserver.controllers.observer.IActor;
import org.typezero.gameserver.geoEngine.scene.Spatial;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.templates.materials.MaterialSkill;
import org.typezero.gameserver.model.templates.materials.MaterialTemplate;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author Rolandas
 */
public class MaterialZoneHandler implements ZoneHandler {

	FastMap<Integer, IActor> observed = new FastMap<Integer, IActor>();

	private Spatial geometry;
	private MaterialTemplate template;
	private boolean actOnEnter = false;
	private Race ownerRace = Race.NONE;

	public MaterialZoneHandler(Spatial geometry, MaterialTemplate template) {
		this.geometry = geometry;
		this.template = template;
		String name = geometry.getName();
		if (name.indexOf("FIRE_BOX") != -1 || name.indexOf("FIRE_SEMISPHERE") != -1 || name.indexOf("FIREPOT") != -1 ||
			name.indexOf("FIRE_CYLINDER") != -1 || name.indexOf("FIRE_CONE") != -1 || name.startsWith("BU_H_CENTERHALL"))
			actOnEnter = true;
		if (name.startsWith("BU_AB_DARKSP"))
			ownerRace = Race.ASMODIANS;
		else if (name.startsWith("BU_AB_LIGHTSP"))
			ownerRace = Race.ELYOS;
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		if (ownerRace == creature.getRace())
			return;
		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : template.getSkills()) {
			if (skill.getTarget().isTarget(creature)) {
				foundSkill = skill;
				break;
			}
		}
		if (foundSkill == null)
			return;
		CollisionMaterialActor actor = new CollisionMaterialActor(creature, geometry, template);
		creature.getObserveController().addObserver(actor);
		observed.put(creature.getObjectId(), actor);
		if (actOnEnter)
			actor.act();
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		IActor actor = observed.get(creature.getObjectId());
		if (actor != null) {
			creature.getObserveController().removeObserver((ActionObserver) actor);
			observed.remove(creature.getObjectId());
			actor.abort();
		}
	}

}
