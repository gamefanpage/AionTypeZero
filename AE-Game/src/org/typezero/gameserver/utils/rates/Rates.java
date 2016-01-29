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

package org.typezero.gameserver.utils.rates;

/**
 * @author ATracer
 */
public abstract class Rates {

	public abstract float getGroupXpRate();

	public abstract float getXpRate();

	public abstract float getApNpcRate();

	public abstract float getApPlayerGainRate();

	public abstract float getXpPlayerGainRate();
	
	public abstract float getApPlayerLossRate();

	public abstract float getGatheringXPRate();

	public abstract int getGatheringCountRate();

	public abstract float getCraftingXPRate();

	public abstract float getDropRate();

	public abstract float getQuestXpRate();

	public abstract float getQuestKinahRate();

	public abstract float getQuestApRate();

	public abstract float getDpNpcRate();

	public abstract float getDpPlayerRate();

	public abstract int getCraftCritRate();
	
	public abstract int getComboCritRate();

	public abstract float getDisciplineRewardRate();

	public abstract float getChaosRewardRate();

	public abstract float getHarmonyRewardRate();

	public abstract float getGloryRewardRate();

	public abstract float getSellLimitRate();
        
	public abstract float getKamarRewardRate();

	/**
	 * @param membership
	 * @return Rates
	 */
	public static Rates getRatesFor(byte membership) {
		switch (membership) {
			case 0:
				return new RegularRates();
			case 1:
				return new PremiumRates();
			case 2:
				return new VipRates();
			default:
				return new VipRates();
		}
	}
}