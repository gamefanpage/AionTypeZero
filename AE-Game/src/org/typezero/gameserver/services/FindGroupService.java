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

import java.util.ArrayList;
import java.util.Collection;

import javolution.util.FastMap;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.commons.objects.filter.ObjectFilter;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.FindGroup;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.callback.AddPlayerToAllianceCallback;
import org.typezero.gameserver.model.team2.alliance.callback.PlayerAllianceCreateCallback;
import org.typezero.gameserver.model.team2.alliance.callback.PlayerAllianceDisbandCallback;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.callback.AddPlayerToGroupCallback;
import org.typezero.gameserver.model.team2.group.callback.PlayerGroupCreateCallback;
import org.typezero.gameserver.model.team2.group.callback.PlayerGroupDisbandCallback;
import org.typezero.gameserver.network.aion.serverpackets.SM_FIND_GROUP;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * Find Group Service
 *
 * @author cura, MrPoke
 */
public class FindGroupService {

	private FastMap<Integer, FindGroup> elyosRecruitFindGroups = new FastMap<Integer, FindGroup>().shared();
	private FastMap<Integer, FindGroup> elyosApplyFindGroups = new FastMap<Integer, FindGroup>().shared();
	private FastMap<Integer, FindGroup> asmodianRecruitFindGroups = new FastMap<Integer, FindGroup>().shared();
	private FastMap<Integer, FindGroup> asmodianApplyFindGroups = new FastMap<Integer, FindGroup>().shared();

	private FindGroupService() {

		GlobalCallbackHelper.addCallback(new FindGroupOnAddPlayerToGroupListener());
		GlobalCallbackHelper.addCallback(new FindGroupPlayerGroupdDisbandListener());
		GlobalCallbackHelper.addCallback(new FindGroupPlayerGroupdCreateListener());
		GlobalCallbackHelper.addCallback(new FindGroupOnAddPlayerToAllianceListener());
		GlobalCallbackHelper.addCallback(new FindGroupAllianceDisbandListener());
		GlobalCallbackHelper.addCallback(new FindGroupAllianceCreateListener());
	}

	public void addFindGroupList(Player player, int action, String message, int groupType) {
		AionObject object = null;
		if (player.isInTeam()) {
			object = player.getCurrentTeam();
		}
		else {
			object = player;
		}

		FindGroup findGroup = new FindGroup(object, message, groupType);
		int objectId = object.getObjectId();
		switch (player.getRace()) {
			case ELYOS:
				switch (action) {
					case 0x02:
						elyosRecruitFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400392));
						break;
					case 0x06:
						elyosApplyFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400393));
						break;
				}
				break;
			case ASMODIANS:
				switch (action) {
					case 0x02:
						asmodianRecruitFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400392));
						break;
					case 0x06:
						asmodianApplyFindGroups.put(objectId, findGroup);
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400393));
						break;
				}
				break;
		}

		Collection<FindGroup> findGroupList = new ArrayList<FindGroup>();
		findGroupList.add(findGroup);

		PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(action, ((int) (System.currentTimeMillis() / 1000)),
			findGroupList));
	}

	public void updateFindGroupList(Player player, String message, int objectId) {
		FindGroup findGroup = null;

		switch (player.getRace()) {
			case ELYOS:
				findGroup = elyosRecruitFindGroups.get(objectId);
				findGroup.setMessage(message);
				break;
			case ASMODIANS:
				findGroup = asmodianRecruitFindGroups.get(objectId);
				findGroup.setMessage(message);
				break;
		}
	}

	public Collection<FindGroup> getFindGroups(Race race, int action) {
		switch (race) {
			case ELYOS:
				switch (action) {
					case 0x00:
						return elyosRecruitFindGroups.values();
					case 0x04:
						return elyosApplyFindGroups.values();
				}
				break;
			case ASMODIANS:
				switch (action) {
					case 0x00:
						return asmodianRecruitFindGroups.values();
					case 0x04:
						return asmodianApplyFindGroups.values();
				}
				break;
		}
		return null;
	}

	public void sendFindGroups(Player player, int action) {
		PacketSendUtility.sendPacket(player, new SM_FIND_GROUP(action, (int) (System.currentTimeMillis() / 1000),
			getFindGroups(player.getRace(), action)));
	}

	public FindGroup removeFindGroup(final Race race, int action, int playerObjId) {
		FindGroup findGroup = null;
		switch (race) {
			case ELYOS:
				switch (action) {
					case 0x00:
						findGroup = elyosRecruitFindGroups.remove(playerObjId);
						break;
					case 0x04:
						findGroup = elyosApplyFindGroups.remove(playerObjId);
						break;
				}
				break;
			case ASMODIANS:
				switch (action) {
					case 0x00:
						findGroup = asmodianRecruitFindGroups.remove(playerObjId);
						break;
					case 0x04:
						findGroup = asmodianApplyFindGroups.remove(playerObjId);
						break;
				}
				break;
		}
		if (findGroup != null)
			PacketSendUtility.broadcastFilteredPacket(new SM_FIND_GROUP(action + 1, playerObjId, findGroup.getUnk()),
				new ObjectFilter<Player>() {

					@Override
					public boolean acceptObject(Player object) {
						return race == object.getRace();
					}
				});
		return findGroup;
	}

	public void clean() {
		cleanMap(elyosRecruitFindGroups, Race.ELYOS, 0x00);
		cleanMap(elyosApplyFindGroups, Race.ELYOS, 0x04);
		cleanMap(asmodianRecruitFindGroups, Race.ASMODIANS, 0x00);
		cleanMap(asmodianApplyFindGroups, Race.ASMODIANS, 0x04);
	}

	private void cleanMap(FastMap<Integer, FindGroup> map, Race race, int action) {
		for (FindGroup group : map.values()) {
			if (group.getLastUpdate() + 60 * 60 < System.currentTimeMillis() / 1000)
				removeFindGroup(race, action, group.getObjectId());
		}
	}

	public static final FindGroupService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final FindGroupService instance = new FindGroupService();
	}

	static class FindGroupOnAddPlayerToGroupListener extends AddPlayerToGroupCallback {

		@Override
		public void onBeforePlayerAddToGroup(PlayerGroup group, Player player) {
			FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
			FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		}

		@Override
		public void onAfterPlayerAddToGroup(PlayerGroup group, Player player) {
			if (group.isFull()) {
				FindGroupService.getInstance().removeFindGroup(group.getRace(), 0, group.getObjectId());
			}
		}
	}

	static class FindGroupPlayerGroupdDisbandListener extends PlayerGroupDisbandCallback {

		@Override
		public void onBeforeGroupDisband(PlayerGroup group) {
			FindGroupService.getInstance().removeFindGroup(group.getRace(), 0, group.getTeamId());
		}

		@Override
		public void onAfterGroupDisband(PlayerGroup group) {
		}
	}

	static class FindGroupPlayerGroupdCreateListener extends PlayerGroupCreateCallback {

		@Override
		public void onBeforeGroupCreate(Player player) {
		}

		@Override
		public void onAfterGroupCreate(Player player) {
			FindGroup inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00,
				player.getObjectId());
			if (inviterFindGroup == null)
				inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
			if (inviterFindGroup != null)
				FindGroupService.getInstance().addFindGroupList(player, 0x02, inviterFindGroup.getMessage(),
					inviterFindGroup.getGroupType());
		}

	}

	static class FindGroupAllianceDisbandListener extends PlayerAllianceDisbandCallback {

		@Override
		public void onBeforeAllianceDisband(PlayerAlliance alliance) {
			FindGroupService.getInstance().removeFindGroup(alliance.getRace(), 0, alliance.getTeamId());
		}

		@Override
		public void onAfterAllianceDisband(PlayerAlliance alliance) {
		}
	}

	static class FindGroupAllianceCreateListener extends PlayerAllianceCreateCallback {

		@Override
		public void onBeforeAllianceCreate(Player player) {
		}

		@Override
		public void onAfterAllianceCreate(Player player) {
			FindGroup inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00,
				player.getObjectId());
			if (inviterFindGroup == null)
				inviterFindGroup = FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
			if (inviterFindGroup != null)
				FindGroupService.getInstance().addFindGroupList(player, 0x02, inviterFindGroup.getMessage(),
					inviterFindGroup.getGroupType());
		}

	}

	static class FindGroupOnAddPlayerToAllianceListener extends AddPlayerToAllianceCallback {

		@Override
		public void onBeforePlayerAddToAlliance(PlayerAlliance alliance, Player player) {
			FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
			FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		}

		@Override
		public void onAfterPlayerAddToAlliance(PlayerAlliance alliance, Player player) {
			if (alliance.isFull())
				FindGroupService.getInstance().removeFindGroup(alliance.getRace(), 0, alliance.getObjectId());
		}

	}
}
