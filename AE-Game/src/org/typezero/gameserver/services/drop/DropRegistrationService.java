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

package org.typezero.gameserver.services.drop;

import com.aionemu.commons.utils.Rnd;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.configs.main.DropConfig;
import org.typezero.gameserver.configs.main.EventsConfig;
import org.typezero.gameserver.model.drop.Drop;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.drop.NpcDrop;
import org.typezero.gameserver.model.gameobjects.DropNpc;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.stats.container.StatEnum;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.model.templates.event.EventDrop;
import org.typezero.gameserver.model.templates.event.EventTemplate;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.model.templates.pet.PetFunctionType;
import org.typezero.gameserver.network.aion.serverpackets.SM_LOOT_STATUS;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.services.EventService;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.DropRewardEnum;
import javolution.util.FastList;

/**
 * @author xTz
 */
public class DropRegistrationService {

	private Map<Integer, Set<DropItem>> currentDropMap = new FastMap<Integer, Set<DropItem>>().shared();
	private Map<Integer, DropNpc> dropRegistrationMap = new FastMap<Integer, DropNpc>().shared();
	private FastList<Integer> noReductionMaps;

	public void registerDrop(Npc npc, Player player, Collection<Player> groupMembers) {
		registerDrop(npc, player, player.getLevel(), groupMembers);
	}

	private DropRegistrationService() {
		init();
		noReductionMaps = new FastList<Integer>();
		for (String zone : DropConfig.DISABLE_DROP_REDUCTION_IN_ZONES.split(",")) {
			noReductionMaps.add(Integer.parseInt(zone));
		}
	}

	public final void init() {

	}

	/**
	 * After NPC dies, it can register arbitrary drop
	 */
	public void registerDrop(Npc npc, Player player, int heighestLevel, Collection<Player> groupMembers) {

		if (player == null) {
			return;
		}
		int npcObjId = npc.getObjectId();

		// Getting all possible drops for this Npc
		NpcDrop npcDrop = npc.getNpcDrop();
		Set<DropItem> droppedItems = new HashSet<DropItem>();
		int index = 1;
		int dropChance = 100;
		int npcLevel = npc.getLevel();
		boolean isChest = npc.getAi2().getName().equals("chest");
		if (!DropConfig.DISABLE_DROP_REDUCTION && ((isChest && npcLevel != 1 || !isChest)) && !noReductionMaps.contains(npc.getWorldId())) {
			dropChance = DropRewardEnum.dropRewardFrom(npcLevel - heighestLevel); // reduce chance depending on level
		}

		// Generete drop by this player
		Player genesis = player;
		Integer winnerObj = 0;

		// Distributing drops to players
		Collection<Player> dropPlayers = new ArrayList<Player>();
		Collection<Player> winningPlayers = new ArrayList<Player>();
		if (player.isInGroup2() || player.isInAlliance2()) {
			List<Integer> dropMembers = new ArrayList<Integer>();
			LootGroupRules lootGrouRules = player.getLootGroupRules();

			switch (lootGrouRules.getLootRule()) {
				case ROUNDROBIN:
					int size = groupMembers.size();
					if (size > lootGrouRules.getNrRoundRobin())
						lootGrouRules.setNrRoundRobin(lootGrouRules.getNrRoundRobin() + 1);
					else
						lootGrouRules.setNrRoundRobin(1);

					int i = 0;
					for (Player p : groupMembers) {
						i++;
						if (i == lootGrouRules.getNrRoundRobin()) {
							winningPlayers.add(p);
							winnerObj = p.getObjectId();
							setItemsToWinner(droppedItems, winnerObj);
							genesis = p;
							break;
						}
					}
					break;
				case FREEFORALL:
					winningPlayers = groupMembers;
					break;
				case LEADER:
					Player leader = player.isInGroup2() ? player.getPlayerGroup2().getLeaderObject() : player
						.getPlayerAlliance2().getLeaderObject();
					winningPlayers.add(leader);
					winnerObj = leader.getObjectId();
					setItemsToWinner(droppedItems, winnerObj);

					genesis = leader;
					break;
			}

			for (Player member : winningPlayers) {
				dropMembers.add(member.getObjectId());
				dropPlayers.add(member);
			}
			DropNpc dropNpc = new DropNpc(npcObjId);
			dropRegistrationMap.put(npcObjId, dropNpc);
			dropNpc.setPlayersObjectId(dropMembers);
			dropNpc.setInRangePlayers(groupMembers);
			dropNpc.setGroupSize(groupMembers.size());
		}
		else {
			List<Integer> singlePlayer = new ArrayList<Integer>();
			singlePlayer.add(player.getObjectId());
			dropPlayers.add(player);
			dropRegistrationMap.put(npcObjId, new DropNpc(npcObjId));
			dropRegistrationMap.get(npcObjId).setPlayersObjectId(singlePlayer);
		}

		// Drop rate from NPC can be boosted by Spiritmaster Erosion skill
		float boostDropRate = npc.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f;

		//Drop rate can be boosted by player buff too
		boostDropRate += genesis.getGameStats().getStat(StatEnum.DR_BOOST, 0).getCurrent() / 100f;

		// Some personal drop boost
		// EoR 10% Boost drop rate
		boostDropRate += genesis.getCommonData().getCurrentReposteEnergy() > 0 ? 0.1f : 0;
		// EoS 5% Boost drop rate
		boostDropRate += genesis.getCommonData().getCurrentSalvationPercent() > 0 ? 0.05f : 0;
		// Deed to Palace 5% Boost drop rate
		boostDropRate += genesis.getActiveHouse() != null ? genesis.getActiveHouse().getHouseType().equals(HouseType.PALACE) ? 0.05f : 0 : 0;
		// Hmm.. 169625013 have boost drop rate 5% info but no such desc on buff

		// can be exploited on duel with Spiritmaster Erosion skill
		boostDropRate += genesis.getGameStats().getStat(StatEnum.BOOST_DROP_RATE, 100).getCurrent() / 100f - 1;

		float dropRate = genesis.getRates().getDropRate() * boostDropRate * dropChance / 100F;

		if (npcDrop != null) {
			index = npcDrop.dropCalculator(droppedItems, index, dropRate, genesis.getRace(), groupMembers);
		}

		// Updating current dropMap
		currentDropMap.put(npcObjId, droppedItems);


		index = QuestService.getQuestDrop(droppedItems, index, npc, groupMembers, genesis);

		if (EventsConfig.ENABLE_EVENT_SERVICE) {
			List<EventTemplate> activeEvents = EventService.getInstance().getActiveEvents();
			for (EventTemplate eventTemplate : activeEvents) {
				if (eventTemplate.EventDrop() == null) {
					continue;
				}
				List<EventDrop> eventDrops = eventTemplate.EventDrop().getEventDrops();
				for (EventDrop eventDrop : eventDrops) {
					int diff = npc.getLevel() - eventDrop.getItemTemplate().getLevel();
					int minDiff = eventDrop.getMinDiff();
					int maxDiff = eventDrop.getMaxDiff();
					if (minDiff != 0) {
						if (diff < eventDrop.getMinDiff()) {
							continue;
						}
					}
					if (maxDiff != 0) {
						if (diff > eventDrop.getMaxDiff()) {
							continue;
						}
					}
					float percent = eventDrop.getChance();
					percent *= dropRate;
					if (Rnd.get() * 100 > percent) {
						continue;
					}
					droppedItems.add(regDropItem(index++, winnerObj, npcObjId, eventDrop.getItemId(), eventDrop.getCount()));
				}
			}
		}

		if (npc.getPosition().isInstanceMap()) {
			npc.getPosition().getWorldMapInstance().getInstanceHandler().onDropRegistered(npc);
		}
		npc.getAi2().onGeneralEvent(AIEventType.DROP_REGISTERED);

		for (Player p : dropPlayers) {
			PacketSendUtility.sendPacket(p, new SM_LOOT_STATUS(npcObjId, 0));
		}

		if (player.getPet() != null && player.getPet().getPetTemplate().getPetFunction(PetFunctionType.LOOT) != null &&
				player.getPet().getCommonData().isLooting()) {
			PacketSendUtility.sendPacket(player, new SM_PET(true, npcObjId));
			Set<DropItem> drops = geCurrentDropMap().get(npcObjId);
			if (drops == null || drops.size() == 0) {
				npc.getController().onDelete();
			}
			else {
				DropItem[] dropItems = drops.toArray(new DropItem[0]);
				for (int i = 0; i < dropItems.length; i++) {
					DropService.getInstance().requestDropItem(player, npcObjId, dropItems[i].getIndex(), true);
				}
			}
			PacketSendUtility.sendPacket(player, new SM_PET(false, npcObjId));
			// if everything was looted, npc is deleted
			if (drops == null || drops.size() == 0)
				return;
		}
		DropService.getInstance().scheduleFreeForAll(npcObjId);
	}

	public void setItemsToWinner(Set<DropItem> droppedItems, Integer obj) {
		for (DropItem dropItem : droppedItems) {
			if (!dropItem.getDropTemplate().isEachMember()) {
				dropItem.setPlayerObjId(obj);
			}
		}
	}

	public DropItem regDropItem(int index, int playerObjId, int objId, int itemId, long count) {
		DropItem item = new DropItem(new Drop(itemId, 1, 1, 100, false));
		item.setPlayerObjId(playerObjId);
		item.setNpcObj(objId);
		item.setCount(count);
		item.setIndex(index);
		return item;
	}

	/**
	 * @return dropRegistrationMap
	 */
	public Map<Integer, DropNpc> getDropRegistrationMap() {
		return dropRegistrationMap;
	}

	/**
	 * @return currentDropMap
	 */
	public Map<Integer, Set<DropItem>> geCurrentDropMap() {
		return currentDropMap;
	}

	public static DropRegistrationService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DropRegistrationService instance = new DropRegistrationService();
	}

}
