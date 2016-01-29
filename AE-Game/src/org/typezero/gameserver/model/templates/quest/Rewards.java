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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rewards", propOrder = { "selectableRewardItem", "rewardItem" })
public class Rewards {

	@XmlElement(name = "selectable_reward_item")
	protected List<QuestItems> selectableRewardItem;
	@XmlElement(name = "reward_item")
	protected List<QuestItems> rewardItem;
	@XmlAttribute
	protected Integer gold;
	@XmlAttribute
	protected Integer exp;
	@XmlAttribute(name = "reward_abyss_point")
	protected Integer rewardAbyssPoint;
	@XmlAttribute
	protected Integer title;
	@XmlAttribute(name = "extend_inventory")
	protected Integer extendInventory;
	@XmlAttribute(name = "extend_stigma")
	protected Integer extendStigma;
	@XmlAttribute(name = "glory_point")
	protected Integer rewardGloryPoint;
	@XmlAttribute(name = "dp")
	protected Integer rewardDp;

	/**
	 * Gets the value of the selectableRewardItem property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the selectableRewardItem property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getSelectableRewardItem().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 */
	public List<QuestItems> getSelectableRewardItem() {
		if (selectableRewardItem == null) {
			selectableRewardItem = new ArrayList<QuestItems>();
		}
		return this.selectableRewardItem;
	}

	/**
	 * Gets the value of the rewardItem property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the rewardItem property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getRewardItem().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 */
	public List<QuestItems> getRewardItem() {
		if (rewardItem == null) {
			rewardItem = new ArrayList<QuestItems>();
		}
		return this.rewardItem;
	}

	/**
	 * Gets the value of the gold property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getGold() {
		return gold;
	}

	/**
	 * Gets the value of the exp property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getExp() {
		return exp;
	}

	/**
	 * Gets the value of the rewardAbyssPoint property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getRewardAbyssPoint() {
		return rewardAbyssPoint;
	}

	/**
	 * Gets the value of the rewardGloryPoint property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getRewardGloryPoint() {
		return rewardGloryPoint;
	}

	/**
	 * Gets the value of the rewardDp property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getRewardDp() {
		return rewardDp;
	}

	/**
	 * Gets the value of the title property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getTitle() {
		return title;
	}

	/**
	 * @return the extendInventory
	 */
	public Integer getExtendInventory() {
		return extendInventory;
	}

	/**
	 * @return the extendStigma
	 */
	public Integer getExtendStigma() {
		return extendStigma;
	}
}
