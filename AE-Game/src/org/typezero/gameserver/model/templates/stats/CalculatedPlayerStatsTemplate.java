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

package org.typezero.gameserver.model.templates.stats;

import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.utils.stats.ClassStats;

/**
 * @author ATracer
 */
public class CalculatedPlayerStatsTemplate extends PlayerStatsTemplate {

	private PlayerClass playerClass;

	public CalculatedPlayerStatsTemplate(PlayerClass playerClass) {
		this.playerClass = playerClass;
	}

	@Override
	public int getAccuracy() {
		return ClassStats.getAccuracyFor(playerClass);
	}

	@Override
	public int getAgility() {
		return ClassStats.getAgilityFor(playerClass);
	}

	@Override
	public int getHealth() {
		return ClassStats.getHealthFor(playerClass);
	}

	@Override
	public int getKnowledge() {
		return ClassStats.getKnowledgeFor(playerClass);
	}

	@Override
	public int getPower() {
		return ClassStats.getPowerFor(playerClass);
	}

	@Override
	public int getWill() {
		return ClassStats.getWillFor(playerClass);
	}

	@Override
	public float getAttackSpeed() {
		return ClassStats.getAttackSpeedFor(playerClass) / 1000f;
	}

	@Override
	public int getBlock() {
		return ClassStats.getBlockFor(playerClass);
	}

	@Override
	public int getEvasion() {
		return ClassStats.getEvasionFor(playerClass);
	}

	@Override
	public float getFlySpeed() {
		// TODO Auto-generated method stub
		return ClassStats.getFlySpeedFor(playerClass);
	}

	@Override
	public int getMagicAccuracy() {
		return ClassStats.getMagicAccuracyFor(playerClass);
	}

	@Override
	public int getMainHandAccuracy() {
		return ClassStats.getMainHandAccuracyFor(playerClass);
	}

	@Override
	public int getMainHandAttack() {
		return ClassStats.getMainHandAttackFor(playerClass);
	}

	@Override
	public int getMainHandCritRate() {
		return ClassStats.getMainHandCritRateFor(playerClass);
	}

	@Override
	public int getMaxHp() {
		return ClassStats.getMaxHpFor(playerClass, 10); // level is hardcoded
	}

	@Override
	public int getMaxMp() {
		return 1000;
	}

	@Override
	public int getParry() {
		return ClassStats.getParryFor(playerClass);
	}

	@Override
	public float getRunSpeed() {
		return ClassStats.getSpeedFor(playerClass);
	}

	@Override
	public float getWalkSpeed() {
		return 1.5f;
	}

}
