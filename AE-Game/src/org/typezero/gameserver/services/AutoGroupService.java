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

import static ch.lambdaj.Lambda.*;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.instance.InstanceEngine;
import org.typezero.gameserver.model.autogroup.AGPlayer;
import org.typezero.gameserver.model.autogroup.AGQuestion;
import org.typezero.gameserver.model.autogroup.AutoGroupType;
import org.typezero.gameserver.model.autogroup.AutoInstance;
import org.typezero.gameserver.model.autogroup.EntryRequestType;
import org.typezero.gameserver.model.autogroup.LookingForParty;
import org.typezero.gameserver.model.autogroup.SearchInstance;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PortalCooldown;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.templates.InstanceCooltime;
import org.typezero.gameserver.network.aion.serverpackets.SM_AUTO_GROUP;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.instance.DredgionService2;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.instance.KamarBattlefieldService;
import org.typezero.gameserver.services.instance.PvPArenaService;
import org.typezero.gameserver.spawnengine.SpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldMap;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldMapInstanceFactory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javolution.util.FastList;
import javolution.util.FastMap;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author xTz
 */
public class AutoGroupService {
	private FastMap<Integer, LookingForParty> searchers = new FastMap<Integer, LookingForParty>().shared();
	private FastMap<Integer, AutoInstance> autoInstances = new FastMap<Integer, AutoInstance>().shared();
	private Collection<Integer> penaltys = new FastList<Integer>().shared();
	private Lock lock = new ReentrantLock();

	private AutoGroupService() {
	}

	public void startLooking(Player player, int instanceMaskId, EntryRequestType ert) {
		AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
		if (agt == null) {
			return;
		}
		if (!canEnter(player, ert, agt)) {
			return;
		}
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		if (penaltys.contains(obj)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400181, agt.getInstanceMapId()));
			return;
		}
		if (lfp == null) {
			searchers.put(obj, new LookingForParty(player, instanceMaskId, ert));
		}
		else if (lfp.hasPenalty() || lfp.isRegistredInstance(instanceMaskId)) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400181, agt.getInstanceMapId()));
			return;
		}
		else {
			lfp.addInstanceMaskId(instanceMaskId, ert);
		}

		if (ert.isGroupEntry()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (agt.isDredgion()) {
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6, true));
				}
				PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
				PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
			}
		}
		else {
			if (agt.isDredgion()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6, true));
			}
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400194, agt.getInstanceMapId()));
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 1, ert.getId(), player.getName()));
		}
		startSort(ert, instanceMaskId, true);
	}

	public synchronized void pressEnter(Player player, byte instanceMaskId) {
		AutoInstance instance = getAutoInstance(player, instanceMaskId);
		if (instance == null || instance.players.get(player.getObjectId()).isPressedEnter()) {
			return;
		}
		if (player.isInGroup2()) {
			PlayerGroupService.removePlayer(player);
		}
		if (player.isInAlliance2()) {
			PlayerAllianceService.removePlayer(player);
		}
		instance.onPressEnter(player);
		PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 5));
	}

	public void onEnterInstance(Player player) {
		if (player.isInInstance()) {
			Integer obj = player.getObjectId();
			AutoInstance autoInstance = autoInstances.get(player.getInstanceId());
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				autoInstance.onEnterInstance(player);
			}
		}
	}

	public void unregisterLooking(Player player, byte instanceMaskId) {
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		SearchInstance si;
		if (lfp != null) {
			lfp.setPenaltyTime();
			si = lfp.getSearchInstance(instanceMaskId);
			if (si != null) {
				if (lfp.unregisterInstance(instanceMaskId) == 0) {
					searchers.remove(obj);
					startPenalty(obj);
				}
				getInstance().unRegisterSearchInstance(player , si);
			}
		}
	}

	public void cancelEnter(Player player, int instanceMaskId) {
		AutoInstance autoInstance = getAutoInstance(player, instanceMaskId);
		if (autoInstance != null) {
			Integer obj = player.getObjectId();
			if (!autoInstance.players.get(obj).isInInstance()) {
				autoInstance.unregister(player);
				if (!searchers.containsKey(obj)) {
					startPenalty(obj);
				}
				if (autoInstance.agt.hasRegisterQuick()) {
					startSort(EntryRequestType.QUICK_GROUP_ENTRY, instanceMaskId, false);
				}
				if (autoInstance.players.isEmpty()) {
					WorldMapInstance instance = autoInstance.instance;
					autoInstance = autoInstances.remove(instance.getInstanceId());
					InstanceService.destroyInstance(instance);
					autoInstance.clear();
				}
			}
			if (autoInstance.agt.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
		}
	}

	public void onPlayerLogin(Player player) {
		if (DredgionService2.getInstance().isDredgionAvialable() && player.getLevel() > DredgionService2.minLevel && player.getLevel() < DredgionService2.capLevel
				&& !DredgionService2.getInstance().hasCoolDown(player)) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(DredgionService2.getInstance().getInstanceMaskId(player), SM_AUTO_GROUP.wnd_EntryIcon));
		}
		else if (KamarBattlefieldService.getInstance().isKamarAvialable() && player.getLevel() > KamarBattlefieldService.minLevel
				&& !KamarBattlefieldService.getInstance().hasCoolDown(player)) {
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(KamarBattlefieldService.maskId, SM_AUTO_GROUP.wnd_EntryIcon));
		}
		Integer obj = player.getObjectId();
		LookingForParty lfp = searchers.get(obj);
		if (lfp != null) {
			for (SearchInstance searchInstance : lfp.getSearchInstances()) {
				if (searchInstance.getEntryRequestType().isGroupEntry() && !player.isInGroup2()) {
					int instanceMaskId = searchInstance.getInstanceMaskId();
					lfp.unregisterInstance(instanceMaskId);
					if (searchInstance.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
					continue;
				}
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 8,
						searchInstance.getRemainingTime() + searchInstance.getEntryRequestType().getId(), player.getName()));
				if (searchInstance.isDredgion()) {
					PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(searchInstance.getInstanceMaskId(), 6, true));
				}
			}
			if (lfp.getSearchInstances().isEmpty()) {
				searchers.remove(obj);
				return;
			}
			lfp.setPlayer(player);
			for (SearchInstance si : lfp.getSearchInstances()) {
				startSort(si.getEntryRequestType(), si.getInstanceMaskId(), true);
			}
		}
	}

	public void onPlayerLogOut(Player player) {
		Integer obj = player.getObjectId();
		int instanceId = player.getInstanceId();
		LookingForParty lfp = searchers.get(obj);
		if (lfp != null) {
			lfp.setPlayer(null);
			if (lfp.isOnStartEnterTask()) {
				for (AutoInstance autoInstance : autoInstances.values()) {
					if (autoInstance.players.containsKey(obj) && !autoInstance.players.get(obj).isInInstance()) {
						cancelEnter(player, autoInstance.agt.getInstanceMaskId());
					}
				}
			}
		}

		if (player.isInInstance()) {
			AutoInstance autoInstance = autoInstances.get(instanceId);
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				WorldMapInstance instance = autoInstance.instance;
				if (instance != null ) {
					autoInstance.players.get(obj).setOnline(false);
					if (select(autoInstance.players, having(on(AGPlayer.class).isOnline(), equalTo(true))).isEmpty()) {
						autoInstance = autoInstances.remove(instanceId);
						InstanceService.destroyInstance(instance);
						autoInstance.clear();
					}
				}
			}
		}
	}

	public void onLeaveInstance(Player player) {
		if (player.isInInstance()) {
			Integer obj = player.getObjectId();
			int instanceId = player.getInstanceId();
			AutoInstance autoInstance = autoInstances.get(instanceId);
			if (autoInstance != null && autoInstance.players.containsKey(obj)) {
				autoInstance.onLeaveInstance(player);
				if (select(autoInstance.players, having(on(AGPlayer.class).isOnline(), equalTo(true))).isEmpty()) {
					WorldMapInstance instance = autoInstance.instance;
					autoInstances.remove(instanceId);
					if (instance != null) {
						InstanceService.destroyInstance(instance);
					}
				}
				else if (autoInstance.agt.hasRegisterQuick()) {
					startSort(EntryRequestType.QUICK_GROUP_ENTRY, autoInstance.agt.getInstanceMaskId(), false);
				}
			}
		}
	}

	private void startSort(EntryRequestType ert, Integer instanceMaskId, boolean checkNewGroup) {
		lock.lock();
		try {
			Collection<Player> players = new HashSet<Player>();
			if (ert.isQuickGroupEntry()) {
				for (LookingForParty lfp : searchers.values()) {
					if (lfp.getPlayer() == null || lfp.isOnStartEnterTask()) {
						continue;
					}
					for (AutoInstance autoInstance : autoInstances.values()) {
						int searchMaskId = autoInstance.agt.getInstanceMaskId();
						SearchInstance searchInstance = lfp.getSearchInstance(searchMaskId);
						if (searchInstance != null && searchInstance.getEntryRequestType().isQuickGroupEntry()) {
							Player owner = lfp.getPlayer();
							if (autoInstance.addPlayer(owner, searchInstance).isAdded()) {
								lfp.setStartEnterTime();
								if (lfp.unregisterInstance(searchMaskId) == 0) {
									players.add(owner);
								}
								PacketSendUtility.sendPacket(lfp.getPlayer(), new SM_AUTO_GROUP(searchMaskId, 4));
							}
						}
					}
				}
				for (Player p : players) {
					searchers.remove(p.getObjectId());
				}
				players.clear();
			}
			if (checkNewGroup) {
				AutoGroupType agt = AutoGroupType.getAGTByMaskId(instanceMaskId);
				AutoInstance autoInstance = agt.getAutoInstance();
				autoInstance.initialize(instanceMaskId);
				boolean canCreate = false;
				Iterator<LookingForParty> iter = searchers.values().iterator();
				LookingForParty lfp;
				while (iter.hasNext()) {
					lfp = iter.next();
					if (lfp.getPlayer() == null || lfp.isOnStartEnterTask()) {
						continue;
					}
					SearchInstance searchInstance = lfp.getSearchInstance(instanceMaskId);
					if (searchInstance != null) {
						if (searchInstance.getEntryRequestType().isGroupEntry()) {
							if (!lfp.getPlayer().isInGroup2()) {
								if (lfp.unregisterInstance(instanceMaskId) == 0) {
									iter.remove();
								}
								continue;
							}
						}
						AGQuestion question = autoInstance.addPlayer(lfp.getPlayer(), searchInstance);
						if (!question.isFailed()) {
							if (searchInstance.getEntryRequestType().isGroupEntry()) {
								for (Player member : lfp.getPlayer().getPlayerGroup2().getOnlineMembers()) {
									if (searchInstance.getMembers().contains(member.getObjectId())) {
										players.add(member);
									}
								}
							}
							else {
								players.add(lfp.getPlayer());
							}
						}
						if (question.isReady()) {
							canCreate = true;
							break;
						}
					}
				}
				if (canCreate) {
					WorldMapInstance instance = createInstance(agt.getInstanceMapId(), agt.getDifficultId());
					autoInstance.onInstanceCreate(instance);
					autoInstances.put(instance.getInstanceId(), autoInstance);
					for (Player player : players) {
						Integer obj = player.getObjectId();
						lfp = searchers.get(obj);
						if (lfp != null) {
							lfp.setStartEnterTime();
							if (lfp.unregisterInstance(autoInstance.agt.getInstanceMaskId()) == 0) {
								searchers.remove(obj);
							}
						}
						PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 4));
					}
				}
				else {
					autoInstance.clear();
				}
				players.clear();
			}
		}
		finally {
			lock.unlock();
		}
	}

	private boolean canEnter(Player player, EntryRequestType ert, AutoGroupType agt) {
		int mapId = agt.getInstanceMapId();
		int instanceMaskId = agt.getInstanceMaskId();
		if (!agt.hasLevelPermit(player.getLevel())) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
			return false;
		}

		if (agt.isDredgion() && !DredgionService2.getInstance().isDredgionAvialable()) {
			return false;
		}
		else if ((agt.isPvPFFAArena() || agt.isPvPSoloArena() || agt.isHarmonyArena() || agt.isGloryArena())
				&& !PvPArenaService.isPvPArenaAvailable(player, agt)) {
			return false;
		}
		else if (hasCoolDown(player, mapId)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANNOT_MAKE_INSTANCE_COOL_TIME);
			return false;
		}
		switch (ert) {
			case NEW_GROUP_ENTRY:
				if (!agt.hasRegisterNew()) {
					return false;
				}
				break;
			case QUICK_GROUP_ENTRY:
				if (!agt.hasRegisterQuick()) {
					return false;
				}
				break;
			case GROUP_ENTRY:
				if (!agt.hasRegisterGroup()) {
					return false;
				}
				PlayerGroup group = player.getPlayerGroup2();
				if (group == null || !group.isLeader(player)) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_NOT_LEADER);
					return false;
				}
				if (agt.isHarmonyArena() || agt.isTrainigHarmonyArena()) {
					if (group.getOnlineMembers().size() > 3) {
						PacketSendUtility.sendPacket(player,  SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_TOO_MANY_MEMBERS(3, Integer.toString(mapId)));
						return false;
					}
				}
				for (Player member : group.getMembers()) {
					if (group.getLeaderObject().equals(member)) {
						continue;
					}
					LookingForParty lfp = searchers.get(member.getObjectId());
					if (lfp != null && lfp.isRegistredInstance(instanceMaskId) ) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					if (agt.isHarmonyArena() && !PvPArenaService.checkItem(member, agt)) {
						PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1400219, agt.getInstanceMapId()));
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					if (agt.isDredgion() && DredgionService2.getInstance().hasCoolDown(member)) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					else if (hasCoolDown(member, mapId)) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
					if (!agt.hasLevelPermit(member.getLevel())) {
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_LEVEL);
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_CANT_INSTANCE_ENTER_MEMBER(member.getName()));
						return false;
					}
				}
				break;
		}
		return true;
	}

	private AutoInstance getAutoInstance(Player player, int instanceMaskId) {
		for (AutoInstance autoInstance : autoInstances.values()) {
			if (autoInstance.agt.getInstanceMaskId() == instanceMaskId && autoInstance.players.containsKey(player.getObjectId())) {
				return autoInstance;
			}
		}
		return null;
	}

	private boolean hasCoolDown(Player player, int worldId) {
        if (DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId) == null) {
            return false;
        }
		InstanceCooltime clt = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId);
        PortalCooldown portalCooldown = player.getPortalCooldownList().getPortalCooldown(clt.getId());
        if (portalCooldown == null)
        {
            return false;
		}
        if (clt == null)
            return false;
        if (clt.getCount() == 0)
            return player.getPortalCooldownList().isPortalUseDisabled(worldId);

        return (player.getPortalCooldownList().isPortalUseDisabled(worldId)) && (portalCooldown.getCount() == 0);
	}

	private WorldMapInstance createInstance(int worldId, byte difficultId) {
		WorldMap map = World.getInstance().getWorldMap(worldId);
		int nextInstanceId = map.getNextInstanceId();
		WorldMapInstance worldMapInstance = WorldMapInstanceFactory.createWorldMapInstance(map, nextInstanceId);
		map.addInstance(nextInstanceId, worldMapInstance);
		SpawnEngine.spawnInstance(worldId, worldMapInstance.getInstanceId(), difficultId);
		InstanceEngine.getInstance().onInstanceCreate(worldMapInstance);
		return worldMapInstance;
	}

	private void startPenalty(final Integer obj) {
		if (penaltys.contains(obj)) {
			penaltys.remove(obj);
		}
		penaltys.add(obj);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (penaltys.contains(obj)) {
					penaltys.remove(obj);
				}
			}
		}, 10000);
	}

	public void unRegisterInstance(byte instanceMaskId) {
		for (LookingForParty lfp : searchers.values()) {
			if (lfp.isRegistredInstance(instanceMaskId)) {
				if (lfp.getPlayer() != null) {
					getInstance().unregisterLooking(lfp.getPlayer(), instanceMaskId);
				}
				else {
					getInstance().unRegisterSearchInstance(null, lfp.getSearchInstance(instanceMaskId));
					if (lfp.unregisterInstance(instanceMaskId) == 0) {
						searchers.values().remove(lfp);
					}
				}
			}
		}
	}

	private void unRegisterSearchInstance(Player player, SearchInstance si) {
		int instanceMaskId = si.getInstanceMaskId();
		if (si.getEntryRequestType().isGroupEntry() && si.getMembers() != null) {
			for (Integer obj : si.getMembers()) {
				Player member = World.getInstance().findPlayer(obj);
				if (member != null) {
					if (si.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
						PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 6));
					}
					PacketSendUtility.sendPacket(member, new SM_AUTO_GROUP(instanceMaskId, 2));
				}
			}
		}
		if (player != null) {
			if (si.isDredgion() && DredgionService2.getInstance().isDredgionAvialable()) {
				PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 6));
			}
			PacketSendUtility.sendPacket(player, new SM_AUTO_GROUP(instanceMaskId, 2));
		}
	}

	public void unRegisterInstance(Integer instanceId) {
		AutoInstance autoInstance = autoInstances.remove(instanceId);
		if (autoInstance != null) {
			WorldMapInstance instance = autoInstance.instance;
			if (instance != null) {
				InstanceService.destroyInstance(instance);
			}
			autoInstance.clear();
		}
	}

	public boolean isAutoInstance(int instanceId) {
		return autoInstances.containsKey(instanceId);
	}

	public static AutoGroupService getInstance() {
		return NewSingletonHolder.INSTANCE;
	}

	private static class NewSingletonHolder {

		private static final AutoGroupService INSTANCE = new AutoGroupService();
	}

}
