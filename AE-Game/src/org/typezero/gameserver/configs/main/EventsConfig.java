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

/**
 * @author Rolandas
 */
public class EventsConfig {

	/**
	 * Event Enabled
	 */
	@Property(key = "gameserver.event.enable", defaultValue = "false")
	public static boolean EVENT_ENABLED;

	@Property(key = "gameserver.event.level", defaultValue = "44")
	public static int EVENT_REWARD_LEVEL;
        
	@Property(key = "gameserver.event.enable2", defaultValue = "false")
	public static boolean EVENT_ENABLED2;

	@Property(key = "gameserver.event.level2", defaultValue = "45")
	public static int EVENT_REWARD_LEVEL2;
        
	@Property(key = "gameserver.event.enable3", defaultValue = "false")
	public static boolean EVENT_ENABLED3;

	@Property(key = "gameserver.event.level3", defaultValue = "64")
	public static int EVENT_REWARD_LEVEL3;
	
    /**
     * Enable the Server Event Decorations
     * 00 = no decoration
     * 01 = christmas
     * 02 = halloween
     * 04 = valentine
     */
    @Property(key = "gameserver.enable.decor", defaultValue = "0")
    public static int ENABLE_DECOR;

	/**
	 * Event Rewarding Membership
	 */
	@Property(key = "gameserver.event.membership", defaultValue = "0")
	public static int EVENT_REWARD_MEMBERSHIP;
	
	@Property(key = "gameserver.event.membership2", defaultValue = "2")
	public static int EVENT_REWARD_MEMBERSHIP2;

	@Property(key = "gameserver.event.membership.rate", defaultValue = "false")
	public static boolean EVENT_REWARD_MEMBERSHIP_RATE;

	/**
	 * Event Rewarding Period
	 */
	@Property(key = "gameserver.event.period", defaultValue = "60")
	public static int EVENT_PERIOD;

	@Property(key = "gameserver.event.period2", defaultValue = "120")
	public static int EVENT_PERIOD2;
	
	@Property(key = "gameserver.event.period3", defaultValue = "30")
	public static int EVENT_PERIOD3;

	/**
	 * Event Reward Values
	 */
	@Property(key = "gameserver.event.item.elyos", defaultValue = "141000001")
	public static int EVENT_ITEM_ELYOS;
        
	@Property(key = "gameserver.event.item.elyos2", defaultValue = "141000001")
	public static int EVENT_ITEM_ELYOS2;
        
	@Property(key = "gameserver.event.item.elyos3", defaultValue = "141000001")
	public static int EVENT_ITEM_ELYOS3;
	
	@Property(key = "gameserver.event.item.asmo", defaultValue = "141000001")
	public static int EVENT_ITEM_ASMO;
        
	@Property(key = "gameserver.event.item.asmo2", defaultValue = "141000001")
	public static int EVENT_ITEM_ASMO2;
        
	@Property(key = "gameserver.event.item.asmo3", defaultValue = "141000001")
	public static int EVENT_ITEM_ASMO3;

	@Property(key = "gameserver.events.givejuice", defaultValue = "160009017")
	public static int EVENT_GIVEJUICE;
	
	@Property(key = "gameserver.events.givecake", defaultValue = "160010073")
	public static int EVENT_GIVECAKE;

	@Property(key = "gameserver.event.count", defaultValue = "1")
	public static int EVENT_ITEM_COUNT;
        
	@Property(key = "gameserver.event.count2", defaultValue = "1")
	public static int EVENT_ITEM_COUNT2;
        
	@Property(key = "gameserver.event.count3", defaultValue = "1")
	public static int EVENT_ITEM_COUNT3;

	@Property(key = "gameserver.event.service.enable", defaultValue = "false")
	public static boolean ENABLE_EVENT_SERVICE;

	@Property(key = "gameserver.pig.time", defaultValue = "0 0 20 ? * SAT")
	public static String PIG_EVENT_SCHEDULE;

	@Property(key = "gameserver.abyss.event.time", defaultValue = "0 0 15 ? * SUN")
	public static String ABYSS_EVENT_SCHEDULE;

}
