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

package org.typezero.gameserver.model.templates.curingzones;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CuringTemplate")
public class CuringTemplate {

	@XmlAttribute(name = "map_id")
	protected int mapId;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "range")
	protected float range;

	/**
	 * Gets the value of the mapId property.
	 *
	 * @return possible object is
	 *     {@link Integer }
	 *
	 */
	public int getMapId() {
		return mapId;
	}

	/**
	 * Sets the value of the mapId property.
	 *
	 * @param value allowed object is
	 *     {@link Integer }
	 *
	 */
	public void setMapId(int value) {
		this.mapId = value;
	}

	/**
	 * Gets the value of the x property.
	 *
	 * @return possible object is
	 *     {@link Float }
	 *
	 */
	public float getX() {
		return x;
	}

	/**
	 * Sets the value of the x property.
	 *
	 * @param value allowed object is
	 *     {@link Float }
	 *
	 */
	public void setX(float value) {
		this.x = value;
	}

	/**
	 * Gets the value of the y property.
	 *
	 * @return possible object is
	 *     {@link Float }
	 *
	 */
	public float getY() {
		return y;
	}

	/**
	 * Sets the value of the y property.
	 *
	 * @param value allowed object is
	 *     {@link Float }
	 *
	 */
	public void setY(float value) {
		this.y = value;
	}

	/**
	 * Gets the value of the z property.
	 *
	 * @return possible object is
	 *     {@link Float }
	 *
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Sets the value of the z property.
	 *
	 * @param value allowed object is
	 *     {@link Float }
	 *
	 */
	public void setZ(float value) {
		this.z = value;
	}

	public float getRange() {
		return range;
	}
}
