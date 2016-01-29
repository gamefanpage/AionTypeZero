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
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.manager.EmoteManager;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;

/**
 * @author Cheatkiller
 *
 */
@AIName("brigadegeneralterath")
public class BrigadeGeneralTerathAI2 extends AggressiveNpcAI2 {

   private AtomicBoolean isHome = new AtomicBoolean(true);
   private List<Integer> percents = new ArrayList<Integer>();
   private Future<?> skillTask;
   private boolean canThink = true;
   private Npc aethericField;
   private boolean isGravityEvent;
   private boolean isFinalBuff;

   @Override
   protected void handleAttack(Creature creature) {
	  super.handleAttack(creature);
	  if (isHome.compareAndSet(true, false)) {
		 if (aethericField == null) {
			aethericField = (Npc) spawn(730692, 1030.08f, 1030.08f, 1030.08f, (byte) 0);
			getPosition().getWorldMapInstance().getDoors().get(706).setOpen(false);
		 }
		 if (!isGravityEvent) {
			startSkillTask();
		 }
	  }
	  checkPercentage(getLifeStats().getHpPercentage());
	  if (!isFinalBuff && getOwner().getLifeStats().getHpPercentage() <= 25) {
		 isFinalBuff = true;
		 AI2Actions.useSkill(this, 20942);
	  }
   }

   private void startSkillTask() {
	  skillTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
		 @Override
		 public void run() {
			if (isAlreadyDead())
			   cancelskillTask();
			else {
			   gravityDistortionEvent();
			}
		 }
	  }, 5000, 30000);
   }

   private void cancelskillTask() {
	  if (skillTask != null && !skillTask.isCancelled()) {
		 skillTask.cancel(true);
	  }
   }

   private void gravityDistortionEvent() {
	  SkillEngine.getInstance().getSkill(getOwner(), 20739, 55, getOwner()).useNoAnimationSkill();
	  spawn(283096, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
	  spawn(283097, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
	  //spawn(283098, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) 0);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			SkillEngine.getInstance().getSkill(getOwner(), 20741, 55, getOwner()).useNoAnimationSkill();
		 }
	  }, 5000);
   }

   private synchronized void checkPercentage(int hpPercentage) {
	  for (Integer percent : percents) {
		 if (hpPercentage <= percent && !isGravityEvent) {
			percents.remove(percent);
			canThink = false;
			isGravityEvent = true;
			cancelskillTask();
			spawn(283158, 1056.8f, 297.6f, 409.9f, (byte) 0);
			spawn(283158, 1002.07f, 297.4f, 409.85f, (byte) 0);
			SkillEngine.getInstance().getSkill(getOwner(), 20737, 55, getOwner()).useNoAnimationSkill();
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  EmoteManager.emoteStopAttacking(getOwner());
				  setStateIfNot(AIState.WALKING);
				  getOwner().getMoveController().moveToPoint(getOwner().getSpawn().getX(), getOwner().getSpawn().getY(), getOwner().getSpawn().getZ());
				  WalkManager.startWalking(BrigadeGeneralTerathAI2.this);
				  getOwner().setState(1);
				  PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
			   }
			}, 4000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  spawn(283109, 1029.9f, 297.26f, 409.08f, (byte) 0);
				  spawn(283110, 1029.93f, 297.31f, 409.08f, (byte) 0);
			   }
			}, 10000);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  despawn();
				  getEffectController().removeEffect(20737);
				  canThink = true;
				  isGravityEvent = false;
				  startSkillTask();
				  Creature creature = getAggroList().getMostHated();
				  if (creature == null || creature.getLifeStats().isAlreadyDead() || !getOwner().canSee(creature)) {
					 setStateIfNot(AIState.FIGHT);
					 think();
				  }
				  else {
					 getMoveController().abortMove();
					 getOwner().setTarget(creature);
					 getOwner().getGameStats().renewLastAttackTime();
					 getOwner().getGameStats().renewLastAttackedTime();
					 getOwner().getGameStats().renewLastChangeTargetTime();
					 getOwner().getGameStats().renewLastSkillTime();
					 setStateIfNot(AIState.WALKING);
					 getOwner().setState(1);
					 getOwner().getMoveController().moveToTargetObject();
					 PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getOwner().getObjectId()));
				  }
			   }
			}, 30000);
		 }
		 break;
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
	  percents.clear();
	  cancelskillTask();
	  aethericField.getController().onDelete();
	  getPosition().getWorldMapInstance().getDoors().get(706).setOpen(true);
	  despawn();
   }

   private void despawn() {
	  WorldMapInstance instance = getPosition().getWorldMapInstance();
	  deleteNpcs(instance.getNpcs(283158));
	  deleteNpcs(instance.getNpcs(283109));
	  deleteNpcs(instance.getNpcs(283110));
   }

   @Override
   protected void handleBackHome() {
	  super.handleBackHome();
	  addPercent();
	  isFinalBuff = false;
	  cancelskillTask();
	  isGravityEvent = false;
	  canThink = true;
	  isHome.set(true);
	  aethericField.getController().onDelete();
	  despawn();
	  getPosition().getWorldMapInstance().getDoors().get(706).setOpen(true);
   }

   @Override
   protected void handleDespawned() {
	  super.handleDespawned();
	  cancelskillTask();
   }

   @Override
   protected void handleSpawned() {
	  super.handleSpawned();
	  addPercent();
   }

   private void addPercent() {
	  percents.clear();
	  Collections.addAll(percents, new Integer[]{90, 70, 50, 30, 25});
   }

   @Override
   public boolean canThink() {
	  return canThink;
   }
}
