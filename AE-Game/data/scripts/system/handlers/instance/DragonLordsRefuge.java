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

package instance;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Bobobear
 * @modified Luzien
 */
@InstanceID(300520000)
public class DragonLordsRefuge extends GeneralInstanceHandler {

   private final AtomicInteger specNpcKilled = new AtomicInteger();
   private boolean isInstanceDestroyed;
   private Race instanceRace;
   private int killedCount;
   private Future<?> failTask;

   @Override
   public void onDie(Npc npc) {
	  if (isInstanceDestroyed) {
		 return;
	  }

	  int npcId = npc.getNpcId();

	  switch (npcId) {
		 case 219365: //fissurefang
			despawnNpc(219365); //despawn fissurefang corpse
			performSkillToTarget(219361, 219361, 20979); //remove Fissure Buff
			sendMsg(1401533);
			checkIncarnationKills();
			break;
		 case 219366: //graviwing
			despawnNpc(219366); //despawn graviwing corpse
			performSkillToTarget(219361, 219361, 20981); //remove Gravity Buff
			sendMsg(1401535);
			checkIncarnationKills();
			break;
		 case 219367: //wrathclaw
			despawnNpc(219367); //despawn wrathclaw corpse
			performSkillToTarget(219361, 219361, 20980); //remove Wrath Buff
			sendMsg(1401534);
			checkIncarnationKills();
			break;
		 case 219368: //petriscale
			despawnNpc(219368); //despawn petriscale corpse
			performSkillToTarget(219361, 219361, 20982); //remove Petrification Buff
			sendMsg(1401536);
			checkIncarnationKills();
			break;
		 case 730695:
			instance.getNpc(219359).getEffectController().removeEffect(20590);
			break;
		 case 730696:
			instance.getNpc(219359).getEffectController().removeEffect(20591);
			break;
		 case 219359: //Calindi Flamelord
				if (Rnd.get(1, 100) < 25) {
					spawn(802182, 487.16f, 528.44f, 417.40f, (byte) 0);
				}
			despawnNpc(730694); //despawn tiamat aetheric field
			despawnNpc(730695); //despawn Surkanas if spawned
			despawnNpc(730696); //despawn Surkanas if spawned
			performSkillToTarget(219360, 219360, 20919); //Transformation
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  despawnNpc(219360); //despawn tiamat woman (1st spawn)
				  spawn(219361, 466.7468f, 514.5500f, 417.4044f, (byte) 0);//tiamat dragon 2nd Spawn
				  performSkillToTarget(219361, 219361, 20975); //Fissure Buff
				  performSkillToTarget(219361, 219361, 20976); //Wrath Buff
				  performSkillToTarget(219361, 219361, 20977); //Gravity Buff
				  performSkillToTarget(219361, 219361, 20978); //Petrification Buff
				  performSkillToTarget(219361, 219361, 20984); //Unbreakable Wing (reflect)
			   }
			}, 5000);

			//schedule dragon lords roar skill to block all players before spawn empyrean lords
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  performSkillToTarget(219361, 219361, 20920);
			   }
			}, 8000);

			//spawn Kaisinel or Marchutan Gods (depends of group race)
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  spawn((instanceRace == Race.ELYOS ? 219488 : 219491), 504f, 515f, 417.405f, (byte) 60);
			   }
			}, 15000);

			//schedule spawn of balaur spiritualists and broadcast messages
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  sendMsg(instanceRace == Race.ELYOS ? 1401531 : 1401532);
				  //spawn balaur spritualists (will defend Internal Passages)
				  spawn(283163, 463f, 568f, 417.405f, (byte) 105);
				  spawn(283164, 545f, 568f, 417.405f, (byte) 78);
				  spawn(283165, 545f, 461f, 417.405f, (byte) 46);
				  spawn(283166, 463f, 461f, 417.405f, (byte) 17);
			   }
			}, 40000);
			break;
		 case 219362: //Tiamat Dragon (3rd spawn)
				if (Rnd.get(1, 100) < 25) {
					spawn(802182, 489.81f, 492.72f, 417.40f, (byte) 0);
				}
			if (failTask != null && !failTask.isDone())
			   failTask.cancel(true);
			spawn(701542, 480f, 514f, 417.405f, (byte) 0);//tiamat treasure chest reward
			spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23);
			spawn(800464, 544.964f, 517.898f, 417.405f, (byte) 113);
			spawn(800465, 545.605f, 510.325f, 417.405f, (byte) 17);
                        despawnBoss();
                        despawnBossL();
                        despawnNpc(219362);
			break;
		 case 283163: //balaur spiritualist (spawn Portal after die)
			healEmpyreanLord(0); //heal Empyrean Lord
			spawn(730675, 460.082f, 571.978f, 417.405f, (byte) 43); //spawn portal to tiamat incarnation
                        despawnNpc(283163);
			break;
		 case 283164:
			healEmpyreanLord(1);
			spawn(730676, 547.822f, 571.876f, 417.405f, (byte) 18);
                        despawnNpc(283164);
			break;
		 case 283165:
			healEmpyreanLord(2);
			spawn(730674, 547.909f, 456.568f, 417.405f, (byte) 103);
                        despawnNpc(283165);
			break;
		 case 283166:
			healEmpyreanLord(3);
			spawn(730673, 459.548f, 456.849f, 417.405f, (byte) 78);
                        despawnNpc(283166);
			break;
		 case 219361: // Tiamat Dragon (1st spawn) - Players cannot kill tiamat, they must kill 4 incanation before
			//TODO: what to do?
			break;
		 case 219488: // Kaisinel Gods (1st Spawn)
		 case 219491: // Marchutan Gods (1st Spawn)
			sendMsg(1401542);
			Npc tiamat = getNpc(219361);
			tiamat.getController().useSkill(20983);
			ThreadPoolManager.getInstance().schedule(new Runnable() {
			   @Override
			   public void run() {
				  despawnNpc(219361);//despawn tiamat dragon
				  spawn(730694, 436.7526f, 513.8103f, 420.6662f, (byte) 0, 14); //re-spawn tiamat aetheric field
				  spawn(219360, 451.9700f, 514.5500f, 417.4044f, (byte) 0); //re-spawn tiamat woman to initial position
				  sendMsg(1401563);//broadcast message of instance failed
				  spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23); //spawn exit
			   }
			}, 5000);
			//TODO: check on retail
			break;
	  }
   }

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 219359:
			case 219362:
				if (Rnd.get(1, 100) < 5) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000194, 1));
				}
				break;
			}
		}

   private void performSkillToTarget(int npcId, int targetId, int skillId) {
	  if (isSpawned(npcId) && isSpawned(targetId)) {
		 final Npc npc = getNpc(npcId);
		 final Npc target = getNpc(targetId);
		 SkillEngine.getInstance().getSkill(npc, skillId, 100, target).useSkill();
	  }
   }

   private void despawnNpc(int npcId) {
	  Npc npc = getNpc(npcId);
	  if (npc != null) {
		 npc.getController().onDelete();
	  }
   }

   private boolean isSpawned(int npcId) {
	  Npc npc = getNpc(npcId);
	  if (!isInstanceDestroyed && npc != null && !NpcActions.isAlreadyDead(npc))
		 return true;
	  return false;
   }

   private void startFinalTimer() {
	  sendMsg(1401547);//broadcast message for start time

	  failTask = ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			if (isSpawned(219362)) {
			   despawnNpc(219362); //despawn tiamat dragon
			   spawn(730694, 436.7526f, 513.8103f, 420.6662f, (byte) 0, 14); //re-spawn tiamat aetheric field
			   spawn(219360, 451.9700f, 514.5500f, 417.4044f, (byte) 0); //re-spawn tiamat woman to initial position
			   sendMsg(1401563); //broadcast message of instance failed
			   spawn(730630, 548.18683f, 514.54523f, 420f, (byte) 0, 23); //spawn exit
			}
		 }
	  }, 1800000);
   }

   private void healEmpyreanLord(int id) {
	  int npcId = instanceRace == Race.ELYOS ? 219488 : 219491;
	  int skill = 20993 + id;
	  Npc npc = instance.getNpc(npcId);
	  if (npc != null && !NpcActions.isAlreadyDead(npc)) {
		 SkillEngine.getInstance().getSkill(npc, skill, 60, npc).useNoAnimationSkill(); //heal 7% + def buff
		 sendMsg(1401551);
	  }
   }

   private void despawnBoss() {
	  int npcId = 219492;
	  Npc npc = instance.getNpc(npcId);
	  if (npc != null) {
		spawn(800356, 513.6345f, 513.96356f, 417.405f, (byte) 60);
                spawn(800430, 502.426f, 510.462f, 417.405f, (byte) 0);
                despawnNpc(219492);
	  }
   }

   private void despawnBossL() {
	  int npcId = 219489;
	  Npc npc = instance.getNpc(npcId);
	  if (npc != null) {
		spawn(800350, 513.6345f, 513.96356f, 417.405f, (byte) 60);
                spawn(800430, 502.426f, 510.462f, 417.405f, (byte) 0);
                despawnNpc(219489);
	  }
   }

   private void checkIncarnationKills() {
	  killedCount = specNpcKilled.incrementAndGet();
	  if (killedCount == 4) {
		 if (!isSpawned(219361)) {
			return;
		 }
		 Npc npc = getNpc(219361);
		 final int npcId = instanceRace == Race.ELYOS ? 219488 : 219491;
		 final int msg = instanceRace == Race.ELYOS ? 1401540 : 1401541;
		 npc.getEffectController().removeEffect(20984);// dispel Unbreakable Wing (reflect)
		 sendMsg(1401537);
		 //schedule spawn of empyrean lords for final attack to tiamat before became exausted
		 ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
			   if (isSpawned(npcId)) {
				  despawnNpc(npcId);
				  spawn(npcId + 1, 528f, 514f, 417.405f, (byte) 60);
			   }
			}
		 }, 30000);
		 //schedule spawn of Tiamat 3rd Spawn
		 ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				  if (isSpawned(npcId + 1)) {
					 spawn(283137, 461f, 514f, 417.405f, (byte) 0);
					 spawn(283134, 461f, 514f, 417.405f, (byte) 0);
					 spawn(219362, 461f, 514f, 417.405f, (byte) 0);
					 ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
						   despawnNpc(283134);
						   despawnNpc(283137);
						   despawnNpc(219361);
						   sendMsg(msg);
						   ThreadPoolManager.getInstance().schedule(new Runnable() {
							  @Override
							  public void run() {
								 startFinalTimer();
							  }
						   }, 10000);
						}
					 }, 2000);
				  }
			   }
		 }, 40000);
	  }
   }

   @Override
   public void onInstanceDestroy() {
	  isInstanceDestroyed = true;
   }

   @Override
   public void onInstanceCreate(WorldMapInstance instance) {
	  super.onInstanceCreate(instance);
	  killedCount = 0;
   }

   @Override
   public boolean onDie(final Player player, Creature lastAttacker) {
	  PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
			  : lastAttacker.getObjectId()), true);

	  PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
	  return true;
   }

   @Override
   public void onEnterInstance(Player player) {
	  if (instanceRace == null) {
		 instanceRace = player.getRace();
	  }
   }

   @Override
   public void onExitInstance(Player player) {
	  TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
   }
}
