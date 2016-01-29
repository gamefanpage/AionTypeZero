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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.typezero.gameserver.spawnengine.WalkerGroupType;

/**
 * @author KKnD
 */
@XmlRootElement(name = "walker_template")
@XmlAccessorType(XmlAccessType.FIELD)
public class WalkerTemplate {

	@XmlAttribute(name = "reversed")
	private Boolean isReversed = false;

	@XmlAttribute(name = "pool", required = true)
	private int pool = 1;

	@XmlAttribute(name = "route_id", required = true)
	private String routeId;

	@XmlAttribute(name = "formation")
	private WalkerGroupType formation = WalkerGroupType.POINT;

	@XmlAttribute(name = "rows")
	private String rowValues;

	@XmlElement(name = "routestep")
	private List<RouteStep> routeStepList;

	@XmlTransient
	private int[] rows;

	public WalkerTemplate() {
	}

	public WalkerTemplate(String routeId) {
		this.routeId = routeId;
	}

	void beforeMarshal(Marshaller marshaller) {
		if (isReversed == false)
			isReversed = null;
		if (formation == WalkerGroupType.POINT)
			formation = null;
	}

	void afterMarshal(Marshaller marshaller) {
		if (isReversed == null)
			isReversed = false;
		if (formation == null)
			formation = WalkerGroupType.POINT;
	}

	/**
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (isReversed) {
			for (int i = routeStepList.size() - 2; i > 0; i--) {
				RouteStep step = routeStepList.get(i);
				routeStepList.add(new RouteStep(step.getX(), step.getY(), step.getZ(), step.getRestTime()));
			}
		}
		for (int i = 0; i < routeStepList.size() - 1; i++) {
			routeStepList.get(i).setNextStep(routeStepList.get(i + 1));
			routeStepList.get(i).setRouteStep(i + 1);
		}
		routeStepList.get(routeStepList.size() - 1).setRouteStep(routeStepList.size());
		routeStepList.get(routeStepList.size() - 1).setNextStep(routeStepList.get(0));

		if (pool == 2) {
			formation = WalkerGroupType.SQUARE;
			rows = new int[1];
			rows[0] = 2;
		}
		else if (formation == WalkerGroupType.SQUARE) {
			if (rowValues != null) {
				String[] values = rowValues.split(",");
				rows = new int[values.length];
				for (int i = 0; i < values.length; i++)
					rows[i] = Integer.parseInt(values[i]);
			}
			else {
				formation = WalkerGroupType.POINT;
			}
		}
		rowValues = null;
	}

	public List<RouteStep> getRouteSteps() {
		return routeStepList;
	}

	public RouteStep getRouteStep(int value) {
		return routeStepList.get(value - 1);
	}

	public String getRouteId() {
		return routeId;
	}

	public int getPool() {
		return pool;
	}

	public void setPool(int pool) {
		this.pool = pool;
	}

	public void setRouteSteps(ArrayList<RouteStep> newSteps) {
		routeStepList = newSteps;
	}

	public boolean isReversed() {
		return isReversed;
	}

	public void setIsReversed(boolean value) {
		isReversed = value;
	}

	public WalkerGroupType getType() {
		return formation;
	}

	/**
	 * @return the rows
	 */
	public int[] getRows() {
		return rows;
	}

}
