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

import org.typezero.gameserver.ai2.AI2Engine;
import org.typezero.gameserver.controllers.CreatureController;
import org.typezero.gameserver.controllers.SummonController;
import org.typezero.gameserver.controllers.attack.AggroList;
import org.typezero.gameserver.controllers.attack.PlayerAggroList;
import org.typezero.gameserver.controllers.movement.SiegeWeaponMoveController;
import org.typezero.gameserver.controllers.movement.SummonMoveController;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TribeClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.SummonGameStats;
import org.typezero.gameserver.model.stats.container.SummonLifeStats;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.stats.SummonStatsTemplate;
import org.typezero.gameserver.world.WorldPosition;

import java.util.concurrent.Future;

/**
 * @author ATracer
 */
public class Summon extends Creature {

	private Player master;
	private SummonMode mode = SummonMode.GUARD;
	private byte level;
	private int liveTime;
	private Future<?> releaseTask;

	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param position
	 * @param level
	 */
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate,
			NpcTemplate objectTemplate, byte level, int time) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition());
		controller.setOwner(this);
		String ai = objectTemplate.getAi();
		AI2Engine.getInstance().setupAI(ai, this);
		moveController = ai.equals("siege_weapon") ? new SiegeWeaponMoveController(this) : new SummonMoveController(this);
		this.level = level;
		this.liveTime = time;
		SummonStatsTemplate statsTemplate = DataManager.SUMMON_STATS_DATA.getSummonTemplate(objectTemplate.getTemplateId(),
			level);
		setGameStats(new SummonGameStats(this, statsTemplate));
		setLifeStats(new SummonLifeStats(this));
	}

	@Override
	protected AggroList createAggroList() {
		return new PlayerAggroList(this);
	}

	@Override
	public SummonGameStats getGameStats() {
		return (SummonGameStats) super.getGameStats();
	}

	@Override
	public Player getMaster() {
		return master;
	}

	/**
	 * @param master
	 *          the master to set
	 */
	public void setMaster(Player master) {
		this.master = master;
	}

	@Override
	public String getName() {
		return objectTemplate.getName();
	}

	/**
	 * @return the level
	 */
	@Override
	public byte getLevel() {
		return level;
	}

	@Override
	public NpcTemplate getObjectTemplate() {
		return (NpcTemplate) super.getObjectTemplate();
	}

	public int getNpcId() {
		return getObjectTemplate().getTemplateId();
	}

	public int getNameId() {
		return getObjectTemplate().getNameId();
	}

	/**
	 * @return NpcObjectType.SUMMON
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.SUMMON;
	}

	@Override
	public SummonController getController() {
		return (SummonController) super.getController();
	}

	/**
	 * @return the mode
	 */
	public SummonMode getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *          the mode to set
	 */
	public void setMode(SummonMode mode) {
		this.mode = mode;
	}

	@Override
	public boolean isEnemy(Creature creature) {
		return master != null ? master.isEnemy(creature) : false;
	}

	@Override
	public boolean isEnemyFrom(Npc npc) {
		return master != null ? master.isEnemyFrom(npc) : false;
	}

	@Override
	public boolean isEnemyFrom(Player player) {
		return master != null ? master.isEnemyFrom(player) : false;
	}

	@Override
	public TribeClass getTribe() {
		if (master == null)
			return ((NpcTemplate)objectTemplate).getTribe();
		return master.getTribe();
	}

	@Override
	public SummonMoveController getMoveController() {
		return (SummonMoveController) super.getMoveController();
	}

	@Override
	public Creature getActingCreature() {
		return getMaster() == null ? this : getMaster();
	}

	@Override
	public Race getRace() {
		return getMaster() != null ? getMaster().getRace() : Race.NONE;
	}

	/**
	 * @return liveTime in sec.
	 */
	public int getLiveTime() {
		return liveTime;
	}

	/**
	 * @param liveTime in sec.
	 */
	public void setLiveTime(int liveTime) {
		this.liveTime = liveTime;
	}

	public void setReleaseTask(Future<?> task) {
		releaseTask = task;
	}

	public void cancelReleaseTask() {
		if (releaseTask != null && !releaseTask.isDone()) {
			releaseTask.cancel(true);
		}
	}
}
