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


package org.typezero.gameserver.services.event;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.configs.main.InvasionConfig;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Romanz
 */
public class InvasionBritra {
	private static final Logger log = LoggerFactory.getLogger(InvasionBritra.class);
	private static final String GERHA_INVASION_SPAWN_SCHEDULE = InvasionConfig.GERHA_INVASION_SPAWN_SCHEDULE;
	private FastMap<Integer, VisibleObject> gerhaInvasion = new FastMap<Integer, VisibleObject>();

	private static final String ENSHAR_INVASION_SPAWN_SCHEDULE = InvasionConfig.ENSHAR_INVASION_SPAWN_SCHEDULE;
	private FastMap<Integer, VisibleObject> ensharInvasion = new FastMap<Integer, VisibleObject>();

	private static final String SINGEA_INVASION_SPAWN_SCHEDULE = InvasionConfig.SINGEA_INVASION_SPAWN_SCHEDULE;
	private FastMap<Integer, VisibleObject> singeaInvasion = new FastMap<Integer, VisibleObject>();

	private static final String KALDOR_INVASION_SPAWN_SCHEDULE = InvasionConfig.KALDOR_INVASION_SPAWN_SCHEDULE;
	private FastMap<Integer, VisibleObject> kaldorInvasion = new FastMap<Integer, VisibleObject>();

	private static final InvasionBritra instance = new InvasionBritra();
	public static InvasionBritra getInstance() {
		return instance;
	}

	public void initStart() {
		if (!InvasionConfig.INVASION_ENABLED) {
                return;
            }
		//Gerha
		CronService.getInstance().schedule(new Runnable() {
		int mobsIds[] = {234609, 234610, 234611, 234612, 234614};
		int mobsIdsrnd = Rnd.get(mobsIds);
			@Override
			public void run() {
				if (gerhaInvasion.containsKey(mobsIds)) {
                                log.warn("Invasion Gerha was already spawned...");
                            }
				else {
					int randomPos = Rnd.get(1, 5);
					switch (randomPos) {
						case 1:
							gerhaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, mobsIdsrnd, 1158.565f, 1075.491f, 303.5f, (byte) 0), 1));
							gerhaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, 832819, 1158.565f, 1075.491f, 303.5f, (byte) 0), 1));
							break;
						case 2:
							gerhaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, mobsIdsrnd, 681.2332f, 1001.908f, 275.0771f, (byte) 0), 1));
							gerhaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, 832819, 681.2332f, 1001.908f, 275.0771f, (byte) 0), 1));
							break;
						case 3:
							gerhaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, mobsIdsrnd, 387.4441f, 1809.429f, 226.46526f, (byte) 0), 1));
							gerhaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, 832819, 387.4441f, 1809.429f, 226.46526f, (byte) 0), 1));
							break;
						case 4:
							gerhaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, mobsIdsrnd, 1838.406f, 141.1846f, 242.50826f, (byte) 0), 1));
							gerhaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600100000, 832819, 1838.406f, 141.1846f, 242.50826f, (byte) 0), 1));
							break;
					}
					log.info("Invasion spawned in Gerha");
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
                            for (VisibleObject vo : gerhaInvasion.values())
                            if (vo != null)
							PacketSendUtility.sendPacket(player, new SM_NPC_INFO((Npc) vo, player));
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402459));
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402384));
						}

					});
					//Despawned 1 hr if not killed
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							for (VisibleObject vo : gerhaInvasion.values()) {
								if (vo != null) {
									Npc npc = (Npc) vo;
									if (!npc.getLifeStats().isAlreadyDead()) {
										npc.getController().onDelete();
									}
								}
								gerhaInvasion.clear();
								log.info("Invasion Gerha despawn");
								World.getInstance().doOnAllPlayers(new Visitor<Player>() {
									@Override
									public void visit(Player player) {
									PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402385));
									}

								});
							}
						}

					}, 3600 * 1000);
				}
			}

		}, GERHA_INVASION_SPAWN_SCHEDULE);


		//Enshar
		CronService.getInstance().schedule(new Runnable() {
		int mobsIds[] = {234609, 234610, 234611, 234612, 234614};
		int mobsIdsrnd = Rnd.get(mobsIds);
			@Override
			public void run() {
				if (ensharInvasion.containsKey(mobsIds)) {
                                log.warn("Invasion Enshar was already spawned...");
                            }
				else {
					int randomPos = Rnd.get(1, 3);
					switch (randomPos) {
						case 1:
							ensharInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, mobsIdsrnd, 2653.46f, 2719.95f, 203.2f, (byte) 0), 1));
							ensharInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, 832819, 2653.46f, 2719.95f, 203.2f, (byte) 0), 1));
							break;
						case 2:
							ensharInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, mobsIdsrnd, 1582.96f, 1105.84f, 132.8f, (byte) 0), 1));
							ensharInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, 832819, 1582.96f, 1105.84f, 132.8f, (byte) 0), 1));
							break;
						case 3:
							ensharInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, mobsIdsrnd, 192.86f, 526.93f, 196.71f, (byte) 0), 1));
							ensharInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(220080000, 832819, 192.86f, 526.93f, 196.71f, (byte) 0), 1));
							break;
					}
					log.info("Invasion spawned in Enshar");
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
                            for (VisibleObject vo : ensharInvasion.values())
                            if (vo != null)
							PacketSendUtility.sendPacket(player, new SM_NPC_INFO((Npc) vo, player));
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402459));
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402384));
						}

					});
					//Despawned 1 hr if not killed
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							for (VisibleObject vo : ensharInvasion.values()) {
								if (vo != null) {
									Npc npc = (Npc) vo;
									if (!npc.getLifeStats().isAlreadyDead()) {
										npc.getController().onDelete();
									}
								}
								ensharInvasion.clear();
								log.info("Invasion Enshar despawn");
								World.getInstance().doOnAllPlayers(new Visitor<Player>() {
									@Override
									public void visit(Player player) {
									PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402385));
									}

								});
							}
						}

					}, 3600 * 1000);
				}
			}

		}, ENSHAR_INVASION_SPAWN_SCHEDULE);

		//Singea
		CronService.getInstance().schedule(new Runnable() {
		int mobsIds[] = {234609, 234610, 234611, 234612, 234614};
		int mobsIdsrnd = Rnd.get(mobsIds);
			@Override
			public void run() {
				if (singeaInvasion.containsKey(mobsIds)) {
                                log.warn("Invasion Singea was already spawned...");
                            }
				else {
					int randomPos = Rnd.get(1, 3);
					switch (randomPos) {
						case 1:
							singeaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, mobsIdsrnd, 594.88f, 481.64f, 416.45f, (byte) 0), 1));
							singeaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, 832819, 594.88f, 481.64f, 416.45f, (byte) 0), 1));
							break;
						case 2:
							singeaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, mobsIdsrnd, 2945.27f, 2274.98f, 231.45f, (byte) 0), 1));
							singeaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, 832819, 2945.27f, 2274.98f, 231.45f, (byte) 0), 1));
							break;
						case 3:
							singeaInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, mobsIdsrnd, 1429.93f, 1949.66f, 138.62f, (byte) 0), 1));
							singeaInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(210070000, 832819, 1429.93f, 1949.66f, 138.62f, (byte) 0), 1));
							break;
					}
					log.info("Invasion spawned in Singea");
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
                            for (VisibleObject vo : singeaInvasion.values())
                            if (vo != null)
							PacketSendUtility.sendPacket(player, new SM_NPC_INFO((Npc) vo, player));
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402459));
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402384));
						}

					});
					//Despawned 1 hr if not killed
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							for (VisibleObject vo : singeaInvasion.values()) {
								if (vo != null) {
									Npc npc = (Npc) vo;
									if (!npc.getLifeStats().isAlreadyDead()) {
										npc.getController().onDelete();
									}
								}
								singeaInvasion.clear();
								log.info("Invasion Singea despawn");
								World.getInstance().doOnAllPlayers(new Visitor<Player>() {
									@Override
									public void visit(Player player) {
									PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402385));
									}

								});
							}
						}

					}, 3600 * 1000);
				}
			}

		}, SINGEA_INVASION_SPAWN_SCHEDULE);

		//Kaldor
		CronService.getInstance().schedule(new Runnable() {
		int mobsIds[] = {234609, 234610, 234611, 234612, 234614};
		int mobsIdsrnd = Rnd.get(mobsIds);
			@Override
			public void run() {
				if (kaldorInvasion.containsKey(mobsIds)) {
                                log.warn("Invasion Kaldor was already spawned...");
                            }
				else {
					int randomPos = Rnd.get(1, 3);
					switch (randomPos) {
						case 1:
							kaldorInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, mobsIdsrnd, 655.87f, 808.41f, 164.9f, (byte) 0), 1));
							kaldorInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, 832819, 655.87f, 808.41f, 164.9f, (byte) 0), 1));
							break;
						case 2:
							kaldorInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, mobsIdsrnd, 1181.87f, 348.52f, 128.52f, (byte) 0), 1));
							kaldorInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, 832819, 1181.87f, 348.52f, 128.52f, (byte) 0), 1));
							break;
						case 3:
							kaldorInvasion.put(mobsIdsrnd, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, mobsIdsrnd, 289.29f, 505.44f, 158.15f, (byte) 0), 1));
							kaldorInvasion.put(832819, SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(600090000, 832819, 289.29f, 505.44f, 158.15f, (byte) 0), 1));
							break;
					}
					log.info("Invasion spawned in Kaldor");
					World.getInstance().doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player player) {
                            for (VisibleObject vo : kaldorInvasion.values())
                            if (vo != null)
							PacketSendUtility.sendPacket(player, new SM_NPC_INFO((Npc) vo, player));
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402459));
                            PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402384));
						}

					});
					//Despawned 1 hr if not killed
					ThreadPoolManager.getInstance().schedule(new Runnable() {
						@Override
						public void run() {
							for (VisibleObject vo : kaldorInvasion.values()) {
								if (vo != null) {
									Npc npc = (Npc) vo;
									if (!npc.getLifeStats().isAlreadyDead()) {
										npc.getController().onDelete();
									}
								}
								kaldorInvasion.clear();
								log.info("Invasion Kaldor despawn");
								World.getInstance().doOnAllPlayers(new Visitor<Player>() {
									@Override
									public void visit(Player player) {
									PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1402385));
									}

								});
							}
						}

					}, 3600 * 1000);
				}
			}

		}, KALDOR_INVASION_SPAWN_SCHEDULE);

    }
}
