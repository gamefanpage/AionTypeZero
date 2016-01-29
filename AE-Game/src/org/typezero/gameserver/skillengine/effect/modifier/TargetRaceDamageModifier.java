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

package org.typezero.gameserver.skillengine.effect.modifier;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * modified by Sippolo, kecimis
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TargetRaceDamageModifier")
public class TargetRaceDamageModifier extends ActionModifier {

	@XmlAttribute(name = "race")
	private Race skillTargetRace;

	@Override
	public int analyze(Effect effect) {
		Creature effected = effect.getEffected();

		int newValue = (value + effect.getSkillLevel() * delta);
		if (effected instanceof Player) {

			Player player = (Player) effected;
			switch (skillTargetRace) {
				case ASMODIANS:
					if (player.getRace() == Race.ASMODIANS)
						return newValue;
					break;
				case ELYOS:
					if (player.getRace() == Race.ELYOS)
						return newValue;
			}
		}
		else if (effected instanceof Npc) {
			Npc npc = (Npc) effected;
			if (npc.getObjectTemplate().getRace().toString().equals(skillTargetRace.toString()))
				return newValue;
			else
				return 0;
		}

		return 0;
	}

	@Override
	public boolean check(Effect effect) {
		Creature effected = effect.getEffected();
		if (effected instanceof Player) {

			Player player = (Player) effected;
			Race race = player.getRace();
			return (race == Race.ASMODIANS && skillTargetRace == Race.ASMODIANS)
				|| (race == Race.ELYOS && skillTargetRace == Race.ELYOS);
		}
		else if (effected instanceof Npc) {
			Npc npc = (Npc) effected;

			Race race = npc.getObjectTemplate().getRace();
			if (race == null)
				return false;

			return race.toString().equals(skillTargetRace.toString());
		}

		return false;
	}

}
