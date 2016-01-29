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
 */
@XmlType(name = "PartType")
@XmlEnum
public enum PartType {
	ROOF(1, 1),
	OUTWALL(2, 2),
	FRAME(3, 3),
	DOOR(4, 4),
	GARDEN(5, 5),
	FENCE(6, 6),
	INWALL_ANY(8, 13),
	INFLOOR_ANY(14, 19),
	ADDON(27, 27);

	private int lineNrStart;
	private int lineNrEnd;

	private PartType(int packetLineStart, int packetLineEnd) {
		this.lineNrStart = packetLineStart;
		this.lineNrEnd = packetLineEnd;
	}

	public int getStartLineNr() {
		return lineNrStart;
	}

	public int getEndLineNr() {
		return lineNrEnd;
	}

	public static PartType getForLineNr(int lineNr) {
		for (PartType type : PartType.values()) {
			if (type.getStartLineNr() <= lineNr && type.getEndLineNr() >= lineNr)
				return type;
		}
		return null;
	}
}
