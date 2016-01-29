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

package org.typezero.gameserver.configs.main;

import com.aionemu.commons.configuration.Property;

public class CustomConfig {

	/**
	 * Enables challenge tasks
	 */
	@Property(key = "gameserver.challenge.tasks.enabled", defaultValue = "false")
	public static boolean CHALLENGE_TASKS_ENABLED;

    /**
     * Enables fatigue system
     */
    @Property(key = "gameserver.fatigue.system.enabled", defaultValue = "false")
    public static boolean FATIGUE_SYSTEM_ENABLED;

    /**
     * Security Service Buff
     */
    @Property(key = "gameserver.securitybuff.enable", defaultValue = "true")
    public static boolean SECURITY_BUFF_ENABLE;

	/**
	 * Show premium account details on login
	 */
	@Property(key = "gameserver.premium.notify", defaultValue = "false")
	public static boolean PREMIUM_NOTIFY;

	@Property(key = "gameserver.display.rate", defaultValue = "false")
	public static boolean DISPLAY_RATE;

	/**
	 * Enable announce when a player succes enchant item 15
	 */
	@Property(key = "gameserver.enchant.announce.enable", defaultValue = "true")
	public static boolean ENABLE_ENCHANT_ANNOUNCE;

	/**
	 * Enable speaking between factions
	 */
	@Property(key = "gameserver.chat.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_BETWEEN_FACTIONS;

	@Property(key = "gameserver.privatestore.factions.enable", defaultValue = "false")
	public static boolean SPEAKING_PRIVATESTORE_FACTIONS;

	/**
	 * Minimum level to use whisper
	 */
	@Property(key = "gameserver.chat.whisper.level", defaultValue = "10")
	public static int LEVEL_TO_WHISPER;

	/**
	 * Factions search mode
	 */
	@Property(key = "gameserver.search.factions.mode", defaultValue = "false")
	public static boolean FACTIONS_SEARCH_MODE;

	/**
	 * list gm when search players
	 */
	@Property(key = "gameserver.search.gm.list", defaultValue = "false")
	public static boolean SEARCH_GM_LIST;

	/**
	 * Minimum level to use search
	 */
	@Property(key = "gameserver.search.player.level", defaultValue = "10")
	public static int LEVEL_TO_SEARCH;

	/**
	 * Allow opposite factions to bind in enemy territories
	 */
	@Property(key = "gameserver.cross.faction.binding", defaultValue = "false")
	public static boolean ENABLE_CROSS_FACTION_BINDING;

	/**
	 * Enable second class change without quest
	 */
	@Property(key = "gameserver.simple.secondclass.enable", defaultValue = "false")
	public static boolean ENABLE_SIMPLE_2NDCLASS;

	/**
	 * Disable chain trigger rate (chain skill with 100% success)
	 */
	@Property(key = "gameserver.skill.chain.triggerrate", defaultValue = "true")
	public static boolean SKILL_CHAIN_TRIGGERRATE;

	/**
	 * Unstuck delay
	 */
	@Property(key = "gameserver.unstuck.delay", defaultValue = "3600")
	public static int UNSTUCK_DELAY;

	/**
	 * The price for using dye command
	 */
	@Property(key = "gameserver.admin.dye.price", defaultValue = "1000000")
	public static int DYE_PRICE;

	/**
	 * Base Fly Time
	 */
	@Property(key = "gameserver.base.flytime", defaultValue = "60")
	public static int BASE_FLYTIME;

	/**
	 * Disable prevention using old names with coupon & command
	 */
	@Property(key = "gameserver.oldnames.coupon.disable", defaultValue = "false")
	public static boolean OLD_NAMES_COUPON_DISABLED;
	@Property(key = "gameserver.oldnames.command.disable", defaultValue = "true")
	public static boolean OLD_NAMES_COMMAND_DISABLED;

	/**
	 * Friendlist size
	 */
	@Property(key = "gameserver.friendlist.size", defaultValue = "90")
	public static int FRIENDLIST_SIZE;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.questsize.limit", defaultValue = "40")
	public static int BASIC_QUEST_SIZE_LIMIT;

	/**
	 * Basic Quest limit size
	 */
	@Property(key = "gameserver.basic.cubesize.limit", defaultValue = "10")
	public static int BASIC_CUBE_SIZE_LIMIT;

	 /**
	 * Npc Cube Expands limit size
	 */
	@Property(key = "gameserver.npcexpands.limit", defaultValue = "5")
	public static int NPC_CUBE_EXPANDS_SIZE_LIMIT;

	/**
	 * Enable instances
	 */
	@Property(key = "gameserver.instances.enable", defaultValue = "true")
	public static boolean ENABLE_INSTANCES;

	/**
	 * Enable instances mob always aggro player ignore level
	 */
	@Property(key = "gameserver.instances.mob.aggro", defaultValue = "300080000,300090000,300060000")
	public static String INSTANCES_MOB_AGGRO;

	/**
	 * Enable instances cooldown filtring
	 */
	@Property(key = "gameserver.instances.cooldown.filter", defaultValue = "0")
	public static String INSTANCES_COOL_DOWN_FILTER;

	/**
	 * Instances formula
	 */
	@Property(key = "gameserver.instances.cooldown.rate", defaultValue = "1")
	public static int INSTANCES_RATE;

	/**
	 * Enable Kinah cap
	 */
	@Property(key = "gameserver.enable.kinah.cap", defaultValue = "false")
	public static boolean ENABLE_KINAH_CAP;

	/**
	 * Kinah cap value
	 */
	@Property(key = "gameserver.kinah.cap.value", defaultValue = "999999999")
	public static long KINAH_CAP_VALUE;

	/**
	 * Enable AP cap
	 */
	@Property(key = "gameserver.enable.ap.cap", defaultValue = "false")
	public static boolean ENABLE_AP_CAP;

	/**
	 * AP cap value
	 */
	@Property(key = "gameserver.ap.cap.value", defaultValue = "1000000")
	public static long AP_CAP_VALUE;

	/**
     * Enable Glory Point cap
     */
    @Property(key = "gameserver.enable.gp.cap", defaultValue = "true")
    public static boolean ENABLE_GP_CAP;

     /**
     * Glory Point cap value
     */
    @Property(key = "gameserver.gp.cap.value", defaultValue = "1000000")
    public static long GP_CAP_VALUE;

	/**
     * Enable no Glory Point in mentored group.
     */
    @Property(key = "gameserver.nogp.mentor.group", defaultValue = "false")
    public static boolean MENTOR_GROUP_GP;

	/**
	 * Enable EXP cap
	 */
	@Property(key = "gameserver.enable.exp.cap", defaultValue = "false")
	public static boolean ENABLE_EXP_CAP;

	/**
	 * EXP cap value
	 */
	@Property(key = "gameserver.exp.cap.value", defaultValue = "48000000")
	public static long EXP_CAP_VALUE;

	/**
	 * Enable no AP in mentored group.
	 */
	@Property(key = "gameserver.noap.mentor.group", defaultValue = "false")
	public static boolean MENTOR_GROUP_AP;

	/**
	 * .faction cfg
	 */
	@Property(key = "gameserver.faction.free", defaultValue = "true")
	public static boolean FACTION_FREE_USE;

	@Property(key = "gameserver.faction.prices", defaultValue = "10000")
	public static int FACTION_USE_PRICE;

	@Property(key = "gameserver.faction.cmdchannel", defaultValue = "true")
	public static boolean FACTION_CMD_CHANNEL;

	@Property(key = "gameserver.faction.chatchannels", defaultValue = "false")
	public static boolean FACTION_CHAT_CHANNEL;

	/**
	 * Time in milliseconds in which players are limited for killing one player
	 */
	@Property(key = "gameserver.pvp.dayduration", defaultValue = "86400000")
	public static long PVP_DAY_DURATION;

	/**
	 * Allowed Kills in configuered time for full AP. Move to separate config when more pvp options.
	 */
	@Property(key = "gameserver.pvp.maxkills", defaultValue = "5")
	public static int MAX_DAILY_PVP_KILLS;

	/**
	 * Add a reward to player for pvp kills
	 */
	@Property(key = "gameserver.kill.reward.enable", defaultValue = "false")
	public static boolean ENABLE_KILL_REWARD;

	/**
	 * Kills needed for item reward
	 */
	@Property(key = "gameserver.kills.needed1", defaultValue = "5")
	public static int KILLS_NEEDED1;
	@Property(key = "gameserver.kills.needed2", defaultValue = "10")
	public static int KILLS_NEEDED2;
	@Property(key = "gameserver.kills.needed3", defaultValue = "15")
	public static int KILLS_NEEDED3;

	/**
	 * Item Rewards
	 */
	@Property(key = "gameserver.item.reward1", defaultValue = "186000031")
	public static int REWARD1;
	@Property(key = "gameserver.item.reward2", defaultValue = "186000030")
	public static int REWARD2;
	@Property(key = "gameserver.item.reward3", defaultValue = "186000096")
	public static int REWARD3;

	/**
	 * Show dialog id and quest id
	 */
	@Property(key = "gameserver.dialog.showid", defaultValue = "true")
	public static boolean ENABLE_SHOW_DIALOGID;

	/**
	 * Enable one kisk restriction
	 */
	@Property(key = "gameserver.kisk.restriction.enable", defaultValue = "true")
	public static boolean ENABLE_KISK_RESTRICTION;

	@Property(key = "gameserver.dispute.enable", defaultValue = "true")
	public static boolean DISPUTE_ENABLED;
	@Property(key = "gameserver.dispute.random.chance", defaultValue = "50")
	public static int DISPUTE_RND_CHANCE;
	@Property(key = "gameserver.dispute.random.schedule", defaultValue = "0 0 2 ? * *")
	public static String DISPUTE_RND_SCHEDULE;
	@Property(key = "gameserver.dispute.fixed.schedule", defaultValue = "0 0 4 ? * *")
	public static String DISPUTE_FXD_SCHEDULE;

	@Property(key = "gameserver.rift.enable", defaultValue = "true")
	public static boolean RIFT_ENABLED;
	@Property(key = "gameserver.rift.duration", defaultValue = "1")
	public static int RIFT_DURATION;

	@Property(key = "gameserver.vortex.enable", defaultValue = "true")
	public static boolean VORTEX_ENABLED;
	@Property(key = "gameserver.vortex.brusthonin.schedule", defaultValue = "0 0 16 ? * SAT")
	public static String VORTEX_THEOBOMOS_SCHEDULE;
	@Property(key = "gameserver.vortex.theobomos.schedule", defaultValue = "0 0 16 ? * SUN")
	public static String VORTEX_BRUSTHONIN_SCHEDULE;
	@Property(key = "gameserver.vortex.duration", defaultValue = "1")
	public static int VORTEX_DURATION;

	@Property(key = "gameserver.serialkiller.enable", defaultValue = "true")
	public static boolean SERIALKILLER_ENABLED;
	@Property(key = "gameserver.serialkiller.handledworlds", defaultValue = "")
	public static String SERIALKILLER_WORLDS = "";
	@Property(key = "gameserver.serialkiller.kills.refresh", defaultValue = "5")
	public static int SERIALKILLER_REFRESH;
	@Property(key = "gameserver.serialkiller.kills.decrease", defaultValue = "1")
	public static int SERIALKILLER_DECREASE;
	@Property(key = "gameserver.serialkiller.level.diff", defaultValue = "10")
	public static int SERIALKILLER_LEVEL_DIFF;
	@Property(key = "gameserver.serialkiller.1st.rank.kills", defaultValue = "25")
	public static int KILLER_1ST_RANK_KILLS;
	@Property(key = "gameserver.serialkiller.2nd.rank.kills", defaultValue = "50")
	public static int KILLER_2ND_RANK_KILLS;

	@Property(key = "gameserver.reward.service.enable", defaultValue = "false")
	public static boolean ENABLE_REWARD_SERVICE;

	/**
	 * Limits Config
	 */
	@Property(key = "gameserver.limits.enable", defaultValue = "true")
	public static boolean LIMITS_ENABLED;

	@Property(key = "gameserver.sell.limit.kinah", defaultValue = "5300047")
	public static long SELL_LIMIT_KINAH;

	@Property(key = "gameserver.limits.update", defaultValue = "0 0 0 * * ?")
	public static String LIMITS_UPDATE;

	@Property(key = "gameserver.chat.text.length", defaultValue = "150")
	public static int MAX_CHAT_TEXT_LENGHT;

	@Property(key = "gameserver.abyssxform.afterlogout", defaultValue = "false")
	public static boolean ABYSSXFORM_LOGOUT;

	@Property(key = "gameserver.instance.duel.enable", defaultValue = "true")
	public static boolean INSTANCE_DUEL_ENABLE;

	@Property(key = "gameserver.ride.restriction.enable", defaultValue = "true")
	public static boolean ENABLE_RIDE_RESTRICTION;

	@Property(key = "gameserver.quest.questdatakills", defaultValue = "true")
	public static boolean QUESTDATA_MONSTER_KILLS;

	@Property(key = "gameserver.commands.admin.dot.enable", defaultValue = "false")
	public static boolean ENABLE_ADMIN_DOT_COMMANDS;
}
