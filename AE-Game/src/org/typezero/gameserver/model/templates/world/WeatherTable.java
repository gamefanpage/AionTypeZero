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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WeatherTable", propOrder = { "zoneData" })
public class WeatherTable {

	@XmlElement(name = "table", required = true)
	protected List<WeatherEntry> zoneData;

	@XmlAttribute(name = "weather_count", required = true)
	protected int weatherCount;

	@XmlAttribute(name = "zone_count", required = true)
	protected int zoneCount;

	@XmlAttribute(name = "id", required = true)
	protected int mapId;

	public List<WeatherEntry> getZoneData() {
		return zoneData;
	}

	public int getMapId() {
		return mapId;
	}

	public int getZoneCount() {
		return zoneCount;
	}

	public int getWeatherCount() {
		return weatherCount;
	}

	public WeatherEntry getWeatherAfter(WeatherEntry entry) {
		if (entry.getWeatherName() == null || entry.isAfter())
			return null;
		for (WeatherEntry we : getZoneData()) {
			if (we.getZoneId() != entry.getZoneId())
				continue;
			if (entry.getWeatherName().equals(we.getWeatherName())) {
				if (entry.isBefore() && !we.isBefore() && !we.isAfter())
					return we;
				else if (!entry.isBefore() && !entry.isAfter() && we.isAfter())
					return we;
			}
		}
		return null;
	}

	public List<WeatherEntry> getWeathersForZone(int zoneId) {
		List<WeatherEntry> result = new ArrayList<WeatherEntry>();
		for (WeatherEntry entry : getZoneData()) {
			if (entry.getZoneId() == zoneId)
				result.add(entry);
		}
		return result;
	}

}
