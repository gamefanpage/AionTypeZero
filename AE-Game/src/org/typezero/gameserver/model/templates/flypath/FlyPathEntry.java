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

package org.typezero.gameserver.model.templates.flypath;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author KID
 */
@XmlRootElement(name = "flypath_location")
@XmlAccessorType(XmlAccessType.NONE)
public class FlyPathEntry {
	@XmlAttribute(name = "id", required = true)
	private short id;

	@XmlAttribute(name = "sx", required = true)
	private float startX;
	@XmlAttribute(name = "sy", required = true)
	private float startY;
	@XmlAttribute(name = "sz", required = true)
	private float startZ;
	@XmlAttribute(name = "sworld", required = true)
	private int sworld;

	@XmlAttribute(name = "ex", required = true)
	private float endX;
	@XmlAttribute(name = "ey", required = true)
	private float endY;
	@XmlAttribute(name = "ez", required = true)
	private float endZ;
	@XmlAttribute(name = "eworld", required = true)
	private int eworld;

	@XmlAttribute(name = "time", required = true)
	private float time;

	public short getId() {
		return id;
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getStartZ() {
		return startZ;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public float getEndZ() {
		return endZ;
	}

	public int getStartWorldId() {
		return sworld;
	}

	public int getEndWorldId() {
		return eworld;
	}

	public int getTimeInMs() {
		return (int) (time * 1000);
	}
}
