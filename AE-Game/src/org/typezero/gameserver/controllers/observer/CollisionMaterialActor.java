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

import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.geoEngine.collision.CollisionIntention;
import org.typezero.gameserver.geoEngine.collision.CollisionResult;
import org.typezero.gameserver.geoEngine.collision.CollisionResults;
import org.typezero.gameserver.geoEngine.math.Vector3f;
import org.typezero.gameserver.geoEngine.scene.Spatial;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.materials.MaterialActTime;
import org.typezero.gameserver.model.templates.materials.MaterialSkill;
import org.typezero.gameserver.model.templates.materials.MaterialTemplate;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.services.WeatherService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.gametime.DayTime;
import org.typezero.gameserver.utils.gametime.GameTime;
import org.typezero.gameserver.utils.gametime.GameTimeManager;
import org.typezero.gameserver.world.zone.ZoneInstance;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Rolandas
 */
public class CollisionMaterialActor extends AbstractCollisionObserver implements IActor {

	private MaterialTemplate actionTemplate;
	private AtomicReference<MaterialSkill> currentSkill = new AtomicReference<MaterialSkill>();

	public CollisionMaterialActor(Creature creature, Spatial geometry, MaterialTemplate actionTemplate) {
		super(creature, geometry, CollisionIntention.MATERIAL.getId());
		this.actionTemplate = actionTemplate;
	}

	private MaterialSkill getSkillForTarget(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			if (player.isProtectionActive())
				return null;
		}

		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : actionTemplate.getSkills()) {
			if (skill.getTarget().isTarget(creature)) {
				foundSkill = skill;
				break;
			}
		}
		if (foundSkill == null)
			return null;

		int weatherCode = -1;
		if (creature.getActiveRegion() == null)
			return null;
		List<ZoneInstance> zones = creature.getActiveRegion().getZones(creature);
		for (ZoneInstance regionZone : zones) {
			if (regionZone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER) {
				Vector3f center = geometry.getWorldBound().getCenter();
				if (!regionZone.getAreaTemplate().isInside3D(center.x, center.y, center.z))
					continue;
				int weatherZoneId = DataManager.ZONE_DATA.getWeatherZoneId(regionZone.getZoneTemplate());
				weatherCode = WeatherService.getInstance().getWeatherCode(creature.getWorldId(), weatherZoneId);
				break;
			}
		}

		boolean dependsOnWeather = geometry.getName().indexOf("WEATHER") != -1;
		// TODO: fix it
		if (dependsOnWeather && weatherCode > 0)
			return null; // not active in any weather (usually, during rain and after rain, not before)

		if (foundSkill.getTime() == null)
			return foundSkill;

		GameTime gameTime = (GameTime) GameTimeManager.getGameTime().clone();
		if (foundSkill.getTime() == MaterialActTime.DAY && weatherCode == 0)
			return foundSkill; // Sunny day, according to client data

		if (gameTime.getDayTime() == DayTime.NIGHT) {
			if (foundSkill.getTime() == MaterialActTime.NIGHT)
				return foundSkill;
		}
		else
			return foundSkill;

		return null;
	}

	@Override
	public void onMoved(CollisionResults collisionResults) {
		if (collisionResults.size() == 0) {
			return;
		}
		else {
			if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
				Player player = (Player) creature;
				if (player.isGM()) {
					CollisionResult result = collisionResults.getClosestCollision();
					PacketSendUtility.sendMessage(player, "Entered " + result.getGeometry().getName());
				}
			}
			act();
		}
	}

	@Override
	public void act() {
		final MaterialSkill actSkill = getSkillForTarget(creature);
		if (currentSkill.getAndSet(actSkill) != actSkill) {
			if (actSkill == null)
				return;
			if (creature.getEffectController().hasAbnormalEffect(actSkill.getId())) {
				return;
			}
			Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if (!creature.getEffectController().hasAbnormalEffect(actSkill.getId())) {
						if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
							Player player = (Player) creature;
							if (player.isGM()) {
								PacketSendUtility.sendMessage(player, "Use skill=" + actSkill.getId());
							}
						}
						Skill skill = SkillEngine.getInstance().getSkill(creature, actSkill.getId(), actSkill.getSkillLevel(), creature);
						skill.getEffectedList().add(creature);
						skill.useWithoutPropSkill();
					}
				}
			}, 0, (long) (actSkill.getFrequency() * 1000));
			creature.getController().addTask(TaskId.MATERIAL_ACTION, task);
		}
	}

	@Override
	public void abort() {
		Future<?> existingTask = creature.getController().getTask(TaskId.MATERIAL_ACTION);
		if (existingTask != null) {
			creature.getController().cancelTask(TaskId.MATERIAL_ACTION);
		}
		currentSkill.set(null);
	}

	@Override
	public void died(Creature creature) {
		abort();
	}

	@Override
	public void setEnabled(boolean enable) {
		// TODO Auto-generated method stub
	};

}
