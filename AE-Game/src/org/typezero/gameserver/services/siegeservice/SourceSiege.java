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
package org.typezero.gameserver.services.siegeservice;

import com.aionemu.commons.callbacks.util.GlobalCallbackHelper;
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dao.SiegeDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.siege.SourceLocation;
import org.typezero.gameserver.model.templates.siegelocation.SiegeReward;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.mail.AbyssSiegeLevel;
import org.typezero.gameserver.services.mail.MailFormatter;
import org.typezero.gameserver.services.mail.SiegeResult;
import org.typezero.gameserver.services.player.PlayerService;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Source
 */
public class SourceSiege extends Siege<SourceLocation> {

	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");

	private final AbyssPointsListener addAPListener = new AbyssPointsListener(this);

	public SourceSiege(SourceLocation siegeLocation) {
		super(siegeLocation);
	}

	@Override
	protected void onSiegeStart() {
		if(LoggingConfig.LOG_SIEGE)
			log.info("[SIEGE] > Siege started. [SOURCE:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:"+ getSiegeLocation().getLegionId()+"]");
		getSiegeLocation().setPreparation(false);
		getSiegeLocation().setVulnerable(true);
		getSiegeLocation().setUnderShield(true);
		broadcastState(getSiegeLocation());
		GlobalCallbackHelper.addCallback(addAPListener);
		deSpawnNpcs(getSiegeLocationId());
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();
	}

	@Override
	protected void onSiegeFinish() {
		if(LoggingConfig.LOG_SIEGE) {
			SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
			if(winner != null)
				log.info("[SIEGE] > Siege finished. [SOURCE:" + getSiegeLocationId() + "] [OLD RACE: " + getSiegeLocation().getRace() + "] [OLD LegionId:"+ getSiegeLocation().getLegionId()+"] [NEW RACE: "+winner.getSiegeRace()+"] [NEW LegionId:"+(winner.getWinnerLegionId() == null ? 0 : winner.getWinnerLegionId())+"]");
			else
				log.info("[SIEGE] > Siege finished. No winner found [SOURCE:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:"+ getSiegeLocation().getLegionId()+"]");
		}
		GlobalCallbackHelper.removeCallback(addAPListener);
		unregisterSiegeBossListeners();
		deSpawnNpcs(getSiegeLocationId());
		getSiegeLocation().setVulnerable(false);
		getSiegeLocation().setUnderShield(false);
		if (isBossKilled()) {
			onCapture();
			broadcastUpdate(getSiegeLocation(), getSiegeLocation().getTemplate().getNameId());
		}
		else
			broadcastState(getSiegeLocation());
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);
		if (SiegeRace.BALAUR != getSiegeLocation().getRace())
			giveRewardsToPlayers(getSiegeCounter().getRaceCounter(getSiegeLocation().getRace()));
		DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());
		updateTiamarantaRiftsStatus(false, false);
	}

	@Override
	public boolean isEndless() {
		return false;
	}

	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
		getSiegeCounter().addAbyssPoints(player, abysPoints);
	}

	public void onCapture() {
		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
		getSiegeLocation().setRace(winner.getSiegeRace());
		// If new race is balaur
		if (SiegeRace.BALAUR == winner.getSiegeRace()) {
			getSiegeLocation().setLegionId(0);
		}
		else {
			Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
		}
	}

	protected void giveRewardsToPlayers(SiegeRaceCounter winnerDamage) {
		// Get the map with playerId to siege reward
		Map<Integer, Long> playerAbyssPoints = winnerDamage.getPlayerAbyssPoints();
		List<Integer> topPlayersIds = Lists.newArrayList(playerAbyssPoints.keySet());
		Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());

		// Black Magic Here :)
		int i = 0;
		List<SiegeReward> playerRewards = getSiegeLocation().getReward();
		int rewardLevel = 0;

		for (SiegeReward topGrade : playerRewards) {
			AbyssSiegeLevel level = AbyssSiegeLevel.getLevelById(++rewardLevel);
			for (int rewardedPC = 0; i < topPlayersIds.size() && rewardedPC < topGrade.getTop(); ++i) {
				Integer playerId = topPlayersIds.get(i);

				PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
				++rewardedPC;
				if (LoggingConfig.LOG_SIEGE) {
					log.info("[SIEGE]  > [SOURCE:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Player Reward to: " +  playerNames.get(playerId) + "] ITEM RETURN "
						+ topGrade.getItemId() + " ITEM COUNT " + topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE);
				}
                int points = topGrade.getGloryPoint();
                if (points > 0)
                    AbyssPointsService.addAGp(pcd.getPlayer(), 0, points);
				MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, level
									, SiegeResult.OCCUPY, System.currentTimeMillis(), topGrade.getItemId()
									,	topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);
			}
		}
	}

}
