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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.geometry.*;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.model.templates.zone.ZoneInfo;
import org.typezero.gameserver.model.templates.zone.ZoneTemplate;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "zones")
public class ZoneData {

	private static final Logger log = LoggerFactory.getLogger(ZoneData.class);

	@XmlElement(name = "zone")
	public List<ZoneTemplate> zoneList;

	@XmlTransient
	private TIntObjectHashMap<List<ZoneInfo>> zoneNameMap = new TIntObjectHashMap<List<ZoneInfo>>();

	@XmlTransient
	private HashMap<ZoneTemplate, Integer> weatherZoneIds = new HashMap<ZoneTemplate, Integer>();

	@XmlTransient
	private int count;

	protected void afterUnmarshal(Unmarshaller u, Object parent) {
		int lastMapId = 0;
		int weatherZoneId = 1;
		for (ZoneTemplate zone : zoneList) {
			Area area = null;
			switch (zone.getAreaType()) {
				case POLYGON:
					area = new PolyArea(zone.getName(), zone.getMapid(), zone.getPoints().getPoint(), zone.getPoints().getBottom(), zone.getPoints()
						.getTop());
					break;
				case CYLINDER:
					area = new CylinderArea(zone.getName(), zone.getMapid(), zone.getCylinder().getX(), zone.getCylinder().getY(), zone.getCylinder()
						.getR(), zone.getCylinder().getBottom(), zone.getCylinder().getTop());
					break;
				case SPHERE:
					area = new SphereArea(zone.getName(), zone.getMapid(), zone.getSphere().getX(), zone.getSphere().getY(), zone.getSphere().getZ(),
						zone.getSphere().getR());
					break;
				case SEMISPHERE:
					area = new SemisphereArea(zone.getName(), zone.getMapid(), zone.getSemisphere().getX(), zone.getSemisphere().getY(), zone.getSemisphere().getZ(),
						zone.getSemisphere().getR());
			}
			if (area != null) {
				List<ZoneInfo> zones = zoneNameMap.get(zone.getMapid());
				if (zones == null) {
					zones = new ArrayList<ZoneInfo>();
					zoneNameMap.put(zone.getMapid(), zones);
				}
				if (zone.getZoneType() == ZoneClassName.WEATHER) {
					if (lastMapId != zone.getMapid()) {
						lastMapId = zone.getMapid();
						weatherZoneId = 1;
					}
					weatherZoneIds.put(zone, weatherZoneId++);
				}
				zones.add(new ZoneInfo(area, zone));
				count++;
			}
		}
		zoneList.clear();
		zoneList = null;
	}

	public TIntObjectHashMap<List<ZoneInfo>> getZones() {
		return zoneNameMap;
	}

	public int size() {
		return count;
	}

	/**
	 * Weather zone ID it's an order number (starts from 1)
	 */
	public int getWeatherZoneId(ZoneTemplate template) {
		Integer id = weatherZoneIds.get(template);
		if (id == null)
			return 0;
		return id;
	}

	public void saveData() {
		Schema schema = null;
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			schema = sf.newSchema(new File("./data/static_data/zones/zones.xsd"));
		}
		catch (SAXException e1) {
			log.error("Error while saving data: " + e1.getMessage(), e1.getCause());
			return;
		}

		File xml = new File("./data/static_data/zones/generated_zones.xml");
		JAXBContext jc;
		Marshaller marshaller;
		try {
			jc = JAXBContext.newInstance(ZoneData.class);
			marshaller = jc.createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(this, xml);
		}
		catch (JAXBException e) {
			log.error("Error while saving data: " + e.getMessage(), e.getCause());
			return;
		}
	}
}
