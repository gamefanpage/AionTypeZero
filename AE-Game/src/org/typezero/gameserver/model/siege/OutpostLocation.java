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
package org.typezero.gameserver.model.siege;

import org.typezero.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import org.typezero.gameserver.services.SiegeService;
import java.util.List;

/**
 * @author Source
 *
 * These bosses only appear when an faction conquer all balaurea fortress... If
 * Elyos conquer all fortress the Enraged Mastarius appear on Ancient City of
 * Marayas If Asmodians conquer all fortress the Enraged Veille appear on
 * Inggison Outpost He/She still active for 2 hours after that he/she disappear
 * and respawn again next day on the end of Siege (if the faction owns all
 * fortress)
 */
public class OutpostLocation extends SiegeLocation {

	public OutpostLocation() {
	}

	public OutpostLocation(SiegeLocationTemplate template) {
		super(template);
	}

	@Override
	public int getNextState() {
		return isVulnerable() ? STATE_INVULNERABLE : STATE_VULNERABLE;
	}

	/**
	 * @deprecated Should be configured from datapack
	 * @return Outpost Location Race
	 */
	@Deprecated
	public SiegeRace getLocationRace() {
		switch (getLocationId()) {
			case 3111:
				return SiegeRace.ASMODIANS;
			case 2111:
				return SiegeRace.ELYOS;
			default:
				throw new RuntimeException("Please move this to datapack");
		}
	}

	/**
	 * @return Fortresses that must be captured to own this outpost
	 */
	public List<Integer> getFortressDependency() {
		return template.getFortressDependency();
	}

	public boolean isSiegeAllowed() {
		return getLocationRace() == getRace();
	}

	public boolean isSilenteraAllowed() {
		return !isSiegeAllowed() && !getRace().equals(SiegeRace.BALAUR);
	}

	public boolean isRouteSpawned() {
		for (Integer fortressId : getFortressDependency()) {
			SiegeRace sr = SiegeService.getInstance().getFortresses().get(fortressId).getRace();
			if (sr == getLocationRace()) {
				return true;
			}
		}

		return false;
	}
}
