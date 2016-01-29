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

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AbstractAI;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.services.drop.DropRegistrationService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;
import java.util.Map;
import java.util.Set;


/**
 * @author Cheatkiller
 *
 */
@InstanceID(300510000)
public class TiamatStrongHoldInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;
	private boolean isInstanceDestroyed;
	private int drakans;
	private boolean startSuramaEvent;

	@Override
	public void onDie(Npc npc) {
		if (isInstanceDestroyed) {
			return;
		}
		switch (npc.getNpcId()) {
			case 730612:
				firstWave();
				break;
			case 219373:
			case 219369:
			case 219411:
			case 219370:
				drakans++;
				if (drakans == 5)
				  secondWave();
				else if (drakans == 12)
				   thirdWave();
				break;
			case 219352:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 1213.92f, 1076.19f, 491.329f, (byte) 0);
				}
				spawn(283177, 1175.65f, 1069.08f, 498.52f, (byte) 0);
				spawn(701501, 1075.4409f, 1078.5071f, 787.685f, (byte) 16);
				doors.get(48).setOpen(true);
				spawnKahrun();
				break;
			case 219357:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 767.12f, 1075.97f, 500.01f, (byte) 0);
				}
				spawn(701501, 1077.1716f, 1058.1995f, 787.685f, (byte) 61);
				doors.get(37).setOpen(true);
				isDeadBosses();
				break;
			case 219358:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 686.08f, 1079.55f, 497.75f, (byte) 0);
				}
				spawn(701541, 677.35785f, 1069.5361f, 497.75186f, (byte) 0);
				spawn(701527, 1073.948f, 1068.8732f, 787.685f, (byte) 61);
				spawn(730622, 652.4821f, 1069.0302f, 498.7787f, (byte) 0, 82);
				spawn(283180, 679.88f, 1068.88f, 504.2f, (byte) 119);
				isDeadBosses();
				break;
			case 219353:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 1038.20f, 467.007f, 442.23f, (byte) 0);
				}
				spawn(701501, 1071.5909f, 1040.6797f, 787.685f, (byte) 23);
				doors.get(711).setOpen(true);
				isDeadBosses();
				break;
			case 219354:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 1035.87f, 298.41f, 409.08f, (byte) 0);
				}
				spawn(283178, 1030.03f, 301.83f, 411f, (byte) 26);
				spawn(701501, 1086.274f, 1098.3997f, 787.685f, (byte) 90);
				spawn(730622, 1029.792f, 267.0502f, 409.7982f, (byte) 0, 83);
				isDeadBosses();
				break;
			case 219355:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 863.69f, 1329.34f, 394.39f, (byte) 0);
				}
				spawn(701501, 1063.5973f, 1092.7402f, 787.685f, (byte) 107);
				doors.get(51).setOpen(true);
				doors.get(54).setOpen(true);
				doors.get(78).setOpen(true);
				doors.get(11).setOpen(true);
				doors.get(79).setOpen(true);
				isDeadBosses();
				break;
			case 219356:
				sendMsg(1401614);
				if (Rnd.get(1, 100) < 20) {
					spawn(802179, 645.04f, 1314.27f, 487.709f, (byte) 0);
				}
				spawn(701501, 1099.8691f, 1047.1895f, 787.685f, (byte) 64);
				spawn(730622, 644.4221f, 1319.6221f, 488.7422f, (byte) 0, 15);
				spawn(800438, 665.63409f, 1319.7051f, 487.9f, (byte) 61);
				spawn(283179, 629.1f, 1319.5f, 501.2f, (byte) 0);
				isDeadBosses();
				break;
		}
	}

	@Override
	public void onDropRegistered(Npc npc) {
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npc.getObjectId());
		int npcId = npc.getNpcId();
                Integer object = instance.getSoloPlayerObj();
		switch (npcId) {
			case 219352:
			case 219353:
			case 219354:
			case 219355:
			case 219356:
			case 219357:
			case 219358:
				if (Rnd.get(1, 100) < 3) {
					dropItems.add(DropRegistrationService.getInstance().regDropItem(1, object, npcId, 185000189, 1));
				}
				break;
			}
		}



	private void firstWave() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
		    attackPlayer((Npc)spawn(219373, 1505.09f, 1068.54f, 491.38f, (byte) 0));
		    attackPlayer((Npc)spawn(219369, 1510.54f, 1058.04f, 491.5f, (byte) 0));
		    attackPlayer((Npc)spawn(219411, 1517.38f, 1063.5f, 491.52f, (byte) 0));
		    attackPlayer((Npc)spawn(219411, 1516.81f, 1073.6f, 491.52f, (byte) 0));
		    attackPlayer((Npc)spawn(219369, 1510.41f, 1078.8f, 491.52f, (byte) 0));
			}
		}, 5000);
	}

	private void secondWave() {
		attackPlayer((Npc)spawn(219370, 1426.08f, 1068.41f, 491.38f, (byte) 0));
		attackPlayer((Npc)spawn(219369, 1430.3f, 1061.13f, 491.5f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1428.5f, 1056.6f, 491.52f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1439.49f, 1058.5f, 491.4f, (byte) 0));
		attackPlayer((Npc)spawn(219369, 1430.3f, 1075.49f, 491.52f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1439.4f, 1078.6f, 491.4f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1428.5f, 1080.9f, 491.46f, (byte) 0));
	}

	private void thirdWave() {
		attackPlayer((Npc)spawn(219370, 1296.1f, 1068.3f, 491.38f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1290.9f, 1059.13f, 491.5f, (byte) 0));
		attackPlayer((Npc)spawn(219369, 1300.6f, 1056.4f, 491.52f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1302.78f, 1053.55f, 491.4f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1290.94f, 1077.8f, 491.52f, (byte) 0));
		attackPlayer((Npc)spawn(219369, 1300.6f, 1080.3f, 491.4f, (byte) 0));
		attackPlayer((Npc)spawn(219411, 1302.78f, 1082.8f, 491.5f, (byte) 0));
	}

	private void attackPlayer(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isInstanceDestroyed) {
					for (Player player : instance.getPlayersInside()) {
						npc.setTarget(player);
						((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
						npc.setState(1);
						npc.getMoveController().moveToTargetObject();
						PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
					}
				}
			}

		}, 2000);
	}

	private void spawnKahrun() {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				moveToForward((Npc)spawn(800463, 1201.272f, 1074.5463f, 491f, (byte) 61), 1039.5f, 1075.9f, 497.3f, false);
				moveToForward((Npc)spawn(800463, 1201.272f, 1072.5137f, 491f, (byte) 61), 1130, 1072, 497.3f, false);
				moveToForward((Npc)spawn(800463, 1192.8656f, 1071.1085f, 491f, (byte) 61), 1112, 1070, 497, false);
				moveToForward((Npc)spawn(800463, 1201.272f, 1064.1759f, 491f, (byte) 61), 1039, 1061, 497.3f, false);
				moveToForward((Npc)spawn(800463, 1208.4175f, 1071.1797f, 491f, (byte) 61), 1133, 1072.5f, 497.3f, false);
				moveToForward((Npc)spawn(800463, 1192.8656f, 1068.3411f, 491f, (byte) 61), 1114, 1067, 496.7f, false);
				moveToForward((Npc)spawn(800463, 1208.4175f, 1068.3979f, 491f, (byte) 61), 1133.32f, 1066.47f, 497.3f, false);
				moveToForward((Npc)spawn(800463, 1201.272f, 1066.2085f, 491f, (byte) 61), 1128.8f, 1067, 497.3f, false);
				moveToForward((Npc)spawn(800380, 1190.323f, 1068.1558f, 491.03488f, (byte) 61), 1108, 1066, 497.3f, false);
				moveToForward((Npc)spawn(800374, 1188.4259f, 1066.4757f, 491.55029f, (byte) 61), 1094, 1064, 497.4f, true);
				moveToForward((Npc)spawn(800374, 1188.2158f, 1074.2047f, 491.55029f, (byte) 61), 1092.5f, 1074.6f, 497.4f, true);
				moveToForward((Npc)spawn(800376, 1190.3859f, 1071.6548f, 491.03488f, (byte) 61), 1109, 1073, 497.2f, false);
				moveToForward((Npc)spawn(800461, 1184.7582f, 1068.6f, 491.03488f, (byte) 61), 1111, 1068.6f, 497.33f, false);
				moveToForward((Npc)spawn(800460, 1184.7358f, 1070.77f, 491.03488f, (byte) 61), 1111, 1071, 497, false);
				moveToForward((Npc)spawn(800347, 1178.0425f, 1072.28f, 491.02545f, (byte) 61), 1106, 1072, 497.2f, false);
				moveToForward((Npc)spawn(800336, 1178.0559f, 1069.6f, 491.02545f, (byte) 61), 1104, 1069, 497, true);
			}
		}, 7000);
	}

	private void moveToForward(final Npc npc, float x, float y, float z, boolean despawn) {
		((AbstractAI) npc.getAi2()).setStateIfNot(AIState.WALKING);
		npc.setState(1);
		npc.getMoveController().moveToPoint(x, y, z);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
		if (despawn) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			  @Override
			  public void run() {
			  	if (npc.getNpcId() == 800336) {
			  		spawn(800338, 1104, 1069f, 497, (byte) 61);
			  		Npc kahrun = getNpc(800338);
			  		NpcShoutsService.getInstance().sendMsg(kahrun, 1500599, kahrun.getObjectId(), 0, 1000);
					  NpcShoutsService.getInstance().sendMsg(kahrun, 1500600, kahrun.getObjectId(), 0, 5000);
			  	}
				  npc.getController().onDelete();
			  }
		  }, 13000);
		}
	}

	private void spawnColonels() {
		int rand = Rnd.get(0,3);
		switch (rand) {
			case 0:
				spawn(219364, 763.4179f, 1445.6504f, 495.6519f, (byte) 90);
				spawn(219364, 893.7009f, 1445.4846f, 495.6421f, (byte) 90);
				spawn(219364, 893.3f, 1190.71f, 495.6f, (byte) 30);
				spawn(219364, 762.6f, 1192.1f, 495.6f, (byte) 30);
				break;
			case 1:
				spawn(219364, 763.4179f, 1445.6504f, 495.6519f, (byte) 90);
				spawn(219364, 893.7009f, 1445.4846f, 495.6421f, (byte) 90);
				spawn(219364, 893.3f, 1190.71f, 495.6f, (byte) 30);
				spawn(219364, 762.6f, 1192.1f, 495.6f, (byte) 30);
				break;
			case 2:
				spawn(219364, 763.4179f, 1445.6504f, 495.6519f, (byte) 90);
				spawn(219364, 893.7009f, 1445.4846f, 495.6421f, (byte) 90);
				spawn(219364, 893.3f, 1190.71f, 495.6f, (byte) 30);
				spawn(219364, 762.6f, 1192.1f, 495.6f, (byte) 30);
				break;
			case 3:
				spawn(219364, 763.4179f, 1445.6504f, 495.6519f, (byte) 90);
				spawn(219364, 893.7009f, 1445.4846f, 495.6421f, (byte) 90);
				spawn(219364, 893.3f, 1190.71f, 495.6f, (byte) 30);
				spawn(219364, 762.6f, 1192.1f, 495.6f, (byte) 30);
				break;
		}
	}

	private boolean isDeadBosses() {
		Npc boss = getNpc(219352);
		Npc boss1 = getNpc(219353);
		Npc boss2 = getNpc(219354);
		Npc boss3 = getNpc(219355);
		Npc boss4 = getNpc(219356);
		Npc boss5 = getNpc(219357);
		Npc boss6 = getNpc(219358);
		if (isDead(boss) && isDead(boss1) && isDead(boss2) && isDead(boss3)
			&& isDead(boss4) && isDead(boss5) && isDead(boss6)) {
			spawn(800464, 1119.7076f, 1071.1401f, 496.8615f, (byte) 119);
			spawn(800465, 1119.7421f, 1068.4998f, 496.8616f, (byte) 3);
			spawn(730629, 1121.3807f, 1069.8124f, 500.3319f, (byte) 0, 555);
			return true;
		}
		return false;
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("LAKSYAKA_LEGION_HQ_300510000")) {
			if (!startSuramaEvent) {
				startSuramaEvent = true;
				spawn(800433, 725.93f, 1319.9f, 490.7f, (byte) 61);
			}
		}
		else if (zone.getAreaTemplate().getZoneName() == ZoneName.get("GLORIOUS_NEXUS_300510000")) {
			player.getEffectController().removeEffect(2784);
		}
	}

	@Override
	public void onLeaveInstance(Player player) {
		player.getEffectController().removeEffect(2784);
	}

	@Override
	public void handleUseItemFinish(Player player, Npc npc) {
		switch (npc.getNpcId()) {
			case 701494:
				doors.get(22).setOpen(true);
				break;
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(610).setOpen(true);
		doors.get(20).setOpen(true);
		doors.get(706).setOpen(true);
		spawnColonels();
	}

	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
		doors.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}
