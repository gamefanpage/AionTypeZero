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

package org.typezero.gameserver.dataholders;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.stats.CalculatedPlayerStatsTemplate;
import org.typezero.gameserver.model.templates.stats.PlayerStatsTemplate;

/**
 * Created on: 31.07.2009 14:20:03
 *
 * @author Aquanox
 */
@XmlRootElement(name = "player_stats_templates")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayerStatsData {

	@XmlElement(name = "player_stats", required = true)
	private List<PlayerStatsType> templatesList = new ArrayList<PlayerStatsType>();

	private final TIntObjectHashMap<PlayerStatsTemplate> playerTemplates = new TIntObjectHashMap<PlayerStatsTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (PlayerStatsType pt : templatesList) {
			int code = makeHash(pt.getRequiredPlayerClass(), pt.getRequiredLevel());
			PlayerStatsTemplate template = pt.getTemplate();
			//TODO move to DP
			template.setMaxMp(Math.round(template.getMaxMp()*100f/template.getWill()));
			template.setMaxHp(Math.round(template.getMaxHp()*100f/template.getHealth()));
			int agility = template.getAgility();
			agility = (agility-100);
			template.setEvasion(Math.round(template.getEvasion() - template.getEvasion()*agility*0.003f));
			template.setBlock(Math.round(template.getBlock() - template.getBlock()*agility*0.0025f));
			template.setParry(Math.round(template.getParry() - template.getParry()*agility*0.0025f));
			playerTemplates.put(code, pt.getTemplate());
		}

		/** for unknown templates **/
		playerTemplates.put(makeHash(PlayerClass.WARRIOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.WARRIOR));
		playerTemplates.put(makeHash(PlayerClass.ASSASSIN, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ASSASSIN));
		playerTemplates.put(makeHash(PlayerClass.CHANTER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CHANTER));
		playerTemplates.put(makeHash(PlayerClass.CLERIC, 0), new CalculatedPlayerStatsTemplate(PlayerClass.CLERIC));
		playerTemplates.put(makeHash(PlayerClass.GLADIATOR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GLADIATOR));
		playerTemplates.put(makeHash(PlayerClass.MAGE, 0), new CalculatedPlayerStatsTemplate(PlayerClass.MAGE));
		playerTemplates.put(makeHash(PlayerClass.PRIEST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.PRIEST));
		playerTemplates.put(makeHash(PlayerClass.RANGER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RANGER));
		playerTemplates.put(makeHash(PlayerClass.SCOUT, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SCOUT));
		playerTemplates.put(makeHash(PlayerClass.SORCERER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.SORCERER));
		playerTemplates.put(makeHash(PlayerClass.SPIRIT_MASTER, 0), new CalculatedPlayerStatsTemplate(
			PlayerClass.SPIRIT_MASTER));
		playerTemplates.put(makeHash(PlayerClass.TEMPLAR, 0), new CalculatedPlayerStatsTemplate(PlayerClass.TEMPLAR));
		playerTemplates.put(makeHash(PlayerClass.ARTIST, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ARTIST));
		playerTemplates.put(makeHash(PlayerClass.ENGINEER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.ENGINEER));
		playerTemplates.put(makeHash(PlayerClass.BARD, 0), new CalculatedPlayerStatsTemplate(PlayerClass.BARD));
		playerTemplates.put(makeHash(PlayerClass.GUNNER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.GUNNER));
		playerTemplates.put(makeHash(PlayerClass.RIDER, 0), new CalculatedPlayerStatsTemplate(PlayerClass.RIDER));

		templatesList.clear();
		templatesList = null;
	}

	/**
	 * @param player
	 * @return
	 */
	public PlayerStatsTemplate getTemplate(Player player) {
		PlayerStatsTemplate template = getTemplate(player.getCommonData().getPlayerClass(), player.getLevel());
		if (template == null)
			template = getTemplate(player.getCommonData().getPlayerClass(), 0);
		return template;
	}

	/**
	 * @param playerClass
	 * @param level
	 * @return
	 */
	public PlayerStatsTemplate getTemplate(PlayerClass playerClass, int level) {
		PlayerStatsTemplate template = playerTemplates.get(makeHash(playerClass, level));
		if (template == null)
			template = getTemplate(playerClass, 0);
		return template;
	}

	/**
	 * Size of player templates
	 *
	 * @return
	 */
	public int size() {
		return playerTemplates.size();
	}

	@XmlRootElement(name = "playerStatsTemplateType")
	private static class PlayerStatsType {

		@XmlAttribute(name = "class", required = true)
		private PlayerClass requiredPlayerClass;
		@XmlAttribute(name = "level", required = true)
		private int requiredLevel;

		@XmlElement(name = "stats_template")
		private PlayerStatsTemplate template;

		public PlayerClass getRequiredPlayerClass() {
			return requiredPlayerClass;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public PlayerStatsTemplate getTemplate() {
			return template;
		}
	}

	/**
	 * @param playerClass
	 * @param level
	 * @return
	 */
	private static int makeHash(PlayerClass playerClass, int level) {
		return level << 8 | playerClass.ordinal();
	}
}
