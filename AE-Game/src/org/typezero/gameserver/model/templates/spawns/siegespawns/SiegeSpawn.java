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

package org.typezero.gameserver.model.templates.spawns.siegespawns;

import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.model.templates.spawns.Spawn;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 *
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SiegeSpawn")
public class SiegeSpawn {

	@XmlElement(name = "siege_race")
	private List<SiegeRaceTemplate> siegeRaceTemplates;
	@XmlAttribute(name = "siege_id")
	private int siegeId;

	public int getSiegeId() {
		return siegeId;
	}

	public List<SiegeRaceTemplate> getSiegeRaceTemplates() {
		return siegeRaceTemplates;
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "SiegeRaceTemplate")
	public static class SiegeRaceTemplate {

		@XmlElement(name = "siege_mod")
		private List<SiegeModTemplate> SiegeModTemplates;
		@XmlAttribute(name = "race")
		private SiegeRace race;

		public SiegeRace getSiegeRace() {
			return race;
		}

		public List<SiegeModTemplate> getSiegeModTemplates() {
			return SiegeModTemplates;
		}

		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "SiegeModTemplate")
		public static class SiegeModTemplate {
			@XmlElement(name = "spawn")
			private List<Spawn> spawns;
			@XmlAttribute(name = "mod")
			private SiegeModType siegeMod;

			public List<Spawn> getSpawns() {
				return spawns;
			}

			public SiegeModType getSiegeModType() {
				return siegeMod;
			}
		}
	}
}
