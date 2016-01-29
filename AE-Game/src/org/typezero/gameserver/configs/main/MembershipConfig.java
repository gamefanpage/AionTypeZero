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

public class MembershipConfig {

	@Property(key = "gameserver.instances.title.requirement", defaultValue = "10")
	public static byte INSTANCES_TITLE_REQ;

	@Property(key = "gameserver.instances.race.requirement", defaultValue = "10")
	public static byte INSTANCES_RACE_REQ;

	@Property(key = "gameserver.instances.level.requirement", defaultValue = "10")
	public static byte INSTANCES_LEVEL_REQ;

	@Property(key = "gameserver.instances.group.requirement", defaultValue = "10")
	public static byte INSTANCES_GROUP_REQ;

	@Property(key = "gameserver.instances.quest.requirement", defaultValue = "10")
	public static byte INSTANCES_QUEST_REQ;

	@Property(key = "gameserver.instances.cooldown", defaultValue = "10")
	public static byte INSTANCES_COOLDOWN;

	@Property(key = "gameserver.store.wh.all", defaultValue = "10")
	public static byte STORE_WH_ALL;

	@Property(key = "gameserver.store.accountwh.all", defaultValue = "10")
	public static byte STORE_AWH_ALL;

	@Property(key = "gameserver.store.legionwh.all", defaultValue = "10")
	public static byte STORE_LWH_ALL;

	@Property(key = "gameserver.trade.all", defaultValue = "10")
	public static byte TRADE_ALL;

	@Property(key = "gameserver.disable.soulbind", defaultValue = "10")
	public static byte DISABLE_SOULBIND;

	@Property(key = "gameserver.remodel.all", defaultValue = "10")
	public static byte REMODEL_ALL;

	@Property(key = "gameserver.emotions.all", defaultValue = "10")
	public static byte EMOTIONS_ALL;

	@Property(key = "gameserver.quest.stigma.slot", defaultValue = "10")
	public static byte STIGMA_SLOT_QUEST;

	@Property(key = "gameserver.soulsickness.disable", defaultValue = "10")
	public static byte DISABLE_SOULSICKNESS;

	@Property(key = "gameserver.autolearn.skill", defaultValue = "10")
	public static byte SKILL_AUTOLEARN;

	@Property(key = "gameserver.autolearn.stigma", defaultValue = "10")
	public static byte STIGMA_AUTOLEARN;

	@Property(key = "gameserver.quest.limit.disable", defaultValue = "10")
	public static byte QUEST_LIMIT_DISABLED;

	@Property(key = "gameserver.titles.additional.enable", defaultValue = "10")
	public static byte TITLES_ADDITIONAL_ENABLE;

	@Property(key = "gameserver.character.additional.enable", defaultValue = "10")
	public static byte CHARACTER_ADDITIONAL_ENABLE;

	@Property(key = "gameserver.advanced.friendlist.enable", defaultValue = "10")
	public static byte ADVANCED_FRIENDLIST_ENABLE;

	@Property(key = "gameserver.character.additional.count", defaultValue = "8")
	public static byte CHARACTER_ADDITIONAL_COUNT;

	@Property(key = "gameserver.advanced.friendlist.size", defaultValue = "90")
	public static int ADVANCED_FRIENDLIST_SIZE;
}
