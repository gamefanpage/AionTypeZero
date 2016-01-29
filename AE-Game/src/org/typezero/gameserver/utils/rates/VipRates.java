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

import org.typezero.gameserver.configs.main.CraftConfig;
import org.typezero.gameserver.configs.main.RateConfig;

/**
 * @author ATracer
 */
public class VipRates extends Rates {

	@Override
	public float getXpRate() {
		return RateConfig.VIP_XP_RATE;
	}

	@Override
	public float getGroupXpRate() {
		return RateConfig.VIP_GROUPXP_RATE;
	}

	@Override
	public float getQuestXpRate() {
		return RateConfig.VIP_QUEST_XP_RATE;
	}

	@Override
	public float getGatheringXPRate() {
		return RateConfig.VIP_GATHERING_XP_RATE;
	}

	@Override
	public int getGatheringCountRate() {
		return RateConfig.VIP_GATHERING_COUNT_RATE;
	}

	@Override
	public float getCraftingXPRate() {
		return RateConfig.VIP_CRAFTING_XP_RATE;
	}

	@Override
	public float getDropRate() {
		return RateConfig.VIP_DROP_RATE;
	}

	@Override
	public float getQuestKinahRate() {
		return RateConfig.VIP_QUEST_KINAH_RATE;
	}

	@Override
	public float getQuestApRate() {
		return RateConfig.VIP_QUEST_AP_RATE;
	}

	@Override
	public float getApPlayerGainRate() {
		return RateConfig.VIP_AP_PLAYER_GAIN_RATE;
	}

	@Override
	public float getXpPlayerGainRate() {
		return RateConfig.VIP_XP_PLAYER_GAIN_RATE;
	}

	@Override
	public float getApPlayerLossRate() {
		return RateConfig.VIP_AP_PLAYER_LOSS_RATE;
	}

	@Override
	public float getApNpcRate() {
		return RateConfig.VIP_AP_NPC_RATE;
	}

	@Override
	public float getDpNpcRate() {
		return RateConfig.VIP_DP_NPC_RATE;
	}

	@Override
	public float getDpPlayerRate() {
		return RateConfig.VIP_DP_PLAYER_RATE;
	}

	@Override
	public int getCraftCritRate() {
		return CraftConfig.VIP_CRAFT_CRIT_RATE;
	}

	@Override
	public int getComboCritRate() {
		return CraftConfig.VIP_CRAFT_COMBO_RATE;
	}

	@Override
	public float getDisciplineRewardRate() {
		return RateConfig.VIP_PVP_ARENA_DISCIPLINE_REWARD_RATE;
	}

	@Override
	public float getChaosRewardRate() {
		return RateConfig.VIP_PVP_ARENA_CHAOS_REWARD_RATE;
	}

	@Override
	public float getHarmonyRewardRate() {
		return RateConfig.VIP_PVP_ARENA_HARMONY_REWARD_RATE;
	}

	@Override
	public float getGloryRewardRate() {
		return RateConfig.VIP_PVP_ARENA_GLORY_REWARD_RATE;
	}

	@Override
	public float getSellLimitRate() {
		return RateConfig.VIP_SELL_LIMIT_RATE;
	}

	@Override
	public float getKamarRewardRate() {
		return RateConfig.KAMAR_REWARD_RATE;
	}
}
