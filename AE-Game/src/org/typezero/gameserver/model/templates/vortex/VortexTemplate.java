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

package org.typezero.gameserver.model.templates.vortex;

import org.typezero.gameserver.model.Race;

import javax.xml.bind.annotation.*;

/**
 * @author Source
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Vortex")
public class VortexTemplate {

	@XmlAttribute(name = "id")
	protected int id;
	@XmlAttribute(name = "defends_race")
	protected Race dRace;
	@XmlAttribute(name = "offence_race")
	protected Race oRace;
	@XmlElement(name = "home_point")
	protected HomePoint home;
	@XmlElement(name = "resurrection_point")
	protected ResurrectionPoint resurrection;
	@XmlElement(name = "start_point")
	protected StartPoint start;

	/**
	 * @return the location id
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the defenders race
	 */
	public Race getDefendersRace() {
		return this.dRace;
	}

	/**
	 * @return the invaders race
	 */
	public Race getInvadersRace() {
		return this.oRace;
	}

	public HomePoint getHomePoint() {
		return home;
	}

	public ResurrectionPoint getResurrectionPoint() {
		return resurrection;
	}

	public StartPoint getStartPoint() {
		return start;
	}

}
