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

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.SkillEngine;

/**
 * @author Cheatkiller
 *
 */
@AIName("invincibleshabokan")
public class InvincibleShabokanAI2 extends AggressiveNpcAI2 {

   private AtomicBoolean isHome = new AtomicBoolean(true);
   private Future<?> skillTask;
   private boolean isFinalBuff;

   @Override
   protected void handleAttack(Creature creature) {
	  super.handleAttack(creature);
	  if (isHome.compareAndSet(true, false))
		 startSkillTask();
	  if(!isFinalBuff && getOwner().getLifeStats().getHpPercentage() <= 25) {
			isFinalBuff = true;
			AI2Actions.useSkill(this, 20941);
		}
	}

   private void startSkillTask() {
	  skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
		 @Override
		 public void run() {
			if (isAlreadyDead())
			   cancelTask();
			else {
			   chooseRandomEvent();
			}
		 }
	  }, 5000, 30000);
   }

   private void cancelTask() {
	  if (skillTask != null && !skillTask.isCancelled()) {
		 skillTask.cancel(true);
	  }
   }

   private void earthQuakeEvent() {
	  Npc invisible = getPosition().getWorldMapInstance().getNpc(283082);
	  SkillEngine.getInstance().getSkill(getOwner(), 20717, 55, getOwner()).useNoAnimationSkill();
	  if (invisible == null) {
		 spawn(283082, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
	  }
   }

   private void sinkEvent() {
	  SkillEngine.getInstance().getSkill(getOwner(), 20720, 55, getOwner()).useNoAnimationSkill();
	  for (Player player : getKnownList().getKnownPlayers().values()) {
		 if (isInRange(player, 30)) {
			spawn(283083, player.getX(), player.getY(), player.getZ(), (byte) 0);
			spawn(283084, player.getX(), player.getY(), player.getZ(), (byte) 0);
		 }
	  }
   }

   private void chooseRandomEvent() {
	  int rand = Rnd.get(0, 1);
	  if (rand == 0)
		 earthQuakeEvent();
	  else
		 sinkEvent();
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
	  getOwner().getEffectController().removeEffect(20941);
	  isHome.set(true);
   }
}
