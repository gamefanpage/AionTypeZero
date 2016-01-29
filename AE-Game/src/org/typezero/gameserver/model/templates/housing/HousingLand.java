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

package org.typezero.gameserver.model.templates.housing;

import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Land", propOrder = { "addresses", "buildings", "sale", "fee", "caps" })
public class HousingLand {

	@XmlElementWrapper(name = "addresses", required = true)
	@XmlElement(name ="address")
	protected List<HouseAddress> addresses;

	@XmlElementWrapper(name = "buildings", required = true)
	@XmlElement(name ="building")
	protected List<Building> buildings;

	@XmlElement(required = true)
	protected Sale sale;

	@XmlElement(required = true)
	protected long fee;

	@XmlElement(required = true)
	protected BuildingCapabilities caps;

	@XmlAttribute(name = "sign_nosale", required = true)
	protected int signNosale;

	@XmlAttribute(name = "sign_sale", required = true)
	protected int signSale;

	@XmlAttribute(name = "sign_waiting", required = true)
	protected int signWaiting;

	@XmlAttribute(name = "sign_home", required = true)
	protected int signHome;

	@XmlAttribute(name = "manager_npc", required = true)
	protected int managerNpc;

	@XmlAttribute(name = "teleport_npc", required = true)
	protected int teleportNpc;

	@XmlAttribute(required = true)
	protected int id;

	public List<HouseAddress> getAddresses() {
		return addresses;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public Building getDefaultBuilding() {
		for (Building building : buildings) {
			if (building.isDefault())
				return building;
		}
		return buildings.get(0); // fail
	}

	public Sale getSaleOptions() {
		return sale;
	}

	public long getMaintenanceFee() {
		return fee;
	}

	public BuildingCapabilities getCapabilities() {
		return caps;
	}

	public int getNosaleSignNpcId() {
		return signNosale;
	}

	public int getSaleSignNpcId() {
		return signSale;
	}

	public void setSignSale(int value) {
		this.signSale = value;
	}

	public int getWaitingSignNpcId() {
		return signWaiting;
	}

	public int getHomeSignNpcId() {
		return signHome;
	}

	public int getManagerNpcId() {
		return managerNpc;
	}

	public int getTeleportNpcId() {
		return teleportNpc;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id;
	}

}
