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

import com.aionemu.commons.utils.Rnd;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastMap;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.controllers.attack.AggroInfo;
import org.typezero.gameserver.controllers.attack.KillList;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RewardType;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.services.item.ItemService;
import org.typezero.gameserver.utils.audit.AuditLogger;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.typezero.gameserver.utils.stats.StatFunctions;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Sarynth
 */
public class PvpService {

   private static Logger log = LoggerFactory.getLogger("KILL_LOG");

   public static final PvpService getInstance() {
	  return SingletonHolder.instance;
   }
   private FastMap<Integer, KillList> pvpKillLists;

   private PvpService() {
	  pvpKillLists = new FastMap<Integer, KillList>();
   }

   /**
    * @param winnerId
    * @param victimId
    * @return
    */
   private int getKillsFor(int winnerId, int victimId) {
	  KillList winnerKillList = pvpKillLists.get(winnerId);

	  if (winnerKillList == null)
		 return 0;
	  return winnerKillList.getKillsFor(victimId);
   }

   /**
    * @param winnerId
    * @param victimId
    */
   private void addKillFor(int winnerId, int victimId) {
	  KillList winnerKillList = pvpKillLists.get(winnerId);
	  if (winnerKillList == null) {
		 winnerKillList = new KillList();
		 pvpKillLists.put(winnerId, winnerKillList);
	  }
	  winnerKillList.addKillFor(victimId);
   }

   /**
    * @param victim
    */
   public void doReward(final Player victim) {
	  // winner is the player that receives the kill count
	  final Player winner = victim.getAggroList().getMostPlayerDamage();

	  int totalDamage = victim.getAggroList().getTotalDamage();

	  if (totalDamage == 0 || winner == null) {
		 return;
	  }

	  // Add Player Kill to record.
	  if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
		 winner.getAbyssRank().setAllKill();
		 int kills = winner.getAbyssRank().getAllKill();
		 // Pvp Kill Reward.
		 if (CustomConfig.ENABLE_KILL_REWARD) {
			if (kills % CustomConfig.KILLS_NEEDED1 == 1) {
			   ItemService.addItem(winner, CustomConfig.REWARD1, 1);
			   PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD1
					   + "] for having killed " + CustomConfig.KILLS_NEEDED1 + " players !");
			   log.info("[REWARD] Player [" + winner.getName() + "] win 2 [" + CustomConfig.REWARD1 + "]");
			}
			if (kills % CustomConfig.KILLS_NEEDED2 == 3) {
			   ItemService.addItem(winner, CustomConfig.REWARD2, 1);
			   PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD2
					   + "] for having killed " + CustomConfig.KILLS_NEEDED2 + " players !");
			   log.info("[REWARD] Player [" + winner.getName() + "] win 4 [" + CustomConfig.REWARD2 + "]");
			}
			if (kills % CustomConfig.KILLS_NEEDED3 == 5) {
			   ItemService.addItem(winner, CustomConfig.REWARD3, 1);
			   PacketSendUtility.sendMessage(winner, "Congratulations, you have won " + "[item: " + CustomConfig.REWARD3
					   + "] for having killed " + CustomConfig.KILLS_NEEDED3 + " players !");
			   log.info("[REWARD] Player [" + winner.getName() + "] win 6 [" + CustomConfig.REWARD3 + "]");
			}
		 }
	  }

       // Announce that player has died to all world.
       World.getInstance().doOnAllPlayers(new Visitor<Player>() {
           @Override
           public void visit(Player player) {
               PacketSendUtility.sendYellowMessage(player, "" + winner.getName() + " \u0443\u0431\u0438\u0432\u0430\u0435\u0442 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u0436\u0430 " + victim.getName());
           }
       });

	  // Announce that player has died.
	  //PacketSendUtility.broadcastPacketAndReceive(victim,SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH_TO_B(victim.getName(), winner.getName()));

	  //Kill-log
	  if (LoggingConfig.LOG_KILL)
		 log.info("[KILL] Player [" + winner.getName() + "] killed [" + victim.getName() + "]");

	  if (LoggingConfig.LOG_PL) {
		 String ip1 = winner.getClientConnection().getIP();
		 String mac1 = winner.getClientConnection().getMacAddress();
		 String ip2 = victim.getClientConnection().getIP();
		 String mac2 = victim.getClientConnection().getMacAddress();
		 if (mac1 != null && mac2 != null) {
			if (ip1.equalsIgnoreCase(ip2) && mac1.equalsIgnoreCase(mac2)) {
			   AuditLogger.info(winner, "Possible Power Leveling : " + winner.getName() + " with " + victim.getName() + "; same ip=" + ip1 + " and mac=" + mac1 + ".");
			}
			else if (mac1.equalsIgnoreCase(mac2)) {
			   AuditLogger.info(winner, "Possible Power Leveling : " + winner.getName() + " with " + victim.getName() + "; same mac=" + mac1 + ".");
			}
		 }
	  }

	  // Keep track of how much damage was dealt by players
	  // so we can remove AP based on player damage...
	  int playerDamage = 0;
	  boolean success;

	  // Distribute AP to groups and players that had damage.
	  for (AggroInfo aggro : victim.getAggroList().getFinalDamageList(true)) {
		 success = false;
		 if (aggro.getAttacker() instanceof Player) {
			success = rewardPlayer(victim, totalDamage, aggro);
		 }
		 else if (aggro.getAttacker() instanceof PlayerGroup) {
			success = rewardPlayerGroup(victim, totalDamage, aggro);
		 }
		 else if (aggro.getAttacker() instanceof PlayerAlliance) {
			success = rewardPlayerAlliance(victim, totalDamage, aggro);
		 }

		 // Add damage last, so we don't include damage from same race. (Duels, Arena)
		 if (success)
			playerDamage += aggro.getDamage();
	  }

	  SerialKillerService.getInstance().updateRank(winner, victim);

	  SerialKillerService.getInstance().onKillSerialKiller(winner, victim);

	  //notify Quest engine for winner + his group
	  notifyKillQuests(winner, victim);

	  // Apply lost AP to defeated player
	  final int apLost = StatFunctions.calculatePvPApLost(victim, winner);
	  final int apActuallyLost = (int) (apLost * playerDamage / totalDamage);

	  /*if (apActuallyLost > 0) {
           if (winner.getWorldId() == 600100000 && winner.getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId() && victim.getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId()) {
               int gp = 1;
               gp *= Rnd.get(1, 10);
                   AbyssPointsService.addAGp(victim, -apActuallyLost, -gp);
           }
           else {
                   AbyssPointsService.addAGp(victim, -apActuallyLost, 0);
           }
       }*/
	  if (apActuallyLost > 0)
		 AbyssPointsService.addAGp(victim, -apActuallyLost, 0);

   }

   /**
    * @param victim
    * @param totalDamage
    * @param aggro
    * @return true if group is not same race
    */
   private boolean rewardPlayerGroup(Player victim, int totalDamage, AggroInfo aggro) {
	  // Reward Group
	  PlayerGroup group = ((PlayerGroup) aggro.getAttacker());

	  // Don't Reward Player of Same Faction.
	  if (group.getRace() == victim.getRace())
		 return false;

	  // Find group members in range
	  List<Player> players = new ArrayList<Player>();

	  // Find highest rank and level in local group
	  int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
	  int maxLevel = 0;

	  for (Player member : group.getMembers()) {
		 if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
			// Don't distribute AP to a dead player!
			if (!member.getLifeStats().isAlreadyDead()) {
			   players.add(member);
			   if (member.getLevel() > maxLevel)
				  maxLevel = member.getLevel();
			   if (member.getAbyssRank().getRank().getId() > maxRank)
				  maxRank = member.getAbyssRank().getRank().getId();
			}
		 }
	  }

	  // They are all dead or out of range.
	  if (players.isEmpty())
		 return false;

	  int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
	  int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
	  int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
	  float groupPercentage = (float) aggro.getDamage() / totalDamage;
	  int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
	  int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
	  int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());

	  for (Player member : players) {
		 int memberApGain = 1;
		 int memberXpGain = 1;
		 int memberDpGain = 1;
		 if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
			if (apRewardPerMember > 0)
			   memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
			if (xpRewardPerMember > 0)
			   memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
			if (dpRewardPerMember > 0)
			   memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());

		 }
		 AbyssPointsService.addAGp(member, victim, memberApGain, 0);
		 member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
		 member.getCommonData().addDp(memberDpGain);
		 this.addKillFor(member.getObjectId(), victim.getObjectId());
	  }

	  return true;
   }

   /**
    * @param victim
    * @param totalDamage
    * @param aggro
    * @return true if group is not same race
    */
   private boolean rewardPlayerAlliance(Player victim, int totalDamage, AggroInfo aggro) {
	  // Reward Alliance
	  PlayerAlliance alliance = ((PlayerAlliance) aggro.getAttacker());

	  // Don't Reward Player of Same Faction.
	  if (alliance.getLeaderObject().getRace() == victim.getRace())
		 return false;

	  // Find group members in range
	  List<Player> players = new ArrayList<Player>();

	  // Find highest rank and level in local group
	  int maxRank = AbyssRankEnum.GRADE9_SOLDIER.getId();
	  int maxLevel = 0;

	  for (Player member : alliance.getMembers()) {
		 if (!member.isOnline())
			continue;
		 if (MathUtil.isIn3dRange(member, victim, GroupConfig.GROUP_MAX_DISTANCE)) {
			// Don't distribute AP to a dead player!
			if (!member.getLifeStats().isAlreadyDead()) {
			   players.add(member);
			   if (member.getLevel() > maxLevel)
				  maxLevel = member.getLevel();
			   if (member.getAbyssRank().getRank().getId() > maxRank)
				  maxRank = member.getAbyssRank().getRank().getId();
			}
		 }
	  }

	  // They are all dead or out of range.
	  if (players.isEmpty())
		 return false;

	  int baseApReward = StatFunctions.calculatePvpApGained(victim, maxRank, maxLevel);
	  int baseXpReward = StatFunctions.calculatePvpXpGained(victim, maxRank, maxLevel);
	  int baseDpReward = StatFunctions.calculatePvpDpGained(victim, maxRank, maxLevel);
	  float groupPercentage = (float) aggro.getDamage() / totalDamage;
	  int apRewardPerMember = Math.round(baseApReward * groupPercentage / players.size());
	  int xpRewardPerMember = Math.round(baseXpReward * groupPercentage / players.size());
	  int dpRewardPerMember = Math.round(baseDpReward * groupPercentage / players.size());

	  for (Player member : players) {
		 int memberApGain = 1;
		 int memberXpGain = 1;
		 int memberDpGain = 1;
		 if (this.getKillsFor(member.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
			if (apRewardPerMember > 0)
			   memberApGain = Math.round(RewardType.AP_PLAYER.calcReward(member, apRewardPerMember));
			if (xpRewardPerMember > 0)
			   memberXpGain = Math.round(xpRewardPerMember * member.getRates().getXpPlayerGainRate());
			if (dpRewardPerMember > 0)
			   memberDpGain = Math.round(StatFunctions.adjustPvpDpGained(dpRewardPerMember, victim.getLevel(), member.getLevel()) * member.getRates().getDpPlayerRate());
		 }
		 AbyssPointsService.addAGp(member, victim, memberApGain, 0);
		 member.getCommonData().addExp(memberXpGain, RewardType.PVP_KILL, victim.getName());
		 member.getCommonData().addDp(memberDpGain);

		 this.addKillFor(member.getObjectId(), victim.getObjectId());
	  }

	  return true;
   }

   /**
    * @param victim
    * @param totalDamage
    * @param aggro
    * @return true if player is not same race
    */
   private boolean rewardPlayer(Player victim, int totalDamage, AggroInfo aggro) {
	  // Reward Player
	  Player winner = ((Player) aggro.getAttacker());

	  // Don't Reward Player out of range/dead/same faction
	  if (winner.getRace() == victim.getRace() || !MathUtil.isIn3dRange(winner, victim, GroupConfig.GROUP_MAX_DISTANCE) || winner.getLifeStats().isAlreadyDead())
		 return false;

	  int baseApReward = 1;
	  int baseXpReward = 1;
	  int baseDpReward = 1;

	  if (this.getKillsFor(winner.getObjectId(), victim.getObjectId()) < CustomConfig.MAX_DAILY_PVP_KILLS) {
		 baseApReward = StatFunctions.calculatePvpApGained(victim, winner.getAbyssRank().getRank().getId(),
				 winner.getLevel());
		 baseXpReward = StatFunctions.calculatePvpXpGained(victim, winner.getAbyssRank().getRank().getId(),
				 winner.getLevel());
		 baseDpReward = StatFunctions.calculatePvpDpGained(victim, winner.getAbyssRank().getRank().getId(),
				 winner.getLevel());
	  }

	  int apPlayerReward = Math.round(baseApReward * aggro.getDamage() / totalDamage);
	  apPlayerReward = (int) RewardType.AP_PLAYER.calcReward(winner, apPlayerReward);
	  int xpPlayerReward = Math.round(baseXpReward * winner.getRates().getXpPlayerGainRate() * aggro.getDamage()
			  / totalDamage);
	  int dpPlayerReward = Math.round(baseDpReward * winner.getRates().getDpPlayerRate() * aggro.getDamage()
			  / totalDamage);

		/*if (winner.getWorldId() == 600100000 && winner.getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId() && victim.getAbyssRank().getRank().getId() >= AbyssRankEnum.STAR1_OFFICER.getId()) {
                    int gp = 1;
                    gp *= Rnd.get(5, 15);
			AbyssPointsService.addAGp(winner, victim, apPlayerReward, gp);
		}
		else {
			AbyssPointsService.addAGp(winner, victim, apPlayerReward, 0);
		}*/
			AbyssPointsService.addAGp(winner, victim, apPlayerReward, 0);
	  winner.getCommonData().addExp(xpPlayerReward, RewardType.PVP_KILL, victim.getName());
	  winner.getCommonData().addDp(dpPlayerReward);
	  this.addKillFor(winner.getObjectId(), victim.getObjectId());
	  return true;
   }

   private void notifyKillQuests(Player winner, Player victim) {
	  if (winner.getRace() == victim.getRace())
		 return;

	  List<Player> rewarded = new ArrayList<Player>();
	  int worldId = victim.getWorldId();

	  if (winner.isInGroup2()) {
		 rewarded.addAll(winner.getPlayerGroup2().getOnlineMembers());
	  }
	  else if (winner.isInAlliance2()) {
		 rewarded.addAll(winner.getPlayerAllianceGroup2().getOnlineMembers());
	  }
	  else
		 rewarded.add(winner);

	  for (Player p : rewarded) {
		 if (!MathUtil.isIn3dRange(p, victim, GroupConfig.GROUP_MAX_DISTANCE) || p.getLifeStats().isAlreadyDead())
			continue;

		 QuestEngine.getInstance().onKillInWorld(new QuestEnv(victim, p, 0, 0), worldId);
		 QuestEngine.getInstance().onKillRanked(new QuestEnv(victim, p, 0, 0), victim.getAbyssRank().getRank());
	  }
	  rewarded.clear();
   }

   @SuppressWarnings("synthetic-access")
   private static class SingletonHolder {

	  protected static final PvpService instance = new PvpService();
   }
}
