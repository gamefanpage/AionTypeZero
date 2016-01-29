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


package org.typezero.gameserver.questEngine.handlers.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.templates.quest.QuestItems;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.template.WorkOrders;

/**
 * @author Mr. Poke, reworked Bobobear
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WorkOrdersData", propOrder = { "giveComponent" })
public class WorkOrdersData extends XMLQuest {

	@XmlElement(name = "give_component", required = true)
	protected List<QuestItems> giveComponent;
	@XmlAttribute(name = "start_npc_ids", required = true)
	protected List<Integer> startNpcIds;
	@XmlAttribute(name = "recipe_id", required = true)
	protected int recipeId;

	/**
	 * Gets the value of the giveComponent property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the giveComponent property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getGiveComponent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link QuestItems }
	 */
	public List<QuestItems> getGiveComponent() {
		if (giveComponent == null) {
			giveComponent = new ArrayList<QuestItems>();
		}
		return this.giveComponent;
	}

	/**
	 * Gets the value of the startNpcIds property.
	 */
	public List<Integer> getStartNpcIds() {
		return startNpcIds;
	}

	/**
	 * Gets the value of the recipeId property.
	 */
	public int getRecipeId() {
		return recipeId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.typezero.gameserver.questEngine.handlers.models.QuestScriptData#register()
	 */
	@Override
	public void register(QuestEngine questEngine) {
		questEngine.addQuestHandler(new WorkOrders(this));
	}
}
