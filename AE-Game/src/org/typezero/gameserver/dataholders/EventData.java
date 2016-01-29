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

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.templates.event.EventTemplate;

/**
 * <p>
 * Java class for EventData complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EventData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="active" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="event" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{}EventTemplate">
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventData", propOrder = { "active", "events" })
@XmlRootElement(name = "events_config")
public class EventData {

	@XmlElement(required = true)
	protected String active;

	@XmlElementWrapper(name = "events")
	@XmlElement(name = "event")
	protected List<EventTemplate> events;

	@XmlTransient
	private THashMap<String, EventTemplate> activeEvents = new THashMap<String, EventTemplate>();

	@XmlTransient
	private THashMap<String, EventTemplate> allEvents = new THashMap<String, EventTemplate>();

	@XmlTransient
	private int counter = 0;

	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (active == null || events == null)
			return;

		counter = 0;
		allEvents.clear();
		activeEvents.clear();

		Set<String> ae = new HashSet<String>();
		Collections.addAll(ae, active.split(";"));

		for (EventTemplate ev : events) {
			if (ae.contains(ev.getName()) && ev.isActive()) {
				activeEvents.put(ev.getName(), ev);
				counter++;
			}
			allEvents.put(ev.getName(), ev);
		}

		events.clear();
		events = null;
		active = null;
	}

	public int size() {
		return counter;
	}

	public String getActiveText() {
		return active;
	}

	public List<EventTemplate> getAllEvents() {
		List<EventTemplate> result = new ArrayList<EventTemplate>();
		synchronized (allEvents) {
			result.addAll(allEvents.values());
		}

		return result;
	}

	public void setAllEvents(List<EventTemplate> events, String active) {
		if (events == null)
			events = new ArrayList<EventTemplate>();
		this.events = events;
		this.active = active;

		for (EventTemplate et : this.events) {
			if (allEvents.containsKey(et.getName())) {
				EventTemplate oldEvent = allEvents.get(et.getName());
				if (oldEvent.isActive() && oldEvent.isStarted())
					et.setStarted();
			}
		}
		afterUnmarshal(null, null);
	}

	public List<EventTemplate> getActiveEvents() {
		List<EventTemplate> result = new ArrayList<EventTemplate>();
		synchronized (activeEvents) {
			result.addAll(activeEvents.values());
		}

		return result;
	}

	public boolean Contains(String eventName) {
		return activeEvents.containsKey(eventName);
	}

}
