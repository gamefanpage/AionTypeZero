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


package org.typezero.gameserver.services.base;

import com.aionemu.commons.callbacks.EnhancedObject;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.base.BaseLocation;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.npc.NpcTemplate;
import org.typezero.gameserver.model.templates.npc.NpcTemplateType;
import org.typezero.gameserver.model.templates.spawns.SpawnGroup2;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.templates.spawns.basespawns.BaseSpawnTemplate;
import org.typezero.gameserver.services.BaseService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.spawnengine.SpawnHandlerType;
import org.typezero.gameserver.utils.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Source
 * @author Dision
 * M.O.G. Devs Team
 */
public class Base<BL extends BaseLocation> {

	private Future<?> startAssault, stopAssault;
	private final BL baseLocation;
	private List<Race> list = new ArrayList<Race>();
	private final BossDeathListener bossDeathListener = new BossDeathListener(this);
	private List<Npc> attackers = new ArrayList<Npc>();
	private List<Npc> spawned = new CopyOnWriteArrayList<Npc>();
	private final AtomicBoolean finished = new AtomicBoolean();
	private boolean started;
	private Npc boss, guard, guard_village, guard_rivar, guard_krall, guard_werewolf, portal, flag;

	public Base(BL baseLocation) {
		list.add(Race.ASMODIANS);
		list.add(Race.ELYOS);
		list.add(Race.NPC);
		this.baseLocation = baseLocation;
	}

	public final void start() {

		boolean start = false;

		synchronized (this) {
			if (started) {
				start = true;
			} else {
		  started = true;
			}
		}
		if (start) {
			return;
		}
		spawn();
	}

	public final void stop() {
		if (finished.compareAndSet(false, true)) {
			if (getBoss() != null) {
				rmvBossListener();
			}
			despawn();
		}
	}

	private List<SpawnGroup2> getBaseSpawns() {
		List<SpawnGroup2> spawns = DataManager.SPAWNS_DATA2.getBaseSpawnsByLocId(getId());

		if (spawns == null) {
			throw new NullPointerException("No spawns for base:" + getId());
		}

		return spawns;
	}

	protected void spawn() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() == null) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						NpcTemplate npcTemplate = npc.getObjectTemplate();
						if (npcTemplate.getNpcTemplateType().equals(NpcTemplateType.FLAG)) {
							setFlag(npc);
						}
						getSpawned().add(npc);
					}
				}
			}
		}

		delayedAssault();
		delayedSpawn(getRace());
		delayedSpawnTwo(getRace());
		delayedSpawnThri(getRace());
	}

	private void delayedAssault() {
		startAssault = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				chooseAttackersRace();
			}

		}, Rnd.get(15, 20) * 60000); // Randomly every 15 - 20 min start assault
	}

	private void delayedSpawn(final Race race) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getRace().equals(race) && getBoss() == null) {
					spawnBoss();
				}
			}

		}, 30 * 60000); // Boss 30 min spawn delay
	}

	protected void spawnBoss() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.BOSS)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setBoss(npc);
						addBossListeners();
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	protected void chooseAttackersRace() {
		AtomicBoolean next = new AtomicBoolean(Math.random() < 0.5);
		for (Race race : list) {
			if (!race.equals(getRace())) {
				if (next.compareAndSet(true, false)) {
					continue;
				}
				spawnAttackers(race);
				break;
			}
		}
	}

	private void delayedSpawnTwo(final Race race) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getRace().equals(race) && getGuard() == null) {
					spawnGuard();
				}
				if (getRace().equals(race) && getGuardRivar() == null) {
					spawnGuardRivar();
				}
				if (getRace().equals(race) && getGuardKrall() == null) {
					spawnGuardKrall();
				}
				if (getRace().equals(race) && getGuardWerewolf() == null) {
					spawnGuardWerewolf();
				}
				if (getRace().equals(race) && getPortal() == null) {
					spawnPortal();
				}
			}

		}, 1 * 6000); // Guard 1 min spawn delay
	}

	protected void spawnGuard() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.GUARD)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setGuard(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	protected void spawnGuardRivar() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {

					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.GUARD_RIVAR)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setGuardRivar(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	protected void spawnGuardKrall() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.GUARD_KRALL)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setGuardKrall(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	protected void spawnGuardWerewolf() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.GUARD_WEREWOLF)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setGuardWerewolf(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	private void delayedSpawnThri(final Race race) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				if (getRace().equals(race) && getGuardVillage() == null) {
					spawnGuardVillage();
				}
			}

		}, 2 * 6000); // Guard Village 2 min spawn delay
	}

	protected void spawnGuardVillage() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.GUARD_VILLAGE)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setGuardVillage(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	protected void spawnPortal() {
		for (SpawnGroup2 group : getBaseSpawns()) {
			for (SpawnTemplate spawn : group.getSpawnTemplates()) {
				final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
				if (template.getBaseRace().equals(getRace())) {
					if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.PORTAL)) {
						Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
						setPortal(npc);
						getSpawned().add(npc);
					}
				}
			}
		}
	}

	public void spawnAttackers(Race race) {
		if (getFlag() == null) {
			throw new NullPointerException("Base:" + getId() + " flag is null!");
		}
		else if (!getFlag().getPosition().getMapRegion().isMapRegionActive()) {
			// 20% chance to capture base in not active region by invaders assault
			if (Math.random() < 0.2) {
				BaseService.getInstance().capture(getId(), race);
			}
			else {
				// Next attack
				delayedAssault();
			}
			return;
		}

		if (!isAttacked()) {
			getAttackers().clear();

			for (SpawnGroup2 group : getBaseSpawns()) {
				for (SpawnTemplate spawn : group.getSpawnTemplates()) {
					final BaseSpawnTemplate template = (BaseSpawnTemplate) spawn;
					if (template.getBaseRace().equals(race)) {
						if (template.getHandlerType() != null && template.getHandlerType().equals(SpawnHandlerType.ATTACKER)) {
							Npc npc = (Npc) SpawnEngine.spawnObject(template, 1);
							getAttackers().add(npc);
						}
					}
				}
			}

			if (getAttackers().isEmpty()) {
				throw new NullPointerException("No attackers was found for base:" + getId());
			}
			else {
				stopAssault = ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						despawnAttackers();

						// Next attack
						delayedAssault();
					}

				}, 5 * 60000); // After 5 min attackers despawned
			}
		}
	}

	public boolean isAttacked() {
		for (Npc attacker : getAttackers()) {
			if (!attacker.getLifeStats().isAlreadyDead()) {
				return true;
			}
		}
		return false;
	}

	protected void despawn() {
		setFlag(null);

		for (Npc npc : getSpawned()) {
			npc.getController().cancelTask(TaskId.RESPAWN);
			npc.getController().onDelete();
		}
		getSpawned().clear();

		despawnAttackers();
		if (startAssault != null) {
			startAssault.cancel(true);
		}
		if (stopAssault != null) {
			stopAssault.cancel(true);
		}
	}

	protected void despawnAttackers() {
		for (Npc attacker : getAttackers()) {
			attacker.getController().cancelTask(TaskId.RESPAWN);
			attacker.getController().onDelete();
		}
		getAttackers().clear();
	}

	protected void addBossListeners() {
		AbstractAI ai = (AbstractAI) getBoss().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.addCallback(getBossListener());
	}

	protected void rmvBossListener() {
		AbstractAI ai = (AbstractAI) getBoss().getAi2();
		EnhancedObject eo = (EnhancedObject) ai;
		eo.removeCallback(getBossListener());
	}

	/**
	 * @return
	 * @return
	 */
	public Npc getFlag() {
		return flag;
	}

	public void setFlag(Npc flag) {
		this.flag = flag;
	}

	public Npc getBoss() {
		return boss;
	}

	public void setBoss(Npc boss) {
		this.boss = boss;
	}

	public Npc getGuard() {
		return guard;
	}

	public void setGuard(Npc guard) {
		this.guard = guard;
	}

	public Npc getGuardRivar() {
		return guard_rivar;
	}

	public void setGuardRivar(Npc guard_rivar) {
		this.guard_rivar = guard_rivar;
	}

	public Npc getGuardKrall() {
		return guard_krall;
	}

	public void setGuardKrall(Npc guard_krall) {
		this.guard_krall = guard_krall;
	}

	public Npc getGuardWerewolf() {
		return guard_werewolf;
	}

	public void setGuardWerewolf(Npc guard_werewolf) {
		this.guard_werewolf = guard_werewolf;
	}

	public Npc getGuardVillage() {
		return guard_village;
	}

	public void setGuardVillage(Npc guard_village) {
		this.guard_village = guard_village;
	}

	public Npc getPortal() {
		return portal;
	}

	public void setPortal(Npc portal) {
		this.portal = portal;
	}

	public BossDeathListener getBossListener() {
		return bossDeathListener;
	}

	public boolean isFinished() {
		return finished.get();
	}

	public BL getBaseLocation() {
		return baseLocation;
	}

	public int getId() {
		return baseLocation.getId();
	}

	public Race getRace() {
		return baseLocation.getRace();
	}

	public void setRace(Race race) {
		baseLocation.setRace(race);
	}

	public List<Npc> getAttackers() {
		return attackers;
	}

	public List<Npc> getSpawned() {
		return spawned;
	}

}
