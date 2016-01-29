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

package org.typezero.gameserver.model.templates.walker;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author KKnD, Rolandas
 */
@XmlRootElement(name = "routestep")
@XmlAccessorType(XmlAccessType.FIELD)
public class RouteStep {

	@XmlAttribute(name = "rest_time", required = true)
	private Integer time = 0;

	@XmlAttribute(name = "z", required = true)
	private float locZ;

	@XmlAttribute(name = "y", required = true)
	private float locY;

	@XmlAttribute(name = "x", required = true)
	private float locX;

	@XmlAttribute(name = "step", required = true)
	private int routeStep;

	@XmlTransient
	private RouteStep nextStep;

	void beforeMarshal(Marshaller marshaller) {
		if (time == 0)
			time = null;
	}

	void afterMarshal(Marshaller marshaller) {
		if (time == null)
			time = 0;
	}

	public RouteStep() {
	}

	public RouteStep(float x, float y, float z, int restTime) {
		locX = x;
		locY = y;
		locZ = z;
		time = restTime;
	}

	public float getX() {
		return locX;
	}

	public float getY() {
		return locY;
	}

	public float getZ() {
		return locZ;
	}

	public void setZ(float z) {
		locZ = z;
	}

	public int getRestTime() {
		return time;
	}

	public RouteStep getNextStep() {
		return nextStep;
	}

	public void setNextStep(RouteStep nextStep) {
		this.nextStep = nextStep;
	}

	public int getRouteStep() {
		return routeStep;
	}

	public void setRouteStep(int routeStep) {
		this.routeStep = routeStep;
	}

}
