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

package org.typezero.gameserver.services.toypet;

/**
 * @author Rolandas
 */
public final class PetFeedProgress {

	private int totalPoints = 0;
	private short regularConsumed = 0;
	private short lovedConsumed = 0;
	private PetHungryLevel hungryLevel = PetHungryLevel.HUNGRY;
	private short lovedFoodMax = 0;
	private boolean lovedFeeded = false;

	public PetFeedProgress(short lovedFoodLimit) {
		lovedFoodMax = (short) (lovedFoodLimit & 0x3F);
	}

	/**
	 * @return the totalPoints
	 */
	public int getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(int points) {
		totalPoints = points & 0x3FFF;
	}

	/**
	 * @return the hungryLevel
	 */
	public PetHungryLevel getHungryLevel() {
		return hungryLevel;
	}

	public void setHungryLevel(PetHungryLevel level) {
		hungryLevel = level;
	}

	/**
	 * @return the consumed
	 */
	public int getRegularCount() {
		return regularConsumed & 0xFF;
	}

	public void setRegularCount(short count) {
		regularConsumed = count;
	}

	public int getLovedFoodRemaining() {
		return lovedFoodMax - lovedConsumed;
	}

	public boolean isLovedFeeded() {
		return lovedFeeded;
	}

	public void setIsLovedFeeded() {
		lovedFeeded = true;
	}

	public void incrementCount(boolean lovedFood) {
		if (lovedFood) {
			lovedConsumed++;
		} else {
			regularConsumed++;
		}
	}

	public void reset() {
		if (lovedFeeded)
			lovedFeeded = false;
		else {
			totalPoints = 0;
			regularConsumed = 0;
		}
	}

	public int getDataForPacket() {
		int value = getRegularCount() & 0xFF;
		value <<= 14;
		value |= totalPoints >> 2;
		value <<= 6;
		value |= lovedConsumed & 0x3F;
		value <<= 4; // unk
		return value;
	}

	public void setData(int savedData) {
		savedData >>= 4; // drop unk
		lovedConsumed = (short) (savedData & 0x3F);
		savedData >>= 6;
		totalPoints = (savedData & 0x3FFF) << 2;
		savedData >>= 14;
		regularConsumed = (short) (savedData & 0xFF);
	}
}
