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

package org.typezero.gameserver.world.zone;

import javolution.util.FastMap;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.geometry.Area;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.model.templates.zone.ZoneInfo;
import org.typezero.gameserver.model.templates.zone.ZoneTemplate;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.zone.handler.AdvencedZoneHandler;
import org.typezero.gameserver.world.zone.handler.ZoneHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ATracer
 */
public class ZoneInstance implements Comparable<ZoneInstance> {

	private ZoneInfo template;
	private int mapId;
	private Map<Integer, Creature> creatures = new FastMap<Integer, Creature>();
	protected List<ZoneHandler> handlers = new ArrayList<ZoneHandler>();

	public ZoneInstance(int mapId, ZoneInfo template) {
		this.template = template;
		this.mapId = mapId;
	}

	/**
	 * @return the template
	 */
	public Area getAreaTemplate() {
		return template.getArea();
	}

	/**
	 * @return the template
	 */
	public ZoneTemplate getZoneTemplate() {
		return template.getZoneTemplate();
	}

	public boolean revalidate(Creature creature) {
		return (mapId == creature.getWorldId() && template.getArea().isInside3D(creature.getX(), creature.getY(), creature.getZ()));
	}

	public synchronized boolean onEnter(Creature creature) {
		if (creatures.containsKey(creature.getObjectId()))
			return false;
		creatures.put(creature.getObjectId(), creature);
		if (creature instanceof Player)
			creature.getController().onEnterZone(this);
		for (int i = 0; i < handlers.size(); i++)
			handlers.get(i).onEnterZone(creature, this);
		return true;
	}

	public synchronized boolean onLeave(Creature creature) {
		if (!creatures.containsKey(creature.getObjectId()))
			return false;
		creatures.remove(creature.getObjectId());
		creature.getController().onLeaveZone(this);
		for (int i = 0; i < handlers.size(); i++)
			handlers.get(i).onLeaveZone(creature, this);
		return true;
	}

	public boolean onDie(Creature attacker, Creature target) {
		if (!creatures.containsKey(target.getObjectId()))
			return false;
		for (int i = 0; i < handlers.size(); i++) {
			ZoneHandler handler = handlers.get(i);
			if (handler instanceof AdvencedZoneHandler) {
				if (((AdvencedZoneHandler) handler).onDie(attacker, target, this))
					return true;
			}
		}
		return false;
	}

	public boolean isInsideCreature(Creature creature) {
		return creatures.containsKey(creature.getObjectId());
	}

	public boolean isInsideCordinate(float x, float y, float z) {
		return template.getArea().isInside3D(x, y, z);
	}

	@Override
	public int compareTo(ZoneInstance o) {
		int result = getZoneTemplate().getPriority() - o.getZoneTemplate().getPriority();
		if (result == 0) {
			return template.getZoneTemplate().getName().id() - o.template.getZoneTemplate().getName().id();
		}
		return result;
	}

	public void addHandler(ZoneHandler handler) {
		this.handlers.add(handler);
	}

	public boolean canFly() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.FLY))
			return World.getInstance().getWorldMap(mapId).isPossibleFly();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.FLY.getId()) != 0;
	}

	public boolean canGlide() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.GLIDE))
			return World.getInstance().getWorldMap(mapId).canGlide();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.GLIDE.getId()) != 0;
	}

	public boolean canPutKisk() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.BIND))
			return World.getInstance().getWorldMap(mapId).canPutKisk();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.BIND.getId()) != 0;
	}

	public boolean canRecall() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.RECALL))
			return World.getInstance().getWorldMap(mapId).canRecall();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.RECALL.getId()) != 0;
	}

	public boolean canRide() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.RIDE))
			return World.getInstance().getWorldMap(mapId).canRide();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.RIDE.getId()) != 0;
	}

	public boolean canFlyRide() {
		if (template.getZoneTemplate().getFlags() == -1 || template.getZoneTemplate().getFlags() == 0|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.FLY_RIDE))
			return World.getInstance().getWorldMap(mapId).canFlyRide();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.FLY_RIDE.getId()) != 0;
	}

	public boolean isPvpAllowed() {
		if (template.getZoneTemplate().getZoneType() != ZoneClassName.PVP)
			return World.getInstance().getWorldMap(mapId).isPvpAllowed();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.PVP_ENABLED.getId()) != 0;
	}

	public boolean isSameRaceDuelsAllowed() {
		if (template.getZoneTemplate().getZoneType() != ZoneClassName.DUEL || template.getZoneTemplate().getFlags() == 0
			|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.DUEL_SAME_RACE_ENABLED))
			return World.getInstance().getWorldMap(mapId).isSameRaceDuelsAllowed();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.DUEL_SAME_RACE_ENABLED.getId()) != 0;
	}

	public boolean isOtherRaceDuelsAllowed() {
		if (template.getZoneTemplate().getZoneType() != ZoneClassName.DUEL || template.getZoneTemplate().getFlags() == 0
			|| World.getInstance().getWorldMap(mapId).hasOverridenOption(ZoneAttributes.DUEL_OTHER_RACE_ENABLED))
			return World.getInstance().getWorldMap(mapId).isOtherRaceDuelsAllowed();
		return (template.getZoneTemplate().getFlags() & ZoneAttributes.DUEL_OTHER_RACE_ENABLED.getId()) != 0;
	}

	public int getTownId() {
		return template.getZoneTemplate().getTownId();
	}

	/**
	 * @return the creatures
	 */
	public Map<Integer, Creature> getCreatures() {
		return creatures;
	}
}
