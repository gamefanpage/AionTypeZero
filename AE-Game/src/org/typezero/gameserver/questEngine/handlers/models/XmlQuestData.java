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

import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import org.typezero.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import org.typezero.gameserver.questEngine.handlers.template.XmlQuest;

/**
 * @author Mr. Poke
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XmlQuest", propOrder = { "onTalkEvent", "onKillEvent" })
public class XmlQuestData extends XMLQuest {

	@XmlElement(name = "on_talk_event")
	protected List<OnTalkEvent> onTalkEvent;
	@XmlElement(name = "on_kill_event")
	protected List<OnKillEvent> onKillEvent;
	@XmlAttribute(name = "start_npc_id")
	protected Integer startNpcId;
	@XmlAttribute(name = "end_npc_id")
	protected Integer endNpcId;

	/**
	 * Gets the value of the onTalkEvent property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the onTalkEvent property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getOnTalkEvent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link OnTalkEvent }
	 */
	public List<OnTalkEvent> getOnTalkEvent() {
		if (onTalkEvent == null) {
			onTalkEvent = new ArrayList<OnTalkEvent>();
		}
		return this.onTalkEvent;
	}

	/**
	 * Gets the value of the onKillEvent property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the onKillEvent property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getOnKillEvent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link OnKillEvent }
	 */
	public List<OnKillEvent> getOnKillEvent() {
		if (onKillEvent == null) {
			onKillEvent = new ArrayList<OnKillEvent>();
		}
		return this.onKillEvent;
	}

	/**
	 * Gets the value of the startNpcId property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getStartNpcId() {
		return startNpcId;
	}

	/**
	 * Gets the value of the endNpcId property.
	 *
	 * @return possible object is {@link Integer }
	 */
	public Integer getEndNpcId() {
		return endNpcId;
	}

	/*
	 * (non-Javadoc)
	 * @see org.typezero.gameserver.questEngine.handlers.models.QuestScriptData#register(org.typezero.gameserver.questEngine
	 * .QuestEngine)
	 */
	@Override
	public void register(QuestEngine questEngine) {
		questEngine.addQuestHandler(new XmlQuest(this));
	}
}
