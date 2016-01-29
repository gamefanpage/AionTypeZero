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

package org.typezero.gameserver.model.templates.zone;

import org.typezero.gameserver.world.zone.ZoneName;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author ATracer
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "Zone")
public class ZoneTemplate {

	@XmlElement
	protected Points points;

	@XmlElement
	protected Cylinder cylinder;

	@XmlElement
	protected Sphere sphere;

	@XmlElement
	protected Semisphere semisphere;

	@XmlAttribute
	protected int flags = -1;

	@XmlAttribute
	protected int priority;

	@XmlTransient
	private String name;

	@XmlTransient
	private ZoneName zoneName;

	@XmlAttribute(name = "name")
	public String getXmlName() {
		return name;
	}

	protected void setXmlName(String name) {
		zoneName = ZoneName.createOrGet(name);
		this.name = zoneName.name();
	}

	@XmlAttribute
	protected int mapid;

	@XmlAttribute(name = "siege_id")
	protected List<Integer> siegeId;

	@XmlAttribute(name = "town_id")
	private int townId;

	@XmlAttribute(name = "area_type")
	protected AreaType areaType = AreaType.POLYGON;

	@XmlAttribute(name = "zone_type")
	protected ZoneClassName zoneType = ZoneClassName.SUB;

	/**
	 * Gets the value of the points property.
	 */
	public Points getPoints() {
		return points;
	}

	public Cylinder getCylinder() {
		return cylinder;
	}

	public Sphere getSphere() {
		return sphere;
	}

	public Semisphere getSemisphere() {
		return semisphere;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Gets the value of the name property.
	 */
	public ZoneName getName() {
		return zoneName;
	}

	/**
	 * Gets the value of the mapid property.
	 */
	public int getMapid() {
		return mapid;
	}

	/**
	 * @return the type
	 */
	public AreaType getAreaType() {
		return areaType;
	}

	/**
	 * @return the zoneType
	 */
	public ZoneClassName getZoneType() {
		return zoneType;
	}

	public List<Integer> getSiegeId() {
		return siegeId;
	}

	public int getFlags() {
		return flags;
	}

	public int getTownId() {
		return townId;
	}

}
