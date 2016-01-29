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

package ai.instance.tiamatStrongHold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.PlayerActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.skillengine.model.Skill;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Cheatkiller
 * @modified Luzien
 *
 */
@AIName("brigadegeneraltahabata")
public class BrigadeGeneralTahabataAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private AtomicBoolean isEndPiercingStrike = new AtomicBoolean(true);
	private Future<?> piercingStrikeTask;
	private AtomicBoolean isEndFireStorm = new AtomicBoolean(true);
	private Future<?> fireStormTask;
	protected List<Integer> percents = new ArrayList<Integer>();

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false) && getPosition().getWorldMapInstance().getDoors().get(610) != null) {
			getPosition().getWorldMapInstance().getDoors().get(610).setOpen(false);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void startPiercingStrikeTask() {
		piercingStrikeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead())
					cancelPiercingStrike();
				else {
					startPiercingStrikeEvent();
				}
			}

		}, 15000, 20000);
	}

	private void startFireStormTask() {
		fireStormTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (isAlreadyDead())
					cancelFireStorm();
				else {
					startFireStormEvent();
				}
			}

		}, 10000, 20000);
	}

	private void startFireStormEvent() {
		if (getPosition().getWorldMapInstance().getNpc(283045) == null) {
			AI2Actions.useSkill(this, 20758);
			rndSpawn(283045, Rnd.get(1, 4));
		}
	}

	private void startPiercingStrikeEvent() {
		teleportRandomPlayer();
		Skill skill = SkillEngine.getInstance().getSkill(getOwner(), 20754, 60, getOwner());
		if (skill != null) {
			skill.useNoAnimationSkill();
			if (!skill.getEffectedList().isEmpty())
				useMultiSkill();
		}
	}

	private void useMultiSkill() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				AI2Actions.useSkill(BrigadeGeneralTahabataAI2.this, 20755);
			}

		}, 3000);
	}

	private void teleportRandomPlayer() {
		List<Player> players = new ArrayList<Player>();
		for (Player player : getKnownList().getKnownPlayers().values()) {
			if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 40)) {
				players.add(player);
			}
		}
		Player target = !players.isEmpty() ? players.get(Rnd.get(players.size())) : null;
		AI2Actions.targetCreature(this, target);
		World.getInstance().updatePosition(getOwner(), target.getX(), target.getY(), target.getZ(), (byte) 0);
		PacketSendUtility.broadcastPacketAndReceive(getOwner(), new SM_FORCED_MOVE(getOwner(), getOwner()));
	}

	private void cancelPiercingStrike() {
		if (piercingStrikeTask != null && !piercingStrikeTask.isCancelled()) {
			piercingStrikeTask.cancel(true);
		}
	}

	private void cancelFireStorm() {
		if (fireStormTask != null && !fireStormTask.isCancelled()) {
			fireStormTask.cancel(true);
		}
	}

	private synchronized void checkPercentage(int hpPercentage) {
		for (Integer percent : percents) {
			if (hpPercentage <= percent) {
				switch (percent) {
					case 96:
						lavaEruptionEvent(283116);
						if (isEndPiercingStrike.compareAndSet(true, false))
							startPiercingStrikeTask();
						break;
					case 75:
						AI2Actions.useSkill(this, 20761);
						if (isEndFireStorm.compareAndSet(true, false))
							startFireStormTask();
						break;
					case 60:
						AI2Actions.useSkill(this, 20761);
						break;
					case 55:
						lavaEruptionEvent(283118);
						break;
					case 40:
					case 25:
						AI2Actions.useSkill(this, 20761);
						break;
					case 20:
						cancelFireStorm();
						cancelPiercingStrike();
						lavaEruptionEvent(283120);
						break;
					case 10:
						AI2Actions.useSkill(this, 20942);
						break;
					case 7:
						AI2Actions.useSkill(this, 20883);
						spawn(283102, 679.88f, 1068.88f, 497.88f, (byte) 0);
						break;
				}
				percents.remove(percent);
				break;
			}
		}
	}

	private void lavaEruptionEvent(final int floorId) {
		AI2Actions.targetSelf(BrigadeGeneralTahabataAI2.this);
		AI2Actions.useSkill(BrigadeGeneralTahabataAI2.this, 20756);
		if (getPosition().getWorldMapInstance().getNpc(283051) == null) {
			spawn(283051, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
		}
		spawnfloor(floorId);
	}

	private void spawnfloor(final int floor) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				spawn(floor, 679.88f, 1068.88f, 497.88f, (byte) 0);
				spawn(floor + 1, 679.88f, 1068.88f, 497.88f, (byte) 0);
			}

		}, 10000);
	}

	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			WorldMapInstance instance = getPosition().getWorldMapInstance();
			if (instance != null) {
				SpawnTemplate template = rndSpawnInRange(npcId, 10);
				SpawnEngine.spawnObject(template, getPosition().getInstanceId());
			}
		}
	}

	private SpawnTemplate rndSpawnInRange(int npcId, int dist) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * dist);
		float y1 = (float) (Math.sin(Math.PI * direction) * dist);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
				+ y1, getPosition().getZ(), getPosition().getHeading());
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	private void addPercent() {
		percents.clear();
		Collections.addAll(percents, new Integer[]{96, 75, 60, 55, 40, 25, 20, 10, 7});
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		addPercent();
	}

	private void deleteAdds() {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(283116));
		deleteNpcs(instance.getNpcs(283117));
		deleteNpcs(instance.getNpcs(283118));
		deleteNpcs(instance.getNpcs(283119));
		deleteNpcs(instance.getNpcs(283120));
		deleteNpcs(instance.getNpcs(283121));
		deleteNpcs(instance.getNpcs(283102));
	}

	@Override
	protected void handleBackHome() {
		addPercent();
		isHome.set(true);
		getPosition().getWorldMapInstance().getDoors().get(610).setOpen(true);
		super.handleBackHome();
		getEffectController().removeEffect(20942);
		deleteAdds();
		cancelPiercingStrike();
		cancelFireStorm();
		isEndPiercingStrike.set(true);
		isEndFireStorm.set(true);
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();
		cancelPiercingStrike();
		cancelFireStorm();
	}

	@Override
	protected void handleDied() {
		percents.clear();
		getPosition().getWorldMapInstance().getDoors().get(610).setOpen(true);
		super.handleDied();
		deleteAdds();
		cancelPiercingStrike();
		cancelFireStorm();
	}

}
