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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 * @see client_housing_object_place_tag.xml</tt>
 */
@XmlType(name = "LimitType")
@XmlEnum
public enum LimitType {

	// Limits are in the order of house type: a, b, c, d, s
	NONE(0, new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
	OWNER_POT(1, new int[] { 6, 4, 3, 8, 8 }, new int[] { 0, 0, 0, 4, 0 }),
	VISITOR_POT(2, new int[] { 7, 5, 2, 8, 9 }, new int[] { 0, 0, 0, 4, 0 }),
	STORAGE(3, new int[] { 6, 5, 4, 8, 7 }, new int[] { 0, 0, 0, 4, 0 }),
	POT(4, new int[] { 6, 5, 4, 3, 7 }, new int[] { 6, 5, 4, 1, 7 }),
	COOKING(5, new int[] { 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1, 1 }),
	PICTURE(6, new int[] { 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 0, 1 }),
	JUKEBOX(7, new int[] { 1, 1, 1, 1, 1 }, new int[] { 1, 1, 1, 0, 1 });

	int id;
	int[] personalLimits;
	int[] trialLimits;

	private LimitType(int id, int[] maxPersonalLimits, int[] maxTrialLimits) {
		this.id = id;
		this.personalLimits = maxPersonalLimits;
		this.trialLimits = maxTrialLimits;
	}

	public String value() {
		return name();
	}

	public int getId() {
		return id;
	}

	public int getObjectPlaceLimit(HouseType houseType) {
		return personalLimits[houseType.getLimitTypeIndex()];
	}

	public int getTrialObjectPlaceLimit(HouseType houseType) {
		return trialLimits[houseType.getLimitTypeIndex()];
	}

	public static LimitType fromValue(String value) {
		return valueOf(value);
	}

}
