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

package ai.worlds.tiamarantasEye;

import ai.AggressiveNpcAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.skillengine.SkillEngine;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Luzien
 * TODO: fight AI
 */
@AIName("berserker_sunayaka")
public class BerserkerSunayakaAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);

	@Override
	public void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if(isHome.compareAndSet(true, false)) {
			if(getOwner().getNpcId() == 219359)
				SkillEngine.getInstance().getSkill(getOwner(), 20651, 1, getOwner()).useNoAnimationSkill(); //ragetask
		}
	}

	@Override
	public void handleBackHome() {
		super.handleBackHome();
		isHome.set(true);
		if (getOwner().getNpcId() == 219359) {
			getEffectController().removeEffect(20651);
			getEffectController().removeEffect(8763);
		}
		//SkillEngine.getInstance().getSkill(getOwner(), 20652, 1, getOwner()).useNoAnimationSkill();
	}
}
