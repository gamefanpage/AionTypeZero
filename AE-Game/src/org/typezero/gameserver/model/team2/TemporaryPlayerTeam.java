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

package org.typezero.gameserver.model.team2;

import java.util.Collection;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.model.team2.common.legacy.LootRuleType;
import org.typezero.gameserver.model.team2.group.PlayerFilters;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.SM_PET;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @author ATracer
 */
public abstract class TemporaryPlayerTeam<TM extends TeamMember<Player>> extends GeneralTeam<Player, TM> {

	private LootGroupRules lootGroupRules = new LootGroupRules();

	public TemporaryPlayerTeam(Integer objId) {
		super(objId);
	}

	/**
	 * Level of the player with lowest exp
	 */
	public abstract int getMinExpPlayerLevel();

	/**
	 * Level of the player with highest exp
	 */
	public abstract int getMaxExpPlayerLevel();

	@Override
	public Race getRace() {
		return getLeader().getObject().getRace();
	}

	@Override
	public void sendPacket(AionServerPacket packet) {
		applyOnMembers(new TeamMessageSender(packet, Predicates.<Player> alwaysTrue()));
	}

	@Override
	public void sendPacket(AionServerPacket packet, Predicate<Player> predicate) {
		applyOnMembers(new TeamMessageSender(packet, predicate));
	}

	@Override
	public final int onlineMembers() {
		return getOnlineMembers().size();
	}

	@Override
	public final Collection<Player> getOnlineMembers() {
		return filterMembers(PlayerFilters.ONLINE);
	}

	protected final void initializeTeam(TM leader) {
		setLeader(leader);
	}

	public final LootGroupRules getLootGroupRules() {
		return lootGroupRules;
	}

	public void setLootGroupRules(LootGroupRules lootGroupRules) {
		this.lootGroupRules = lootGroupRules;
		if (lootGroupRules != null && lootGroupRules.getLootRule() == LootRuleType.FREEFORALL) {
			applyOnMembers(new TeamPacketGroupSender(PlayerFilters.HAS_LOOT_PET,
				SM_SYSTEM_MESSAGE.STR_MSG_LOOTING_PET_MESSAGE03, new SM_PET(13, false)));
		}
	}

	public static final class TeamPacketGroupSender implements Predicate<Player> {

		private final AionServerPacket[] packets;
		private final Predicate<Player> predicate;

		public TeamPacketGroupSender(Predicate<Player> predicate, AionServerPacket... packets) {
			this.packets = packets;
			this.predicate = predicate;
		}

		@Override
		public boolean apply(Player player) {
			if (predicate.apply(player)) {
				for (AionServerPacket packet : packets)
					PacketSendUtility.sendPacket(player, packet);
			}
			return true;
		}
	}

	public static final class TeamMessageSender implements Predicate<Player> {

		private final AionServerPacket packet;
		private final Predicate<Player> predicate;

		public TeamMessageSender(AionServerPacket packet, Predicate<Player> predicate) {
			this.packet = packet;
			this.predicate = predicate;
		}

		@Override
		public boolean apply(Player player) {
			if (predicate.apply(player)) {
				PacketSendUtility.sendPacket(player, packet);
			}
			return true;
		}
	}

}
