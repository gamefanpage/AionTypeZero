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

package org.typezero.gameserver.services;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.serial_killer.RankRestriction;
import org.typezero.gameserver.network.aion.serverpackets.SM_SERIAL_KILLER;
import org.typezero.gameserver.services.serialkillers.SerialKiller;
import org.typezero.gameserver.services.serialkillers.SerialKillerDebuff;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Source
 * @modified Dtem
 *
 */
public class SerialKillerService {

	private FastMap<Integer, SerialKiller> serialKillers = new FastMap<Integer, SerialKiller>();
	private FastMap<Integer, FastMap<Integer, Player>> worldKillers = new FastMap<Integer, FastMap<Integer, Player>>();
	private static final FastMap<Integer, WorldType> handledWorlds = new FastMap<Integer, WorldType>();
	private int refresh = CustomConfig.SERIALKILLER_REFRESH;
	private int levelDiff = CustomConfig.SERIALKILLER_LEVEL_DIFF;
	private SerialKillerDebuff debuff;

	public enum WorldType {

		ASMODIANS,
		ELYOS,
		USEALL;
	}

	public void initSerialKillers() {
		if (!CustomConfig.SERIALKILLER_ENABLED) {
			return;
		}

		for (String world : CustomConfig.SERIALKILLER_WORLDS.split(",")) {
			if ("".equals(world))
				break;
			int worldId = Integer.parseInt(world);
			int worldType = Integer.parseInt(String.valueOf(world.charAt(1)));
			debuff = new SerialKillerDebuff();
			WorldType type = worldType > 0 ? worldType > 1 ? WorldType.ASMODIANS : WorldType.ELYOS : WorldType.USEALL;
			handledWorlds.put(worldId, type);
		}

		ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				for (SerialKiller info : serialKillers.values()) {
					// chk if owner is offline
					if (info.victims > 0 && !isEnemyWorld(info.getOwner())) {
						info.victims -= CustomConfig.SERIALKILLER_DECREASE;
						int newRank = getKillerRank(info.victims);

						if (info.getRank() != newRank) {
							info.setRank(newRank);
							PacketSendUtility.sendPacket(info.getOwner(), new SM_SERIAL_KILLER(true, info.getRank()));
						}

						if (info.victims < 1) {
							info.victims = 0;
							serialKillers.remove(info.getOwner().getObjectId());
						}
					}
				}
			}
		}, refresh * 60000, refresh * 60000); // kills remove timer
	}

	public FastMap<Integer, Player> getWorldKillers(int worldId) {
		if (worldKillers.containsKey(worldId)) {
			return worldKillers.get(worldId);
		}
		else {
			FastMap<Integer, Player> killers = new FastMap<Integer, Player>();
			worldKillers.putEntry(worldId, killers);
			return killers;
		}
	}

	public void onLogin(Player player) {
		if (!CustomConfig.SERIALKILLER_ENABLED) {
			return;
		}

		if (serialKillers.containsKey(player.getObjectId())) {
			player.setSKInfo(serialKillers.get(player.getObjectId()));
			player.getSKInfo().refreshOwner(player);
		}
	}

	public void onLogout(Player player) {
		if (!CustomConfig.SERIALKILLER_ENABLED) {
			return;
		}

		onLeaveMap(player);
	}

	public void onEnterMap(final Player player) {
		if (!CustomConfig.SERIALKILLER_ENABLED) {
			return;
		}

		int worldId = player.getWorldId();
		SerialKiller info = player.getSKInfo();

		info.setRank(getKillerRank(info.victims));
		PacketSendUtility.sendPacket(player, new SM_SERIAL_KILLER(false, info.getRank()));

		if (!isHandledWorld(worldId)) {
			return;
		}

		if (isEnemyWorld(player)) {
			int objId = player.getObjectId();
			final FastMap<Integer, Player> world = getWorldKillers(worldId);


			if (!world.containsKey(objId)) {
				world.putEntry(objId, player);
			}

			debuff.applyEffect(player, info.getRank());

			World.getInstance().getWorldMap(worldId).
					getWorldMapInstanceById(player.getInstanceId()).doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player victim) {
					if (!player.getRace().equals(victim.getRace())) {
						PacketSendUtility.sendPacket(victim, new SM_SERIAL_KILLER(world.values()));
					}
				}

			});
		}
		else {
			PacketSendUtility.sendPacket(player, new SM_SERIAL_KILLER(getWorldKillers(worldId).values()));
		}
	}

	public void onLeaveMap(Player player) {
		int worldId = player.getWorldId();

		if (!isHandledWorld(worldId)) {
			return;
		}

		if (isEnemyWorld(player)) {
			SerialKiller info = player.getSKInfo();
			FastList<Player> kill = new FastList<Player>();
			FastMap<Integer, Player> killers = getWorldKillers(worldId);
			kill.addAll(killers.values());
			killers.remove(player.getObjectId());
			if (info.getRank() > 0) {
				info.setRank(0);
				debuff.endEffect(player);
				for (Player victim : World.getInstance().getWorldMap(worldId).
						getWorldMapInstanceById(player.getInstanceId()).getPlayersInside()) {
					if (!player.getRace().equals(victim.getRace())) {
						PacketSendUtility.sendPacket(victim, new SM_SERIAL_KILLER(kill));
					}
				}
			}
		}
	}

	public void updateIcons(Player player) {
		if (!isEnemyWorld(player)) {
			PacketSendUtility.sendPacket(player, new SM_SERIAL_KILLER(getWorldKillers(player.getWorldId()).values()));
		}
	}

	public void updateRank(final Player killer, Player victim) {
		if (isEnemyWorld(killer)) {
			SerialKiller info = killer.getSKInfo();

			if (killer.getLevel() >= victim.getLevel() + levelDiff) {
				int rank = getKillerRank(++info.victims);

				if (info.getRank() != rank) {
					info.setRank(rank);
					debuff.applyEffect(killer, rank);
					final FastMap<Integer, Player> killers = getWorldKillers(killer.getWorldId());
					PacketSendUtility.sendPacket(killer, new SM_SERIAL_KILLER(true, info.getRank()));
					World.getInstance().getWorldMap(killer.getWorldId()).
							getWorldMapInstanceById(killer.getInstanceId()).doOnAllPlayers(new Visitor<Player>() {
						@Override
						public void visit(Player observed) {
							if (!killer.getRace().equals(observed.getRace())) {
								PacketSendUtility.sendPacket(observed, new SM_SERIAL_KILLER(killers.values()));
							}
						}

					});
				}

				if (!serialKillers.containsKey(killer.getObjectId())) {
					serialKillers.put(killer.getObjectId(), info);
				}
			}
		}
	}

	private int getKillerRank(int kills) {
		// chk retail values for killer rank
		return kills > CustomConfig.KILLER_2ND_RANK_KILLS ? 2 : kills > CustomConfig.KILLER_1ST_RANK_KILLS ? 1 : 0;
	}

	public void onKillSerialKiller(final Player killer, final Player victim) {
		if (isEnemyWorld(victim)) {
			final SerialKiller info = victim.getSKInfo();
			victim.getPosition().getWorldMapInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					if (killer.getRace().equals(player.getRace()) && MathUtil.isIn3dRange(victim, player, 30))
						SkillEngine.getInstance().applyEffectDirectly(buffId(killer, info), player, player, 0);
				}
			});
		}
	}

	public boolean isRestrictPortal(Player killer) {
		SerialKiller info = killer.getSKInfo();
		if (info.getRank() > 0) {
			RankRestriction rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(info.getRank());
			if (rankRestriction.isRestrictDirectPortal()) {
				return true;
			}
		}
		return false;
	}

	public boolean isRestrictDynamicBindstone(Player killer) {
		SerialKiller info = killer.getSKInfo();
		if (info.getRank() > 0) {
			RankRestriction rankRestriction = DataManager.SERIAL_KILLER_DATA.getRankRestriction(info.getRank());
			if (rankRestriction.isRestrictDynamicBindstone()) {
				return true;
			}
		}
		return false;
	}

	public boolean isHandledWorld(int worldId) {
		return handledWorlds.containsKey(worldId);
	}

	public boolean isEnemyWorld(Player player) {
		if (handledWorlds.containsKey(player.getWorldId())) {
			WorldType homeType = player.getRace().equals(Race.ASMODIANS) ? WorldType.ASMODIANS : WorldType.ELYOS;
			return !handledWorlds.get(player.getWorldId()).equals(homeType);
		}

		return false;
	}

	private int buffId(Player player, SerialKiller info) {
		if (info.getRank() > 0)
			return player.getRace() == Race.ELYOS ? 8610 : 8611;
		return 0;
	}

	public static SerialKillerService getInstance() {
		return SerialKillerService.SingletonHolder.instance;
	}

	private static class SingletonHolder {

		protected static final SerialKillerService instance = new SerialKillerService();
	}

}
