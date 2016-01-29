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
import org.typezero.gameserver.model.siege.ArtifactLocation;
import org.typezero.gameserver.model.siege.FortressLocation;
import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.team.legion.LegionRank;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLegionReward;
import org.typezero.gameserver.model.templates.siegelocation.SiegeReward;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.mail.AbyssSiegeLevel;
import org.typezero.gameserver.services.mail.MailFormatter;
import org.typezero.gameserver.services.mail.SiegeResult;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Object that controls siege of certain fortress. Siege object is not reusable.
 * New siege = new instance.
 * <p/>
 *
 * @author SoulKeeper
 */
public class FortressSiege extends Siege<FortressLocation> {

	private static final Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final AbyssPointsListener addAPListener = new AbyssPointsListener(this);

	private int oldLegionId = 0;
    private int newLegionId = 0;

	public FortressSiege(FortressLocation fortress) {
		super(fortress);
	}

	@Override
	public void onSiegeStart() {
		if(LoggingConfig.LOG_SIEGE)
			log.info("[SIEGE] > Siege started. [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:"+ getSiegeLocation().getLegionId()+"]");
		// Mark fortress as vulnerable
		getSiegeLocation().setVulnerable(true);

		// Let the world know where the siege are
		broadcastState(getSiegeLocation());

		// Clear fortress from enemys
		getSiegeLocation().clearLocation();

		// Register abyss points listener
		// We should listen for abyss point callbacks that players are earning
		GlobalCallbackHelper.addCallback(addAPListener);

		// Remove all and spawn siege NPCs
		deSpawnNpcs(getSiegeLocationId());
		spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();
	}

	@Override
	public void onSiegeFinish() {
		if(LoggingConfig.LOG_SIEGE) {
			SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();
			if(winner != null)
				log.info("[SIEGE] > Siege finished. [FORTRESS:" + getSiegeLocationId() + "] [OLD RACE: " + getSiegeLocation().getRace() + "] [OLD LegionId:"+ getSiegeLocation().getLegionId()+"] [NEW RACE: "+winner.getSiegeRace()+"] [NEW LegionId:"+(winner.getWinnerLegionId() == null ? 0 : winner.getWinnerLegionId())+"]");
			else
				log.info("[SIEGE] > Siege finished. No winner found [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LegionId:"+ getSiegeLocation().getLegionId()+"]");
		}

        SiegeRace oldSiegeRace = getSiegeLocation().getRace();

		// Unregister abyss points listener callback
		// We really don't need to add abyss points anymore
		GlobalCallbackHelper.removeCallback(addAPListener);

		// Unregister siege boss listeners
		// cleanup :)
		unregisterSiegeBossListeners();

		// despawn protectors and make fortress invulnerable
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());
		getSiegeLocation().setVulnerable(false);
		getSiegeLocation().setUnderShield(false);

		// Guardian deity general was not killed, fortress stays with previous
		if (isBossKilled()) {
			onCapture();
			broadcastUpdate(getSiegeLocation());
		}
		else
			broadcastState(getSiegeLocation());


		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.PEACE);

		// Reward players and owning legion
		// If fortress was not captured by balaur
		if (SiegeRace.BALAUR != getSiegeLocation().getRace()) {
			giveRewardsToLegion();
			giveRewardsToPlayers(getSiegeCounter().getRaceCounter(getSiegeLocation().getRace()));  // награждение победителей
            if (getSiegeLocation().getRace() == SiegeRace.ASMODIANS && oldSiegeRace != SiegeRace.BALAUR)                               // награждение проигравших
                giveGPRewardsToLoosePlayers(getSiegeCounter().getRaceCounter(SiegeRace.ELYOS));
            if (getSiegeLocation().getRace() == SiegeRace.ELYOS && oldSiegeRace != SiegeRace.BALAUR)
                giveGPRewardsToLoosePlayers(getSiegeCounter().getRaceCounter(SiegeRace.ASMODIANS));
		}

		// Update outpost status
		// Certain fortresses are changing outpost ownership
		updateOutpostStatusByFortress(getSiegeLocation());

		// Update data in the DB
		DAOManager.getDAO(SiegeDAO.class).updateSiegeLocation(getSiegeLocation());

		getSiegeLocation().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				player.unsetInsideZoneType(ZoneType.SIEGE);
				if (isBossKilled() && (SiegeRace.getByRace(player.getRace()) == getSiegeLocation().getRace()))
					QuestEngine.getInstance().onKill(new QuestEnv(getBoss(), player, 0, 0));
			}

		});
	}

	public void onCapture() {
		SiegeRaceCounter winner = getSiegeCounter().getWinnerRaceCounter();

		// Set new fortress and artifact owner race
		getSiegeLocation().setRace(winner.getSiegeRace());
		getArtifact().setRace(winner.getSiegeRace());

		// If new race is balaur
		if (SiegeRace.BALAUR == winner.getSiegeRace()) {
			getSiegeLocation().setLegionId(0);
			getArtifact().setLegionId(0);
		}
		else {
            oldLegionId = getSiegeLocation().getLegionId();
            newLegionId = winner.getWinnerLegionId();
			Integer topLegionId = winner.getWinnerLegionId();
			getSiegeLocation().setLegionId(topLegionId != null ? topLegionId : 0);
			getArtifact().setLegionId(topLegionId != null ? topLegionId : 0);
		}
	}

	@Override
	public boolean isEndless() {
		return false;
	}

	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
		getSiegeCounter().addAbyssPoints(player, abysPoints);
	}

	protected void giveRewardsToLegion() {
		// We do not give rewards if fortress was captured for first time
		if (isBossKilled()) {
			if(LoggingConfig.LOG_SIEGE)
				log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LEGION :"+getSiegeLocation().getLegionId()+"] Legion Reward not sending because fortress was captured(siege boss killed).");
			return;
		}

		// Legion with id 0 = not exists?
		if (getSiegeLocation().getLegionId() == 0) {
			if(LoggingConfig.LOG_SIEGE)
				log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] [LEGION :"+getSiegeLocation().getLegionId()+"] Legion Reward not sending because fortress not owned by any legion.");
			return;
		}

		List<SiegeLegionReward> legionRewards = getSiegeLocation().getLegionReward();
		int legionBGeneral = LegionService.getInstance().getLegionBGeneral(getSiegeLocation().getLegionId());
		if (legionBGeneral != 0) {
			PlayerCommonData BGeneral = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(legionBGeneral);
			if (LoggingConfig.LOG_SIEGE) {
				log.info("[SIEGE] > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Legion Reward in process... LegionId:"
						+ getSiegeLocation().getLegionId() + " General Name:" + BGeneral.getName());
			}
			if (legionRewards != null) {
				for (SiegeLegionReward medalsType : legionRewards) {
					if (LoggingConfig.LOG_SIEGE) {
						log.info("[SIEGE] > [Legion Reward to: " +  BGeneral.getName() + "] ITEM RETURN "
							+ medalsType.getItemId() + " ITEM COUNT " +  medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE);
					}
					MailFormatter.sendAbyssRewardMail(getSiegeLocation(), BGeneral, AbyssSiegeLevel.NONE
									, SiegeResult.PROTECT, System.currentTimeMillis(), medalsType.getItemId()
									,	medalsType.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);

				}
			}
		}
	}

	protected void giveRewardsToPlayers(SiegeRaceCounter winnerDamage) {
		// Get the map with playerId to siege reward
		Map<Integer, Long> playerAbyssPoints = winnerDamage.getPlayerAbyssPoints();
		List<Integer> topPlayersIds = Lists.newArrayList(playerAbyssPoints.keySet());
		Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());
		SiegeResult result = isBossKilled() ? SiegeResult.OCCUPY : SiegeResult.DEFENDER;

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
					log.info("[SIEGE]  > [FORTRESS:" + getSiegeLocationId() + "] [RACE: " + getSiegeLocation().getRace() + "] Player Reward to: " +  playerNames.get(playerId) + "] ITEM RETURN "
						+ topGrade.getItemId() + " ITEM COUNT " + topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE);
				}

                int points = topGrade.getGloryPoint();

                // LOG for test
                try {
                    log.info("[SIEGE REWARD LOG FORTRESS: "+getSiegeLocationId()+"] RESULT: "+result.name()+". PLAYER: "+ playerNames.get(playerId) + ". RACE: " + pcd.getRace() + ". STATE: winner. GP: " + points + ". RANK: "+ pcd.getPlayer().getLegionMember().getRank().name());

                	// Успешный захват/оборона
					if (points > 0)
						AbyssPointsService.addAGp(pcd.getPlayer(), 0, points);

					// Легат легиона, завладевшего крепостью
					if (pcd.getPlayer() != null && pcd.getPlayer().getLegionMember() != null && pcd.getPlayer().getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL && result == SiegeResult.OCCUPY && pcd.getPlayer().getLegion().getLegionId() == newLegionId)
						AbyssPointsService.addAGp(pcd.getPlayer(), 0, 1000);

					MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, level, result, System.currentTimeMillis()
									, topGrade.getItemId(),	topGrade.getCount() * SiegeConfig.SIEGE_MEDAL_RATE, 0);
				} catch (Exception e) {
					log.error("[SIEGE REWARD LOG FORTRESS] siege log error, may be char not online");
				}
			}
		}

        // Адъютанты и легат легиона, защитившего крепость
        if (result == SiegeResult.DEFENDER) {
            for (int playerId : topPlayersIds) {
                PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
				try {
					if (pcd.getPlayer().getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL || pcd.getPlayer().getLegionMember().getRank() == LegionRank.DEPUTY && pcd.getPlayer().getLegion().getLegionId() == newLegionId) {
						int rewardPoints = 500 / (1 + LegionService.getInstance().getMembersCountByRank(pcd.getPlayer().getLegion().getLegionId(), LegionRank.DEPUTY));
						AbyssPointsService.addAGp(pcd.getPlayer(), 0, rewardPoints);
					}
				} catch (Exception e) {
					log.error("[SIEGE REWARD LOG FORTRESS] siege log error, may be char not online");
				}
            }
        }

		if (!isBossKilled()) {
			while (i < topPlayersIds.size()) {
				i++;
				Integer playerId = topPlayersIds.get(i);
				PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
				//Send Announcement Mails without reward to the rest
				MailFormatter.sendAbyssRewardMail(getSiegeLocation(), pcd, AbyssSiegeLevel.NONE, SiegeResult.EMPTY, System.currentTimeMillis(), 0, 0, 0);
			}
		}
	}

    protected void giveGPRewardsToLoosePlayers(SiegeRaceCounter looserDamage) {
        Map<Integer, Long> playerAbyssPoints = looserDamage.getPlayerAbyssPoints();
        List<Integer> playersIds = Lists.newArrayList(playerAbyssPoints.keySet());
        Map<Integer, String> playerNames = PlayerService.getPlayerNames(playerAbyssPoints.keySet());
        SiegeResult result = isBossKilled() ? SiegeResult.OCCUPY : SiegeResult.DEFENDER;
        List<SiegeReward> playerRewards = getSiegeLocation().getReward();
        int i = 0;

        for (SiegeReward topGrade : playerRewards) {
            for (int rewardedPC = 0; i < playersIds.size() && rewardedPC < topGrade.getTop(); ++i) {
                Integer playerId = playersIds.get(i);
                PlayerCommonData pcd = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(playerId);
                ++rewardedPC;

                int points = (int)(topGrade.getGloryPoint() * 0.5);

                try {
                    // Не успешный захват/оборона
                    if (points > 0)
                        AbyssPointsService.addAGp(pcd.getPlayer(), 0, points);

                    // Легат легиона, потерявшего крепость
                    if (pcd.getPlayer() != null && pcd.getPlayer().getLegionMember().getRank() == LegionRank.BRIGADE_GENERAL && result == SiegeResult.OCCUPY && pcd.getPlayer().getLegion().getLegionId() == oldLegionId)
						if (pcd.getPlayer().getAbyssRank().getGp() > 0)
							if (pcd.getPlayer().getAbyssRank().getGp() > 1000)
								AbyssPointsService.addAGp(pcd.getPlayer(), 0, -1000);
							else
								AbyssPointsService.addAGp(pcd.getPlayer(), 0, -pcd.getPlayer().getAbyssRank().getGp());

                } catch (NullPointerException e) {
                    log.error("SIEGE loose reward error. Player id "+playerId);
                }

                // LOG for test
                try {
                    log.info("[SIEGE REWARD LOG FORTRESS: "+getSiegeLocationId()+"] RESULT: "+result.name()+". PLAYER: "+ playerNames.get(playerId) + ". RACE: " + pcd.getRace() + ". STATE: looser. GP: " + points + ". RANK: "+ pcd.getPlayer().getLegionMember().getRank().name());
                } catch (Exception e) {
                    log.error("siege log error, may be char not online");
                }
                //-------------
            }
        }
    }

	protected ArtifactLocation getArtifact() {
		return SiegeService.getInstance().getFortressArtifacts().get(getSiegeLocationId());
	}

	protected boolean hasArtifact() {
		return getArtifact() != null;
	}

}
