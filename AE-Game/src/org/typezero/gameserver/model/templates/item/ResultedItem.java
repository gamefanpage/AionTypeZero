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

package org.typezero.gameserver.model.templates.item;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;

/**
 * @author antness
 */
@XmlType(name = "ResultedItem")
public class ResultedItem {

	@XmlAttribute(name = "id")
	public int itemId;
	@XmlAttribute(name = "count")
	public int count;
	@XmlAttribute(name = "rnd_min")
	public int rndMin;
	@XmlAttribute(name = "rnd_max")
	public int rndMax;
	@XmlAttribute(name = "race")
	public Race race = Race.PC_ALL;
	@XmlAttribute(name = "player_class")
	public PlayerClass playerClass = PlayerClass.ALL;

	public int getItemId() {
		return itemId;
	}

	public int getCount() {
		return count;
	}

	public int getRndMin() {
		return rndMin;
	}

	public int getRndMax() {
		return rndMax;
	}

	public final Race getRace() {
		return race;
	}

	public PlayerClass getPlayerClass() {
		return playerClass;
	}

	public final int getResultCount() {
		if (count == 0 && rndMin == 0 && rndMax == 0) {
			return 1;
		}
		else if (rndMin > 0 || rndMax > 0) {
			if (rndMax < rndMin) {
				LoggerFactory.getLogger(ResultedItem.class).warn("Wronte rnd result item definition {} {}", rndMin, rndMax);
				return 1;
			}
			else {
				return Rnd.get(rndMin, rndMax);
			}
		}
		else {
			return count;
		}
	}
}
