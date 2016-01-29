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

package org.typezero.gameserver.model.templates.flyring;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.utils3d.Point3D;

/**
 * @author M@xx
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FlyRing")
public class FlyRingTemplate {

	@XmlAttribute(name = "name")
	protected String name;

	@XmlAttribute(name = "map")
	protected int map;

	@XmlAttribute(name = "radius")
	protected float radius;

	@XmlElement(name = "center")
	protected FlyRingPoint center;

	@XmlElement(name = "p1")
	protected FlyRingPoint p1;

	@XmlElement(name = "p2")
	protected FlyRingPoint p2;

	public String getName() {
		return name;
	}

	public int getMap() {
		return map;
	}

	public float getRadius() {
		return radius;
	}

	public FlyRingPoint getCenter() {
		return center;
	}

	public FlyRingPoint getP1() {
		return p1;
	}

	public FlyRingPoint getP2() {
		return p2;
	}

	public FlyRingTemplate() {
	};

	public FlyRingTemplate(String name, int mapId, Point3D center, Point3D p1, Point3D p2, int radius) {
		this.name = name;
		this.map = mapId;
		this.radius = radius;
		this.center = new FlyRingPoint(center);
		this.p1 = new FlyRingPoint(p1);
		this.p2 = new FlyRingPoint(p2);
	}

}
