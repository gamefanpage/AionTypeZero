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

package org.typezero.gameserver.model;

import org.typezero.gameserver.model.stats.container.StatEnum;

/**
 * @author xavier
 */
public enum SkillElement {
	NONE(0),
	FIRE(1),
	WATER(2),
	WIND(3),
	EARTH(4),
	LIGHT(5),
	DARK(6);

	private int element;

	private SkillElement(int id) {
		this.element = id;
	}

	public int getElementId() {
		return element;
	}

	public static StatEnum getResistanceForElement(SkillElement element) {
		switch (element) {
			case FIRE:
				return StatEnum.FIRE_RESISTANCE;
			case WATER:
				return StatEnum.WATER_RESISTANCE;
			case WIND:
				return StatEnum.WIND_RESISTANCE;
			case EARTH:
				return StatEnum.EARTH_RESISTANCE;
			case LIGHT:
				return StatEnum.ELEMENTAL_RESISTANCE_LIGHT;
			case DARK:
				return StatEnum.ELEMENTAL_RESISTANCE_DARK;
		default:
			break;
		}
		return null;

	}
}
