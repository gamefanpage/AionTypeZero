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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.CreatureType;
import org.typezero.gameserver.model.flyring.FlyRing;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.flyring.FlyRingTemplate;
import org.typezero.gameserver.model.utils3d.Point3D;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Ritsu
 */
@InstanceID(300470000)
public class SatraTreasureHoardInstance extends GeneralInstanceHandler {

   private Map<Integer, StaticDoor> doors;
   private boolean isStartTimer = false;
   private List<Npc> firstChest = new ArrayList<Npc>();
   private List<Npc> finalChest = new ArrayList<Npc>();

   @Override
   public void onInstanceDestroy() {
	  doors.clear();
   }

   @Override
   public void onInstanceCreate(WorldMapInstance instance) {
	  super.onInstanceCreate(instance);
	  doors = instance.getDoors();
	  doors.get(77).setOpen(true);
	  spawnTimerRing();
   }

   private void spawnTimerRing() {
	  FlyRing f1 = new FlyRing(new FlyRingTemplate("SATRAS_01", mapId,
			  new Point3D(501.13412, 672.4659, 177.10771),
			  new Point3D(492.13412, 672.4659, 177.10771),
			  new Point3D(496.54834, 671.5966, 184.10771), 8), instanceId);
	  f1.spawn();
   }

   @Override
   public boolean onPassFlyingRing(Player player, String flyingRing) {
	  if (flyingRing.equals("SATRAS_01")) {
		 if (!isStartTimer) {
			isStartTimer = true;
			System.currentTimeMillis();
			PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 600));
			doors.get(77).setOpen(false);
			//Spawn Chest When the timer begin
			firstChest.add((Npc) spawn(701461, 466.246f, 716.57f, 176.398f, (byte) 0));
			firstChest.add((Npc) spawn(701461, 528.156f, 715.66f, 176.398f, (byte) 60));
			firstChest.add((Npc) spawn(701461, 469.17f, 701.632f, 176.398f, (byte) 11));
			firstChest.add((Npc) spawn(701461, 524.292f, 701.063f, 176.398f, (byte) 50));
			firstChest.add((Npc) spawn(701461, 515.439f, 691.87f, 176.398f, (byte) 45));
			firstChest.add((Npc) spawn(701461, 478.623f, 692.772f, 176.398f, (byte) 15));
			changeChestType(CreatureType.PEACE.getId());
			switchWay();
			despawnChest();
		 }
	  }
	  return false;
   }

   @Override
   public void onDie(Npc npc) {
	  switch (npc.getNpcId()) {
		 case 219296:
			int door = Rnd.get(1, 2);
			if (door == 1) {
			   doors.get(84).setOpen(true);
			   sendMsg(1401230);
			}
			else {
			   doors.get(88).setOpen(true);
			   sendMsg(1401229);
			}
			break;
		 case 219299: // muzzled punisher
		 case 219300: // punisher unleashed
			spawn(730588, 496.600f, 685.600f, 176.400f, (byte) 30); // Spawn Exit
			instance.doOnAllPlayers(new Visitor<Player>() {
			   @Override
			   public void visit(Player p) {
				  if (p.isOnline())
					 PacketSendUtility.sendPacket(p, new SM_QUEST_ACTION(0,0));
			   }
			});
			changeChestType(CreatureType.FRIEND.getId());
			break;
		 case 701464: // artifact spawn stronger boss
			Npc boss = getNpc(219299);
			if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
			   spawn(219300, boss.getX(), boss.getY(), boss.getZ(), boss.getHeading());
			   boss.getController().onDelete();
			}
			break;
		 case 219298:
			doors.get(62).setOpen(true);
			doors.get(108).setOpen(true);
			doors.get(118).setOpen(true);
			sendMsg(1401231);
			break;
		 case 219297:
			doors.get(82).setOpen(true);
			doors.get(86).setOpen(true);
			doors.get(117).setOpen(true);
			sendMsg(1401231);
			break;
	  }
   }

   private void switchWay() {
	  Npc muzzledPunisher = getNpc(219299);
	  int chest = muzzledPunisher == null ? 701463 : 701462;
	  finalChest.add((Npc) spawn(chest, 446.962f, 744.254f, 178.071f, (byte) 0, 206));
	  finalChest.add((Npc) spawn(chest, 459.856f, 759.960f, 178.071f, (byte) 0, 81));
	  finalChest.add((Npc) spawn(chest, 533.697f, 760.551f, 178.071f, (byte) 0, 80));
	  finalChest.add((Npc) spawn(chest, 477.382f, 770.049f, 178.071f, (byte) 0, 83));
	  finalChest.add((Npc) spawn(chest, 497.030f, 773.931f, 178.071f, (byte) 0, 85));
	  finalChest.add((Npc) spawn(chest, 516.508f, 770.646f, 178.071f, (byte) 0, 122));
   }

   private void despawnChest() {
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			firstChest.get(0).getController().onDelete();
			finalChest.get(0).getController().onDelete();
		 }
	  }, 300000);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			firstChest.get(1).getController().onDelete();
			finalChest.get(1).getController().onDelete();
		 }
	  }, 360000);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			firstChest.get(2).getController().onDelete();
			finalChest.get(2).getController().onDelete();
		 }
	  }, 420000);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			firstChest.get(3).getController().onDelete();
			finalChest.get(3).getController().onDelete();
		 }
	  }, 480000);
	  ThreadPoolManager.getInstance().schedule(new Runnable() {
		 @Override
		 public void run() {
			firstChest.get(4).getController().onDelete();
			finalChest.get(4).getController().onDelete();
		 }
	  }, 540000);
   }

   private void changeChestType(final int newType) {
	  for (Npc c : instance.getNpcs(701461)) {
		 c.setNpcType(newType);
	  }
	  for (final Player player : instance.getPlayersInside()) {
	  	player.getKnownList().doOnAllNpcs(new Visitor<Npc>() {
				@Override
				 public void visit(Npc npc) {
					if(npc.getNpcId() == 701461)
						PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(npc.getObjectId(), 0, newType, 0));
				 }
			 });
	  }
  }
}
