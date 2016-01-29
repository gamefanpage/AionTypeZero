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

import java.util.List;

import ai.AggressiveNpcAI2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.world.WorldMapInstance;


/**
 * @author Cheatkiller
 *
 */
@AIName("traitorkumbanda")
public class TraitorKumbandaAI2 extends AggressiveNpcAI2 {

	private boolean isFinalBuff;

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (Rnd.get(1, 100) < 5) {
			spawnTimeAccelerator();
			spawnKumbandaGhost();
		}
		if(!isFinalBuff && getOwner().getLifeStats().getHpPercentage() <= 5) {
			isFinalBuff = true;
			AI2Actions.useSkill(this, 20942);
		}
	}

	private void spawnTimeAccelerator() {
		if (getPosition().getWorldMapInstance().getNpc(283086) == null) {
			SkillEngine.getInstance().getSkill(getOwner(), 20726, 55, getOwner()).useNoAnimationSkill();
			spawn(283086, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
			rndSpawn(283086, 6);
		}
	}

	private void spawnKumbandaGhost() {
		if (getPosition().getWorldMapInstance().getNpc(283085) == null && getOwner().getLifeStats().getHpPercentage() <= 50) {
			spawn(283085, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
		}
	}

	private void deleteNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			if (npc != null) {
				npc.getController().onDelete();
			}
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		deleteNpcs(instance.getNpcs(283086));
		deleteNpcs(instance.getNpcs(283088));
	}

	@Override
	protected void handleBackHome() {
		super.handleBackHome();
		isFinalBuff = false;
	}

	private void rndSpawn(int npcId, int count) {
		for (int i = 0; i < count; i++) {
			SpawnTemplate template = rndSpawnInRange(npcId, Rnd.get(10, 20));
			SpawnEngine.spawnObject(template, getPosition().getInstanceId());
		}
	}

	private SpawnTemplate rndSpawnInRange(int npcId, int dist) {
		float direction = Rnd.get(0, 199) / 100f;
		float x1 = (float) (Math.cos(Math.PI * direction) * dist);
		float y1 = (float) (Math.sin(Math.PI * direction) * dist);
		return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
				+ y1, getPosition().getZ(), getPosition().getHeading());
	}
}
