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

package ai.instance.raksang;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.poll.AIAnswer;
import org.typezero.gameserver.ai2.poll.AIAnswers;
import org.typezero.gameserver.ai2.poll.AIQuestion;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("vasuki_lifespark")
public class VasukiLifesparkAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean startedEvent = new AtomicBoolean(false);
	private boolean think = false;

	@Override
	public boolean canThink() {
		return think;
	}

	@Override
	protected void handleSpawned() {
		if (getNpcId() == 217764) {
			think = true;
		}
		else {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), 19126, 46, getOwner()).useNoAnimationSkill();
					}
				}

			}, 3000);
		}
		super.handleSpawned();
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 30) {
				if (startedEvent.compareAndSet(false, true)) {
					final int level;
					final int shoutId;
					final int skill;
					switch (getNpcId()) {
						case 217760:
							skill = 19972;
							level = 45;
							shoutId = 1401107;
							break;
						case 217761:
							skill = 19972;
							level = 46;
							shoutId = 1401171;
							break;
						case 217763:
							skill = 20087;
							level = 46;
							shoutId = 0;
							break;
						default:
							skill = 20039;
							level = 46;
							shoutId = 1401110;
							break;
					}
					if (shoutId != 0) {
						sendMsg(shoutId);
					}
					SkillEngine.getInstance().getSkill(getOwner(), skill, level, getOwner()).useNoAnimationSkill();
					if (getNpcId() != 217764) {
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								if (!isAlreadyDead()) {
									if (getNpcId() == 217763) {
										getPosition().getWorldMapInstance().getDoors().get(219).setOpen(true);
									}
									SkillEngine.getInstance().getSkill(getOwner(), 19967, level, getOwner()).useNoAnimationSkill();
									ThreadPoolManager.getInstance().schedule(new Runnable() {

										@Override
										public void run() {
											if (!isAlreadyDead()) {
												AI2Actions.deleteOwner(VasukiLifesparkAI2.this);
											}
										}

									}, 3500);

								}
							}

						}, 3500);
					}
					else {
						SkillEngine.getInstance().getSkill(getOwner(), 19974, 46, getOwner()).useNoAnimationSkill();
					}
				}
			}
		}
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), false, 0, 0);
	}

	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_RESIST_ABNORMAL:
				return AIAnswers.POSITIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
	}

	@Override
	protected void handleDied() {
		if (getNpcId() == 217764) {
			sendMsg(1401111);
			Npc soul = getPosition().getWorldMapInstance().getNpc(217471);
			Npc sapping = getPosition().getWorldMapInstance().getNpc(217472);
			if (soul != null) {
				soul.getEffectController().removeEffect(19126);
			}
			if (sapping != null) {
				sapping.getEffectController().removeEffect(19126);
			}
			NpcShoutsService.getInstance().sendMsg(getOwner(), 1401140);
		}
		super.handleDied();
	}

}
