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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javolution.util.FastMap;

import org.typezero.gameserver.model.templates.walker.WalkerTemplate;

/**
 * @author KKnD, Rolandas
 */
@XmlRootElement(name = "npc_walker")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerData {

	private static final Logger log = LoggerFactory.getLogger(WalkerData.class);

	@XmlElement(name = "walker_template")
	private List<WalkerTemplate> walkerlist;

	@XmlTransient
	private FastMap<String, WalkerTemplate> walkerlistData = new FastMap<String, WalkerTemplate>();

	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (WalkerTemplate route : walkerlist) {
			if (walkerlistData.containsKey(route.getRouteId())) {
				log.warn("Duplicate route ID: " + route.getRouteId());
				continue;
			}
			walkerlistData.put(route.getRouteId(), route);
		}
		walkerlist.clear();
		walkerlist = null;
	}

	public int size() {
		return walkerlistData.size();
	}

	public WalkerTemplate getWalkerTemplate(String routeId) {
		if (routeId == null)
			return null;
		return walkerlistData.get(routeId);
	}

	public void AddTemplate(WalkerTemplate newTemplate) {
		if (walkerlist == null)
			walkerlist = new ArrayList<WalkerTemplate>();
		walkerlist.add(newTemplate);
	}

	public void saveData(String routeId) {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			schema = sf.newSchema(new File("./data/static_data/npc_walker/npc_walker.xsd"));
		}
		catch (SAXException e1) {
			log.error("Error while saving data: " + e1.getMessage(), e1.getCause());
			return;
		}

		File xml = new File("./data/static_data/npc_walker/generated_npc_walker_" + routeId + ".xml");
		JAXBContext jc;
		Marshaller marshaller;
		try {
			jc = JAXBContext.newInstance(WalkerData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, xml);
		}
		catch (JAXBException e) {
			log.error("Error while saving data: " + e.getMessage(), e.getCause());
			return;
		}
		finally {
			if (walkerlist != null) {
				walkerlist.clear();
				walkerlist = null;
			}
		}
	}

	public Collection<WalkerTemplate> getTemplates() {
		return walkerlistData.values();
	}

}
