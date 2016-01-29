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

package org.typezero.gameserver.services.player;

import com.aionemu.commons.database.dao.DAOManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.AutoGroupConfig;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.dao.*;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.items.storage.StorageType;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.network.aion.clientpackets.CM_QUIT;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.*;
import org.typezero.gameserver.services.drop.DropService;
import org.typezero.gameserver.services.instance.InstanceService;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.services.toypet.PetSpawnService;
import org.typezero.gameserver.taskmanager.tasks.ExpireTimerTask;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.utils.audit.GMService;

import java.sql.Timestamp;

/**
 * @author ATracer
 */
public class PlayerLeaveWorldService {

	private static final Logger log = LoggerFactory.getLogger(PlayerLeaveWorldService.class);

	/**
	 * @param player
	 * @param delay
	 */
	public static final void startLeaveWorldDelay(final Player player, int delay) {
		// force stop movement of player
		player.getController().stopMoving();

		ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				startLeaveWorld(player);
			}

		}, delay);
	}

	/**
	 * This method is called when player leaves the game, which includes just
	 * two cases: either player goes back to char selection screen or it's
	 * leaving the game [closing client].<br> <br> <b><font color='red'>NOTICE:
	 * </font> This method is called only from {@link GameConnection} and
	 * {@link CM_QUIT} and must not be called from anywhere else</b>
	 */
	public static final void startLeaveWorld(Player player) {
		log.info("Player logged out: " + player.getName() + " Account: "
				+ (player.getClientConnection() != null ? player.getClientConnection().getAccount().getName() : "disconnected"));
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x00, player.getObjectId());
		FindGroupService.getInstance().removeFindGroup(player.getRace(), 0x04, player.getObjectId());
		player.onLoggedOut();
		BrokerService.getInstance().removePlayerCache(player);
		ExchangeService.getInstance().cancelExchange(player);
		RepurchaseService.getInstance().removeRepurchaseItems(player);
		if (AutoGroupConfig.AUTO_GROUP_ENABLE) {
			AutoGroupService.getInstance().onPlayerLogOut(player);
		}
		SerialKillerService.getInstance().onLogout(player);
		InstanceService.onLogOut(player);
		GMService.getInstance().onPlayerLogedOut(player);
		KiskService.getInstance().onLogout(player);
        PlayerFatigueService.getInstance().onPlayerLogout(player);
		player.getMoveController().abortMove();

		if (player.isLooting())
			DropService.getInstance().closeDropList(player, player.getLootingNpcOid());

		// Update prison timer
		if (player.isInPrison()) {
			long prisonTimer = System.currentTimeMillis() - player.getStartPrison();
			prisonTimer = player.getPrisonTimer() - prisonTimer;
			player.setPrisonTimer(prisonTimer);
			log.debug("Update prison timer to " + prisonTimer / 1000 + " seconds !");
		}
		// store current effects
		DAOManager.getDAO(PlayerEffectsDAO.class).storePlayerEffects(player);
		DAOManager.getDAO(PlayerCooldownsDAO.class).storePlayerCooldowns(player);
		DAOManager.getDAO(ItemCooldownsDAO.class).storeItemCooldowns(player);
		DAOManager.getDAO(HouseObjectCooldownsDAO.class).storeHouseObjectCooldowns(player);
		DAOManager.getDAO(PlayerLifeStatsDAO.class).updatePlayerLifeStat(player);

		PlayerGroupService.onPlayerLogout(player);
		PlayerAllianceService.onPlayerLogout(player);
		// fix legion warehouse exploits
		LegionService.getInstance().LegionWhUpdate(player);
		player.getEffectController().removeAllEffects(true);
		player.getLifeStats().cancelAllTasks();

		if (player.getLifeStats().isAlreadyDead()) {
			if (player.isInInstance())
				PlayerReviveService.instanceRevive(player);
			else
				PlayerReviveService.bindRevive(player);
		}
		else if (DuelService.getInstance().isDueling(player.getObjectId())) {
			DuelService.getInstance().loseDuel(player);
		}
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.LOGOUT);
		}
		PetSpawnService.dismissPet(player, true);

		if (player.getPostman() != null)
			player.getPostman().getController().onDelete();
		player.setPostman(null);

		PunishmentService.stopPrisonTask(player, true);
		PunishmentService.stopGatherableTask(player, true);

		if (player.isLegionMember())
			LegionService.getInstance().onLogout(player);

		QuestEngine.getInstance().onLogOut(new QuestEnv(null, player, 0, 0));

		player.getController().delete();
		player.getCommonData().setOnline(false);
		player.getCommonData().setLastOnline(new Timestamp(System.currentTimeMillis()));
		player.setClientConnection(null);

		DAOManager.getDAO(PlayerDAO.class).onlinePlayer(player, false);

		if (GSConfig.ENABLE_CHAT_SERVER)
			ChatService.onPlayerLogout(player);

		PlayerService.storePlayer(player);

		ExpireTimerTask.getInstance().removePlayer(player);
		if (player.getCraftingTask() != null)
			player.getCraftingTask().stop(true);
		player.getEquipment().setOwner(null);
		player.getInventory().setOwner(null);
		player.getWarehouse().setOwner(null);
		player.getStorage(StorageType.ACCOUNT_WAREHOUSE.getId()).setOwner(null);
                // broadcast player delete beam
                PacketSendUtility.broadcastPacket(player, new SM_DELETE(player, 2), 50);

                PlayerAccountData pad = player.getPlayerAccount().getPlayerAccountData(player.getObjectId());
                pad.setEquipment(player.getEquipment().getEquippedItems());
	}

	/**
	 * @param player
	 */
	public static void tryLeaveWorld(Player player) {
		player.getMoveController().abortMove();
		if (player.getController().isInShutdownProgress())
			PlayerLeaveWorldService.startLeaveWorld(player);
		// prevent ctrl+alt+del / close window exploit
		else {
			int delay = 15;
			PlayerLeaveWorldService.startLeaveWorldDelay(player, (delay * 1000));
		}
	}

}
