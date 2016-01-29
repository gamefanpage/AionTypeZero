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

package ai.instance.dragonLordsRefuge;

import java.util.ArrayList;
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
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.MathUtil;

/**
 * @author Cheatkiller
 *
 */
@AIName("calindiflamelord60")
public class CalindiFlamelordAI2 extends AggressiveNpcAI2 {

   private AtomicBoolean isHome = new AtomicBoolean(true);
   private Future<?> trapTask;
   private boolean isFinalBuff;

   @Override
   protected void handleAttack(Creature creature) {
	  super.handleAttack(creature);
	  if (isHome.compareAndSet(true, false))
		 startSkillTask();
	  if (!isFinalBuff) {
		 blazeEngraving();
		 if (getOwner().getLifeStats().getHpPercentage() <= 12) {
			isFinalBuff = true;
			cancelTask();
			AI2Actions.useSkill(this, 20915);
		 }
	  }
   }

   private void startSkillTask() {
	  trapTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
		 @Override
		 public void run() {
			if (isAlreadyDead())
			   cancelTask();
			else {
			   startHallucinatoryVictoryEvent();
			}
		 }
	  }, 5000, 80000);
   }

   private void cancelTask() {
	  if (trapTask != null && !trapTask.isCancelled()) {
		 trapTask.cancel(true);
	  }
   }

   private void startHallucinatoryVictoryEvent() {
	  if (getPosition().getWorldMapInstance().getNpc(730695) == null
			  && getPosition().getWorldMapInstance().getNpc(730696) == null) {
		 AI2Actions.useSkill(this, 20911);
		 SkillEngine.getInstance().applyEffectDirectly(20590, getOwner(), getOwner(), 0);
		 SkillEngine.getInstance().applyEffectDirectly(20591, getOwner(), getOwner(), 0);
		 spawn(730695, 482.21f, 458.06f, 427.42f, (byte) 98);
		 spawn(730696, 482.21f, 571.16f, 427.42f, (byte) 22);
		 rndSpawn(283132, 10);
	  }
   }

   private void blazeEngraving() {
	  if (Rnd.get(0, 100) < 2 && getPosition().getWorldMapInstance().getNpc(283130) == null) {
		 SkillEngine.getInstance().getSkill(getOwner(), 20913, 60, getOwner().getTarget()).useNoAnimationSkill();
		 Player target = getRandomTarget();
		 if (target == null)
			return;
		 spawn(283130, target.getX(), target.getY(), target.getZ(), (byte) 0);
	  }
   }

   private void rndSpawn(int npcId, int count) {
	  for (int i = 0; i < count; i++) {
		 SpawnTemplate template = rndSpawnInRange(npcId);
		 SpawnEngine.spawnObject(template, getPosition().getInstanceId());
	  }
   }

   private SpawnTemplate rndSpawnInRange(int npcId) {
	  float direction = Rnd.get(0, 199) / 100f;
	  int range = Rnd.get(5, 20);
	  float x1 = (float) (Math.cos(Math.PI * direction) * range);
	  float y1 = (float) (Math.sin(Math.PI * direction) * range);
	  return SpawnEngine.addNewSingleTimeSpawn(getPosition().getMapId(), npcId, getPosition().getX() + x1, getPosition().getY()
			  + y1, getPosition().getZ(), getPosition().getHeading());
   }

   protected Player getRandomTarget() {
	  List<Player> players = new ArrayList<Player>();
	  for (Player player : getKnownList().getKnownPlayers().values()) {
		 if (!PlayerActions.isAlreadyDead(player) && MathUtil.isIn3dRange(player, getOwner(), 50)) {
			players.add(player);
		 }
	  }

	  if (players.isEmpty())
		 return null;
	  return players.get(Rnd.get(players.size()));
   }

   @Override
   protected void handleDied() {
	  super.handleDied();
	  cancelTask();
   }

   @Override
   protected void handleDespawned() {
	  super.handleDespawned();
	  cancelTask();
   }

   @Override
   protected void handleBackHome() {
	  super.handleBackHome();
	  cancelTask();
	  isFinalBuff = false;
	  isHome.set(true);
   }
}
