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

package org.typezero.gameserver.model.templates.world;

import javax.xml.bind.annotation.*;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherEntry")
public class WeatherEntry {

	public WeatherEntry() {
	}

	public WeatherEntry(int zoneId, int weatherCode) {
		this.weatherCode = weatherCode;
		this.zoneId = zoneId;
	}

	@XmlAttribute(name = "zone_id", required = true)
	private int zoneId;

	@XmlAttribute(name = "code", required = true)
	private int weatherCode;

	@XmlAttribute(name = "rank", required = true)
	private int rank;

	@XmlAttribute(name = "name")
	private String weatherName;

	@XmlAttribute(name = "before")
	private Boolean isBefore;

	@XmlAttribute(name = "after")
	private Boolean isAfter;

	public int getZoneId() {
		return zoneId;
	}

	public int getCode() {
		return weatherCode;
	}

	public int getRank() {
		return rank;
	}

	public Boolean isBefore() {
		if (isBefore == null)
			return false;
		return isBefore;
	}

	public Boolean isAfter() {
		if (isAfter == null)
			return false;
		return isAfter;
	}

	public String getWeatherName() {
		return weatherName;
	}

}
