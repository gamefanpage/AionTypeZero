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

package ai.instance.darkPoeta;

import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Npc;

/**
 *
 * @author xTz
 */
@AIName("marbatacontroller")
public class MarbataControllerAI2 extends NpcAI2 {

	private Npc getBoss() {
		Npc npc = null;
		switch (getNpcId()) {
			case 700443:
			case 700444:
			case 700442:
				npc = getPosition().getWorldMapInstance().getNpc(214850);
				break;
			case 700446:
			case 700447:
			case 700445:
				npc = getPosition().getWorldMapInstance().getNpc(214851);
				break;
			case 700440:
			case 700441:
			case 700439:
				npc = getPosition().getWorldMapInstance().getNpc(214849);
				break;
		}
		return npc;
	}

	private void apllyEffect(boolean remove) {
		Npc boss = getBoss();
		if (boss != null && !boss.getLifeStats().isAlreadyDead()) {
			switch (getNpcId()) {
				case 700443:
				case 700446:
				case 700440:
					if (remove) {
						boss.getEffectController().removeEffect(18556);
					}
					else {
						boss.getController().useSkill(18556);
					}
					break;
				case 700444:
				case 700447:
				case 700441:
					// TO DO unk
					break;
				case 700442:
				case 700445:
				case 700439:
					if (remove) {
						boss.getEffectController().removeEffect(18110);
					}
					else {
						boss.getController().useSkill(18110);
					}
					break;
			}
		}
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		apllyEffect(true);
		AI2Actions.deleteOwner(this);
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		apllyEffect(false);
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				useSkill();
			}

		}, 2000);

	}

	private void useSkill() {
		if (isAlreadyDead()) {
			return;
		}
		AI2Actions.targetSelf(this);
		int skill = 0;
		switch (getNpcId()) {
			case 700443:
			case 700446:
			case 700440:
				skill = 18554;
				break;
			case 700444:
			case 700447:
			case 700441:
				skill = 18555;
				break;
			case 700442:
			case 700445:
			case 700439:
				skill = 18553;
				break;
		}
		AI2Actions.useSkill(this, skill);
	}

	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
			case SHOULD_DECAY:
				return AIAnswers.NEGATIVE;
			case SHOULD_RESPAWN:
				return AIAnswers.NEGATIVE;
			case SHOULD_REWARD:
				return AIAnswers.NEGATIVE;
			default:
				return null;
		}
	}
}
