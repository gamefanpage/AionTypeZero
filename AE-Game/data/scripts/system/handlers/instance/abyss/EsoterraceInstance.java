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

package instance.abyss;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;

import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.services.player.PlayerReviveService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;
import java.util.Map;


/**
 * @author xTz, Gigi
 */
@InstanceID(300250000)
public class EsoterraceInstance extends GeneralInstanceHandler {

	private Map<Integer, StaticDoor> doors;

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(367).setOpen(true);
		if (Rnd.get(0, 100) < 21) {
			spawn(799580, 1034.11f, 985.01f, 327.35095f, (byte) 105);
			spawn(217649, 1033.67f, 978.08f, 327.35095f, (byte) 35);
		}
	}

	@Override
	public void onDie(Npc npc) {
		switch (npc.getObjectTemplate().getTemplateId()) {
			case 282295:
				openDoor(39);
				break;
			case 282291: // Surkana Feeder enables "hardmode"
				sendMsg(1400996);
				getNpc(217204).getController().onDelete();
				spawn(217205, 1315.43f, 1171.04f, 51.8054f, (byte) 66);
				break;
			case 217289:
				sendMsg(1400924);
				openDoor(122);
				break;
			case 217281:
				sendMsg(1400921);
				openDoor(70);
				break;
			case 217195:
				sendMsg(1400922);
				openDoor(45);
				openDoor(52);
				openDoor(67);
				spawn(701027, 751.513489f, 1136.021851f, 365.031158f, (byte) 60, 41);
				spawn(701027, 829.620789f, 1134.330078f, 365.031281f, (byte) 60, 77);
				break;
			case 217185:
				spawn(701023, 1264.862061f, 644.995178f, 296.831818f, (byte) 60, 112);
				doors.get(367).setOpen(false);
				break;
			case 217204:
				spawn(205437, 1309.390259f, 1163.644287f, 51.493992f, (byte) 13);
				spawn(701027, 1318.669800f, 1180.467651f, 52.879887f, (byte) 75, 727);
				break;
			case 217206:
				spawn(205437, 1309.390259f, 1163.644287f, 51.493992f, (byte) 13);
				spawn(701027, 1318.669800f, 1180.467651f, 52.879887f, (byte) 75, 727);
				spawn(701027, 1325.484497f, 1173.198486f, 52.879887f, (byte) 75, 726);
				break;
			case 217649:
				// keening sirokin treasure chest
				Npc keeningSirokin = getNpc(799580);
				spawn(701025, 1038.63f, 987.74f, 328.356f, (byte) 0, 725);
				NpcShoutsService.getInstance().sendMsg(keeningSirokin, 342359, keeningSirokin.getObjectId(), 0, 0);
				keeningSirokin.getController().onDelete();
			case 217284:
			case 217283:
			case 217282:
				Npc npc1 = getNpc(217284);
				Npc npc2 = getNpc(217283);
				Npc npc3 = getNpc(217282);
				if (isDead(npc1) && isDead(npc2) && isDead(npc3)) {
					sendMsg(1400920);
					openDoor(111);
				}
				break;
		}
	}

	@Override
	public boolean onReviveEvent(Player player) {
		PlayerReviveService.revive(player, 25, 25, false, 0);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, STR_REBIRTH_MASSAGE_ME);
		TeleportService2.teleportTo(player, mapId, instanceId, 384.57535f, 535.4073f, 321.6642f, (byte) 17);
		return true;
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0,
			player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}

	private boolean isDead(Npc npc) {
		return (npc == null || npc.getLifeStats().isAlreadyDead());
	}

	@Override
	public void onEnterZone(Player player, ZoneInstance zone) {
		if (zone.getAreaTemplate().getZoneName() == ZoneName.get("DRANA_PRODUCTION_LAB_300250000")) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400919));
		}
	}

	private void openDoor(int doorId) {
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceDestroy() {
		doors.clear();
	}

}
