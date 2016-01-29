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

package org.typezero.gameserver.model.gameobjects;

import org.apache.commons.lang.StringUtils;

import org.typezero.gameserver.controllers.NpcController;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.NpcLifeStats;
import org.typezero.gameserver.model.stats.container.SummonedObjectGameStats;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author ATracer, modified Rolandas
 */
public class SummonedObject<T extends VisibleObject> extends Npc {

	private byte level;

	/**
	 * Creator of this SummonedObject
	 */
	private T creator;

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param level
	 */
	public SummonedObject(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate,
		byte level) {
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.level = level;
	}

	@Override
	protected void setupStatContainers(byte level) {
		setGameStats(new SummonedObjectGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	@Override
	public byte getLevel() {
		return this.level;
	}

	@Override
	public T getCreator() {
		return creator;
	}

	public void setCreator(T creator) {
		if (creator instanceof Player)
			((Player) creator).setSummonedObj(this);
		this.creator = creator;
	}

	@Override
	public String getMasterName() {
		return creator != null ? creator.getName() : StringUtils.EMPTY;
	}

	@Override
	public int getCreatorId() {
		return creator != null ? creator.getObjectId() : 0;
	}

	@Override
	public Creature getActingCreature() {
		if (creator instanceof Creature)
			return (Creature) getCreator();
		return this;
	}

	@Override
	public Creature getMaster() {
		if (creator instanceof Creature)
			return (Creature) getCreator();
		return this;
	}

	@Override
	public int getType(Creature creature) {
		return creature.isEnemy(getMaster()) ? CreatureType.ATTACKABLE.getId() : CreatureType.SUPPORT.getId();
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return getMaster() != null ? getMaster().isEnemy(creature) : false;
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return getMaster() != null ? getMaster().isEnemyFrom(npc) : false;
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return getMaster() != null ? getMaster().isEnemyFrom(player) : false;
	}

	@Override
	public TribeClass getTribe() {
		if (getMaster() == null)
			return ((NpcTemplate)objectTemplate).getTribe();
		return getMaster().getTribe();
	}

	@Override
	public Race getRace() {
		if (creator instanceof Creature) {
			return creator != null ? ((Creature) creator).getRace() : Race.NONE;
		}
		return super.getRace();
	}

}
