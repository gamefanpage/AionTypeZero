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
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.HomingGameStats;
import org.typezero.gameserver.model.stats.container.NpcLifeStats;
import org.typezero.gameserver.model.templates.item.ItemAttackType;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;

/**
 * @author ATracer
 */
public class Homing extends SummonedObject<Creature> {

	/**
	 * Number of performed attacks
	 */
	private int attackCount;

	private int skillId;

	/**
	 * Skill id of this homing.
	 * 0 - usually attack, other - skills.
	 */
	private int activeSkillId;

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param level
	 */
	public Homing(int objId, NpcController controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level, int skillId) {
		super(objId, controller, spawnTemplate, objectTemplate, level);
		this.skillId = skillId;
	}

	@Override
	protected void setupStatContainers(byte level) {
		setGameStats(new HomingGameStats(this));
		setLifeStats(new NpcLifeStats(this));
	}

	/**
	 * @param attackCount
	 *          the attackCount to set
	 */
	public void setAttackCount(int attackCount) {
		this.attackCount = attackCount;
	}

	/**
	 * @return the attackCount
	 */
	public int getAttackCount() {
		return attackCount;
	}

	/**
	 * @return NpcObjectType.HOMING
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.HOMING;
	}

	@Override
	public String getMasterName() {
		return StringUtils.EMPTY;
	}

	@Override
	public ItemAttackType getAttackType() {
		if (getName().contains("fire"))
			return ItemAttackType.MAGICAL_FIRE;
		else if (getName().contains("stone"))
			return ItemAttackType.MAGICAL_EARTH;
		else if (getName().contains("water"))
			return ItemAttackType.MAGICAL_WATER;
		else if ((getName().contains("wind")) || (getName().contains("cyclone")))
			return ItemAttackType.MAGICAL_WIND;
		return ItemAttackType.PHYSICAL;
	}

	public int getSkillId() {
		return skillId;
	}

	public int getActiveSkillId() {
		return activeSkillId;
	}

	public void setActiveSkillId(int activeSkillId) {
		this.activeSkillId = activeSkillId;
	}

}
