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

package ai;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.ai.Percentage;
import org.typezero.gameserver.model.ai.SummonGroup;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.world.World;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author xTz
 */
@AIName("summoner")
public class SummonerAI2 extends AggressiveNpcAI2 {

	private final List<Integer> spawnedNpc = new ArrayList<Integer>();

	private List<Percentage> percentage = Collections.emptyList();

	private int spawnedPercent = 0;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	@Override
	protected void handleDespawned() {
		super.handleDespawned();

		synchronized(spawnedNpc) {
			removeHelpersSpawn();
			spawnedNpc.clear();
		}

		percentage.clear();
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();

		synchronized(spawnedNpc) {
			removeHelpersSpawn();
			spawnedNpc.clear();
		}

		spawnedPercent = 0;
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		percentage = DataManager.AI_DATA.getAiTemplate().get(getNpcId()).getSummons().getPercentage();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		removeHelpersSpawn();
		spawnedNpc.clear();
		percentage.clear();
	}

	private void removeHelpersSpawn() {
		for (Integer object : spawnedNpc) {
			VisibleObject npc = World.getInstance().findVisibleObject(object);
			if (npc != null && npc.isSpawned()) {
				npc.getController().onDelete();
			}
		}
	}

	protected void addHelpersSpawn(int objId) {
		synchronized(spawnedNpc) {
			spawnedNpc.add(objId);
		}
	}

	private void checkPercentage(int hpPercentage) {
		for (Percentage percent : percentage) {
			if (spawnedPercent != 0 && spawnedPercent <= percent.getPercent()) {
				continue;
			}

			if (hpPercentage <= percent.getPercent()) {
				int skill = percent.getSkillId();
				if (skill != 0) {
					AI2Actions.useSkill(this, skill);
				}

				if (percent.isIndividual()) {
					handleIndividualSpawnedSummons(percent);
				}
				else if (percent.getSummons() != null) {
					handleBeforeSpawn(percent);
					for (SummonGroup summonGroup : percent.getSummons()) {
						final SummonGroup sg = summonGroup;
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								spawnHelpers(sg);
							}
						}, summonGroup.getSchedule());

					}
				}
				spawnedPercent = percent.getPercent();
			}
		}
	}

	protected void spawnHelpers(SummonGroup summonGroup) {
		if (!isAlreadyDead() && checkBeforeSpawn()) {
			int count = 0;
			if (summonGroup.getCount() != 0) {
				count = summonGroup.getCount();
			}
			else {
				count = Rnd.get(summonGroup.getMinCount(), summonGroup.getMaxCount());
			}
			for (int i = 0; i < count; i++) {
						SpawnTemplate summon = null;
					if (summonGroup.getDistance() != 0) {
						summon = rndSpawnInRange(summonGroup.getNpcId(), summonGroup.getDistance());
					}
					else {
						summon = SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), summonGroup.getNpcId(),
							summonGroup.getX(), summonGroup.getY(), summonGroup.getZ(), summonGroup.getH());
					}
					VisibleObject npc = SpawnEngine.spawnObject(summon, getPosition().getInstanceId());
					addHelpersSpawn(npc.getObjectId());
			}
			handleSpawnFinished(summonGroup);
		}
	}

	protected SpawnTemplate rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x = (float) (Math.cos(Math.PI * direction) * distance);
		float y = (float) (Math.sin(Math.PI * direction) * distance);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x, getPosition().getY()
			+ y, getPosition().getZ(), getPosition().getHeading());
	}

	protected boolean checkBeforeSpawn() {
		return true;
	}

	protected void handleBeforeSpawn(Percentage percent) {
	}

	protected void handleSpawnFinished(SummonGroup summonGroup) {
	}

	protected void handleIndividualSpawnedSummons(Percentage percent) {
	}

}
