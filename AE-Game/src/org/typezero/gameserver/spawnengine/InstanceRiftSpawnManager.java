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

package org.typezero.gameserver.spawnengine;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ginho1
 */
public class InstanceRiftSpawnManager {

	private static final Logger log = LoggerFactory.getLogger(InstanceRiftSpawnManager.class);

	private static final ConcurrentLinkedQueue<VisibleObject> rifts = new ConcurrentLinkedQueue<VisibleObject>();

	private static final int RIFT_RESPAWN_DELAY	= 3600;	// 1 hour
	private static final int RIFT_LIFETIME		= 3500;	// 1 hour

	public static void spawnAll() {

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				for (RiftEnum rift : RiftEnum.values()) {
					if(Rnd.get(1, 100) > 30)
						continue;

					spawnInstanceRift(rift);
				}
			}
		}, 0, RIFT_RESPAWN_DELAY * 1000);
	}

	private static void spawnInstanceRift(RiftEnum rift) {
		log.info("Spawning Instance Rift: " + rift.name());

		SpawnTemplate spawn = SpawnEngine.addNewSpawn(rift.getWorldId(), rift.getNpcId(),
				rift.getX(), rift.getY(), rift.getZ(), (byte) 0, 0);

		if (rift.getStaticId() > 0)
			spawn.setStaticId(rift.getStaticId());

		VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, 1);

		rifts.add(visibleObject);

		scheduleDelete(visibleObject);
		sendAnnounce(visibleObject);
	}

	private static void scheduleDelete(final VisibleObject visObj) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if(visObj != null && visObj.isSpawned()) {
					visObj.getController().delete();
					rifts.remove(visObj);
				}
			}
		}, RIFT_LIFETIME * 1000);
	}

	public enum RiftEnum {
    DraupnirCave(700564, 1617, Race.ELYOS, 210040000, 2528.662f, 2680.882f, 155.050f),
		IndratuFortress(700565, 0, Race.ASMODIANS, 220040000, 1466.8792f, 1947.9192f, 588.06555f);

		private int npc_id;
		private int static_id;
		private Race race;
		private int worldId;
		private float x;
		private float y;
		private float z;

		private RiftEnum(int npc_id, int static_id, Race race, int worldId, float x , float y, float z) {
			this.npc_id = npc_id;
			this.static_id = static_id;
			this.race = race;
			this.worldId = worldId;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getNpcId() {
			return npc_id;
		}

		public int getStaticId() {
			return static_id;
		}

		public Race getRace() {
			return race;
		}

		public int getWorldId() {
			return worldId;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public float getZ() {
			return z;
		}
	}

	public static void sendInstanceRiftStatus(Player activePlayer) {
		for (VisibleObject visObj : rifts) {
			if (visObj.getWorldId() == activePlayer.getWorldId()) {
				sendMessage(activePlayer, visObj.getObjectTemplate().getTemplateId());
			}
		}
	}

	public static void sendAnnounce(final VisibleObject visObj) {
		if (visObj.isSpawned()) {
			WorldMapInstance worldInstance = visObj.getPosition().getMapRegion().getParent();

			worldInstance.doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player player) {
					if (player.isSpawned()) {
						sendMessage(player, visObj.getObjectTemplate().getTemplateId());
					}
				}
			});
		}
	}

	public static void sendMessage(Player player, int npc_id) {
		switch(npc_id) {
			case 700564:
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400276));
			break;
			case 700565:
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400275));
			break;
		}
	}
}
