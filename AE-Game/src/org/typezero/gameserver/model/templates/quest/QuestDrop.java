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


package org.typezero.gameserver.model.templates.quest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * @author MrPoke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestDrop")
public class QuestDrop {

	@XmlAttribute(name = "npc_id")
	protected Integer npcId;
	@XmlAttribute(name = "item_id")
	protected Integer itemId;
	@XmlAttribute
	protected Integer chance;
	@XmlAttribute(name = "drop_each_member")
	protected int dropEachMember = 0;
	@XmlAttribute(name = "collecting_step")
	protected int collecting_step = 0;

	@XmlTransient
	protected Integer questId;

	/**
	 * Gets the value of the npcId property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getNpcId() {
		return npcId;
	}

	/**
	 * Gets the value of the itemId property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * Gets the value of the chance property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public int getChance() {
		if (chance == null)
			return 100;
		return chance;
	}

	public boolean isDropEachMemberGroup() {
		return dropEachMember == 1;
	}

	public boolean isDropEachMemberAlliance() {
		return dropEachMember == 2;
	}

	/**
	 * @return the questId
	 */
	public Integer getQuestId() {
		return questId;
	}

	public int getCollectingStep() {
		return collecting_step;
	}

	/**
	 * @param questId
	 *          the questId to set
	 */
	public void setQuestId(Integer questId) {
		this.questId = questId;
	}

}
