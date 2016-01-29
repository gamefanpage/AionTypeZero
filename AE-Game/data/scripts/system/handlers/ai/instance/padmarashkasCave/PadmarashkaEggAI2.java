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

package ai.instance.padmarashkasCave;


import ai.NoActionAI2;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Ritsu
 */
@AIName("padmarashkaegg")
public class PadmarashkaEggAI2 extends NoActionAI2 {

	boolean isSmallEggProtectorSpawned = false;
	boolean isHugeEggProtectorSpawned = false;
	private Npc protector = null;

	@Override
	protected void handleDied() {
		if (protector != null && !NpcActions.isAlreadyDead(protector)) {
			SkillEngine.getInstance().getSkill(protector, 20176, 55, protector).useNoAnimationSkill(); //apply wrath buff
		}
		super.handleDied();
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (!isSmallEggProtectorSpawned && this.getNpcId() == 282613) {
			switch (Rnd.get(1, 6)) {
				case 1:
					protector = (Npc) spawn(282715, 579.415f, 168.109f, 66.000f, (byte) 0);
					break;
				case 2:
					protector = (Npc) spawn(282715, 581.316f, 157.520f, 66.000f, (byte) 0);
					break;
				case 3:
					protector = (Npc) spawn(282715, 575.073f, 147.338f, 66.000f, (byte) 0);
					break;
				case 4:
					protector = (Npc) spawn(282715, 585.119f, 150.989f, 66.000f, (byte) 0);
					break;
				case 5:
					protector = (Npc) spawn(282716, 581.141f, 148.342f, 66.000f, (byte) 0);
					break;
				case 6:
					protector = (Npc) spawn(282716, 584.240f, 142.233f, 66.000f, (byte) 0);
					break;
			}
			isSmallEggProtectorSpawned = true;
		}
		else if (!isHugeEggProtectorSpawned && this.getNpcId() == 282614) {
			SpawnEliteCommander(); // Random spawn SpawnEliteCommander to protect Egg
			isHugeEggProtectorSpawned = true;
		}
	}

	private void SpawnEliteCommander() {
		SpawnTemplate template = rndSpawnInRange(282712);
		protector = (Npc) SpawnEngine.spawnObject(template, getPosition().getInstanceId());
	}

	private SpawnTemplate rndSpawnInRange(int npcId) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * 5);
		float y1 = (float) (Math.sin(Math.PI * direction) * 5);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
						+ y1, getPosition().getZ(), getPosition().getHeading());
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		switch (this.getNpcId()) {
			case 282613:
				smallEggSpawn();
				break;
			case 282614:
				hugeEggSpawn();
				break;
		}
	}

	private void smallEggSpawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (getOwner() != null && !isAlreadyDead()) {
					AI2Actions.deleteOwner(PadmarashkaEggAI2.this);
					spawn(282616, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				}

			}
		}, 60000); //TODO: Need right value
	}

	private void hugeEggSpawn() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (getOwner() != null && !isAlreadyDead()) {
					AI2Actions.deleteOwner(PadmarashkaEggAI2.this);
					spawn(282620, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
				}
			}
		}, 120000); //TODO: Need right value
	}
}
