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

package org.typezero.gameserver.dataholders;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object of this class is containing info about experience that are required for each level that player can obtain.
 *
 * @author Luno
 */
@XmlRootElement(name = "player_experience_table")
@XmlAccessorType(XmlAccessType.NONE)
public class PlayerExperienceTable {

	/** Exp table */
	@XmlElement(name = "exp")
	private long[] experience;

	/**
	 * Returns the number of experience that player have at the beginning of given level.<br>
	 * For example at lv 1 it's 0
	 *
	 * @param level
	 * @return count of experience. If <tt>level</tt> parameter is higher than the max level that player can gain, then
	 *         IllegalArgumentException is thrown.
	 */
	public long getStartExpForLevel(int level) {
		if (level > experience.length)
			throw new IllegalArgumentException("The given level is higher than possible max");

		return level == 0 ? 0 : experience[level - 1];
	}

	public int getLevelForExp(long expValue) {
		int level = 0;
		for (int i = experience.length; i > 0; i--) {
			if (expValue >= experience[(i - 1)]) {
				level = i;
				break;
			}
		}
		if (getMaxLevel() <= level)
			return getMaxLevel()-1;
		return level;
	}

	/**
	 * Max possible level,that player can obtain.
	 *
	 * @return max level.
	 */
	public int getMaxLevel() {
		return experience == null ? 0 : experience.length;
	}
}
