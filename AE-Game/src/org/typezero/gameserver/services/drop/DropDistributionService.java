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
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.drop.DropItem;
import org.typezero.gameserver.model.gameobjects.DropNpc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.common.legacy.LootGroupRules;
import org.typezero.gameserver.network.aion.serverpackets.SM_GROUP_LOOT;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xTz
 */
public class DropDistributionService {

	private static Logger log = LoggerFactory.getLogger(DropDistributionService.class);

	public static DropDistributionService getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * @param Called
	 *          from CM_GROUP_LOOT to handle rolls
	 */
	public void handleRoll(Player player, int roll, int itemId, int npcId, int index) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (player == null || dropNpc == null) {
			return;
		}
		int luck = 0;
		if (player.isInGroup2() || player.isInAlliance2()) {
			if (roll == 0) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_ME);
			}
			else {
				luck = Rnd.get(1, 100);
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_ME(luck, 100));
			}
			for (Player member : dropNpc.getInRangePlayers()) {
				if (member == null) {
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? "
						+ player.isInAlliance2());
					continue;
				}

				int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member,
					new SM_GROUP_LOOT(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), luck, index));
				if (!player.equals(member) && member.isOnline()) {
					if (roll == 0) {
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_DICE_GIVEUP_OTHER(player.getName()));
					}
					else {
						PacketSendUtility.sendPacket(member,
							SM_SYSTEM_MESSAGE.STR_MSG_DICE_RESULT_OTHER(player.getName(), luck, 100));
					}
				}
			}
			distributeLoot(player, luck, itemId, npcId);
		}
	}

	/**
	 * @param Called
	 *          from CM_GROUP_LOOT to handle bids
	 */
	public void handleBid(Player player, long bid, int itemId, int npcId, int index) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		if (player == null || dropNpc == null) {
			return;
		}

		if (player.isInGroup2() || player.isInAlliance2()) {
			if ((bid > 0 && player.getInventory().getKinah() < bid) || bid < 0 || bid > 999999999) {
				bid = 0; // Set BID to 0 if player has bid more KINAH then they have in inventory or send negative value
			}

			if (bid > 0) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_RESULT_ME);
			}
			else {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_PAY_GIVEUP_ME);
			}

			for (Player member : dropNpc.getInRangePlayers()) {
				if (member == null) {
					log.warn("member null Owner is in group? " + player.isInGroup2() + " Owner is in Alliance? "
						+ player.isInAlliance2());
					continue;
				}

				int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member,
					new SM_GROUP_LOOT(teamId, member.getObjectId(), itemId, npcId, dropNpc.getDistributionId(), bid, index));
				if (!player.equals(member) && member.isOnline()) {
					if (bid > 0) {
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_RESULT_OTHER(player.getName()));
					}
					else {
						PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_GIVEUP_OTHER(player.getName()));
					}
				}
			}
			distributeLoot(player, bid, itemId, npcId);
		}
	}

	/**
	 * @param Checks
	 *          all players have Rolled or Bid then Distributes items accordingly
	 */
	private void distributeLoot(Player player, long luckyPlayer, int itemId, int npcId) {
		DropNpc dropNpc = DropRegistrationService.getInstance().getDropRegistrationMap().get(npcId);
		Set<DropItem> dropItems = DropRegistrationService.getInstance().geCurrentDropMap().get(npcId);
		DropItem requestedItem = null;

		if (dropItems == null)
			return;

		synchronized (dropItems) {
			for (DropItem dropItem : dropItems)
				if (dropItem.getIndex() == dropNpc.getCurrentIndex()) {
					requestedItem = dropItem;
					break;
				}
		}

		if (requestedItem == null)
			return;
		player.unsetPlayerMode(PlayerMode.IN_ROLL);
		// Removes player from ARRAY once they have rolled or bid
		if (dropNpc.containsPlayerStatus(player))
			dropNpc.delPlayerStatus(player);

		if (luckyPlayer > requestedItem.getHighestValue()) {
			requestedItem.setHighestValue(luckyPlayer);
			requestedItem.setWinningPlayer(player);
		}

		if (!dropNpc.getPlayerStatus().isEmpty())
			return;

		if (player.isInGroup2() || player.isInAlliance2()) {
			for (Player member : dropNpc.getInRangePlayers()) {
				if (member == null) {
					continue;
				}
				if (requestedItem.getWinningPlayer() == null) {
					PacketSendUtility.sendPacket(member, SM_SYSTEM_MESSAGE.STR_MSG_PAY_ALL_GIVEUP);
				}
				int teamId = member.getCurrentTeamId();
				PacketSendUtility.sendPacket(member, new SM_GROUP_LOOT(teamId,
					requestedItem.getWinningPlayer() != null ? requestedItem.getWinningPlayer().getObjectId() : 1, itemId, npcId,
					dropNpc.getDistributionId(), 0xFFFFFFFF, requestedItem.getIndex()));
			}
		}

		LootGroupRules lgr = player.getLootGroupRules();
		if (lgr != null) {
			lgr.removeItemToBeDistributed(requestedItem);
		}

		// Check if there is a Winning Player registered if not all members must have passed...
		if (requestedItem.getWinningPlayer() == null) {
			requestedItem.isFreeForAll(true);
			if (lgr != null && !lgr.getItemsToBeDistributed().isEmpty()) {
				DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
			}
			return;
		}

		requestedItem.isDistributeItem(true);
		DropService.getInstance().requestDropItem(player, npcId, dropNpc.getCurrentIndex());
		if (lgr != null && !lgr.getItemsToBeDistributed().isEmpty()) {
			DropService.getInstance().canDistribute(player, lgr.getItemsToBeDistributed().getFirst());
		}
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final DropDistributionService instance = new DropDistributionService();
	}

}
