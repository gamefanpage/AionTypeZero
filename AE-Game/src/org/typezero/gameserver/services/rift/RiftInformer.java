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

package org.typezero.gameserver.services.rift;

import org.typezero.gameserver.controllers.RVController;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.SM_RIFT_ANNOUNCE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.ArrayList;
import java.util.List;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class RiftInformer {

	public static List<Npc> getSpawned(int worldId) {
		List<Npc> rifts = RiftManager.getSpawned();
		List<Npc> worldRifts = new ArrayList<Npc>();
		for (Npc rift : rifts) {
			if (rift.getWorldId() == worldId) {
				worldRifts.add(rift);
			}
		}

		return worldRifts;
	}

	public static void sendRiftsInfo(int worldId) {
		syncRiftsState(worldId, getPackets(worldId));
		int twinId = getTwinId(worldId);
		if (twinId > 0) {
			syncRiftsState(twinId, getPackets(twinId));
		}
	}

	public static void sendRiftsInfo(Player player) {
		syncRiftsState(player, getPackets(player.getWorldId()));
		int twinId = getTwinId(player.getWorldId());
		if (twinId > 0) {
			syncRiftsState(twinId, getPackets(twinId));
		}
	}

	public static void sendRiftInfo(int[] worlds) {
		for (int worldId : worlds) {
			syncRiftsState(worldId, getPackets(worlds[0], -1));
		}
	}

	public static void sendRiftDespawn(int worldId, int objId) {
		syncRiftsState(worldId, getPackets(worldId, objId), true);
	}

	private static List<AionServerPacket> getPackets(int worldId) {
		return getPackets(worldId, 0);
	}

	private static List<AionServerPacket> getPackets(int worldId, int objId) {
		List<AionServerPacket> packets = new ArrayList<AionServerPacket>();
		if (objId == -1) {
			for (Npc rift : getSpawned(worldId)) {
				RVController controller = (RVController) rift.getController();
				if (!controller.isMaster()) {
					continue;
				}

				packets.add(new SM_RIFT_ANNOUNCE(controller, false));
			}
		}
		else if (objId > 0) {
			packets.add(new SM_RIFT_ANNOUNCE(objId));
		}
		else {
			packets.add(new SM_RIFT_ANNOUNCE(getAnnounceData(worldId)));
			for (Npc rift : getSpawned(worldId)) {
				RVController controller = (RVController) rift.getController();
				if (!controller.isMaster()) {
					continue;
				}
				packets.add(new SM_RIFT_ANNOUNCE(controller, true));
				packets.add(new SM_RIFT_ANNOUNCE(controller, false));
			}
		}
		return packets;
	}

	/*
	 * Sends generated rift info packets to player
	 */
	private static void syncRiftsState(Player player, final List<AionServerPacket> packets) {
		for (AionServerPacket packet : packets) {
			PacketSendUtility.sendPacket(player, packet);
		}
	}

	/*
	 * Sends generated rift info packets to all players within world
	 */
	private static void syncRiftsState(int worldId, final List<AionServerPacket> packets) {
		syncRiftsState(worldId, packets, false);
	}

	private static void syncRiftsState(int worldId, final List<AionServerPacket> packets, final boolean isDespawnInfo) {
		World.getInstance().getWorldMap(worldId).getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {
			@Override
			public void visit(Player player) {
				syncRiftsState(player, packets);
			}

		});
	}

    private static FastMap<Integer, Integer> getAnnounceData(int worldId) {
        FastMap<Integer, Integer> localRifts = new FastMap<Integer, Integer>();

        // init empty list
        for (int i = 0; i < 8; i++) {
            localRifts.put(i, 0);
        }

        for (Npc rift : getSpawned(worldId)) {
            RVController rc = (RVController) rift.getController();
            localRifts = calcRiftsData(rc, localRifts);
        }

        return localRifts;
    }

    private static FastMap<Integer, Integer> calcRiftsData(RVController rift, FastMap<Integer, Integer> local) {
        if (rift.isMaster()) {
        	local.putEntry(0, local.get(0) + 1);
            if (rift.isVortex()) {
                local.putEntry(1, local.get(1) + 1);
            }
            local.putEntry(2, local.get(2) + 1);//live party
            local.putEntry(3, local.get(3) + 1);//shugo emperor vault
            local.putEntry(4, local.get(4) + 1);//rift battle
        } else {
            local.putEntry(5, local.get(5) + 1);//rift battle
            local.putEntry(6, local.get(6) + 1);//rift battle
            if (rift.isVortex()) {
                local.putEntry(7, local.get(7) + 1);
            }
        }
        return local;
    }

	private static int getTwinId(int worldId) {
		switch (worldId) {
			case 110070000:			// Kaisinel Academy -> Brusthonin
				return 220050000;
			case 210020000:			// Eltnen -> Morheim
				return 220020000;
			case 210040000:			// Heiron -> Beluslan
				return 220040000;
			case 210050000:			// Inggison -> Gelkmaros
				return 220070000;
			case 210060000:			// Theobomos -> Marchutan Priory
				return 120080000;
			case 120080000:			// Marchutan Priory -> Theobomos
				return 210060000;
			case 220020000:			// Morheim -> Eltnen
				return 210020000;
			case 220040000:			// Beluslan -> Heiron
				return 210040000;
			case 220050000:			// Brusthonin -> Kaisinel Academy
				return 110070000;
			case 220070000:			// Gelkmaros -> Inggison
				return 210050000;
			case 220080000:			// Enshar -> Singea
				return 210070000;
			case 210070000:			// Singea -> Enshar
				return 220080000;
			default:
				return 0;
		}
	}

}
