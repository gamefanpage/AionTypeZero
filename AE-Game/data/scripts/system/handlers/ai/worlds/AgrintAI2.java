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

package ai.worlds;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xTz
 */
@AIName("agrint")
public class AgrintAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isSpawned = new AtomicBoolean(false);

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		int msg = 0;
		switch(getNpcId()) {
			case 218862:
			case 218850:
				msg = 1401246;
				break;
			case 218863:
			case 218851:
				msg = 1401247;
				break;
			case 218864:
			case 218852:
				msg = 1401248;
				break;
			case 218865:
			case 218853:
				msg = 1401249;
				break;
		}
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, 2000);
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private synchronized void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 50) {
			if (isSpawned.compareAndSet(false, true)) {
				int npcId;
				switch (getNpcId()) {
					case 218850:
					case 218851:
					case 218852:
					case 218853:
						npcId = getNpcId() + 320;
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						break;
					case 218862:
					case 218863:
					case 218864:
					case 218865:
						npcId = getNpcId() + 308;
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						rndSpawnInRange(npcId, Rnd.get(1, 2));
						break;
				}
			}
		}
	}

	private Npc rndSpawnInRange(int npcId, float distance) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * distance);
		float y1 = (float) (Math.sin(Math.PI * direction) * distance);
		WorldPosition p = getPosition();
		return (Npc) spawn(npcId,p .getX() + x1, p .getY() + y1, p .getZ(), (byte) 0);
	}

	@Override
	protected void handleBackHome() {
		isSpawned.set(false);
		super.handleBackHome();
	}

	private void spawnChests(int npcId) {
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
		rndSpawnInRange(npcId, Rnd.get(1, 6));
	}

	@Override
	protected void handleDied() {
		switch (getNpcId()) {
			case 218850:
				spawnChests(218874);
				break;
			case 218851:
				spawnChests(218876);
				break;
			case 218852:
				spawnChests(218878);
				break;
			case 218853:
				spawnChests(218880);
				break;
			case 218862:
				spawnChests(218882);
				break;
			case 218863:
				spawnChests(218884);
				break;
			case 218864:
				spawnChests(218886);
				break;
			case 218865:
				spawnChests(218888);
				break;
		}
		super.handleDied();
	}

	@Override
	public int modifyOwnerDamage(int damage) {
		return 1;
	}

	@Override
	public int modifyDamage(int damage) {
		return 1;
	}

}
