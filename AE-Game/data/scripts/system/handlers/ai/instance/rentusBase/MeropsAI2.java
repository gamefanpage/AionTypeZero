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

package ai.instance.rentusBase;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("merops")
public class MeropsAI2 extends GeneralNpcAI2 {

private AtomicBoolean startedEvent = new AtomicBoolean(false);

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			if (MathUtil.getDistance(getOwner(), player) <= 31) {
				if (startedEvent.compareAndSet(false, true)) {
					if (getNpcId() != 799674) {
						SkillEngine.getInstance().getSkill(getOwner(), 19358, 60, getOwner()).useNoAnimationSkill();
						SkillEngine.getInstance().getSkill(getOwner(), 19922, 60, getOwner()).useNoAnimationSkill();
					}
					startEvent();
				}
			}
		}
	}

	private void sendMsg(int msg, int delay) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, delay);
	}

	private void startEvent() {
		if (!isAlreadyDead()) {
			switch (getNpcId()) {
				case 799671:
					sendMsg(1500421, 0);
					sendMsg(1500422, 2500);
					sendMsg(1500423, 7000);
					break;
				case 799672:
					sendMsg(1500426, 0);
					sendMsg(1500427, 4500);
					sendMsg(1500428, 7000);
					break;
			}
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if ( !isAlreadyDead()) {
						if (getNpcId() != 799674) {
							SkillEngine.getInstance().getSkill(getOwner(), 19358, 60, getOwner()).useNoAnimationSkill();
						}
						if (getNpcId() == 799671) {
							spawn(282546, 751.380f, 625.360f, 157f, (byte) 10);
							spawn(282546, 748.950f, 645.180f, 157f, (byte) 114);
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									if (!isAlreadyDead()) {
										spawn(282465, 751.366f, 647.573f, 155.681f, (byte) 0);
										spawn(282465, 749.39f, 627.755f, 155.691f, (byte) 0);
										spawn(282543, 749.39f, 627.755f, 155.691f, (byte) 0);
										spawn(282543, 751.366f, 647.573f, 155.681f, (byte) 0);
									}
								}

							}, 1500);
						}
						else if (getNpcId() == 799672) {
							sendMsg(1500429, 1500);
							spawn(282547, 255.980f, 684.432f, 170f, (byte) 7);
							spawn(282547, 270.590f, 672.190f, 170f, (byte) 22);
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									if (!isAlreadyDead()) {
										spawn(282465, 256.308f, 684.923f, 168.356f, (byte) 0);
										spawn(282465, 270.427f, 672.150f, 169.167f, (byte) 0);
										spawn(282544, 256.308f, 684.923f, 168.356f, (byte) 0);
										spawn(282544, 270.427f, 672.150f, 169.167f, (byte) 0);
									}
								}

							}, 1500);
						}
						else if (getNpcId() == 799673) {
							spawn(282548, 160.981f, 310.663f, 252.031f, (byte) 85);
							ThreadPoolManager.getInstance().schedule(new Runnable() {

								@Override
								public void run() {
									if (!isAlreadyDead()) {
										spawn(282545, 160.907f, 309.474f, 252.202f, (byte) 0);
										spawn(282545, 162.361f, 312.186f, 252.032f, (byte) 0);
										spawn(217317, 160.489f, 308.799f, 252.032f, (byte) 0);
										spawn(282465, 160.907f, 309.474f, 252.202f, (byte) 0);
										spawn(282465, 162.361f, 312.186f, 252.032f, (byte) 0);
										spawn(282465, 160.489f, 308.799f, 252.032f, (byte) 85);
										sendMsg(1500432, 8000);
										getSpawnTemplate().setWalkerId("3002800001");
										setStateIfNot(AIState.WALKING);
										think();
										ThreadPoolManager.getInstance().schedule(new Runnable() {

											@Override
											public void run() {
												if (!isAlreadyDead()) {
													getOwner().setState(1);
													PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0,getObjectId()));
													ThreadPoolManager.getInstance().schedule(new Runnable() {

														@Override
														public void run() {
															if (!isAlreadyDead()) {
																getPosition().getWorldMapInstance().getDoors().get(70).setOpen(true);
																delete();
															}
														}

													}, 11000);

												}
											}

										}, 6000);
									}
								}

							}, 1500);
						}
						else {
							final Npc vasharti = getPosition().getWorldMapInstance().getNpc(218614);
							if (vasharti != null) {
								final int obj = vasharti.getObjectId();
								NpcShoutsService.getInstance().sendMsg(vasharti, 1500402, obj, 0, 0);
								NpcShoutsService.getInstance().sendMsg(vasharti, 1500403, obj, 0, 10000);
								NpcShoutsService.getInstance().sendMsg(vasharti, 1500404, obj, 0, 14000);
								sendMsg(1500435, 4000);
								sendMsg(1500436, 13000);
								ThreadPoolManager.getInstance().schedule(new Runnable() {

									@Override
									public void run() {
										if (!isAlreadyDead()) {
											if (vasharti != null && !vasharti.getLifeStats().isAlreadyDead()) {
												ThreadPoolManager.getInstance().schedule(new Runnable() {

													@Override
													public void run() {
														if (vasharti != null && !vasharti.getLifeStats().isAlreadyDead()) {
															sendMsg(1500437, 0);
															vasharti.getController().onDelete();
															ThreadPoolManager.getInstance().schedule(new Runnable() {

																@Override
																public void run() {
																	spawn(217313, 188.17f, 414.06f, 260.75488f, (byte) 86);
																}

															}, 4000);

														}
													}

												}, 2000);
												SkillEngine.getInstance().getSkill(vasharti, 19907, 60, getOwner()).useNoAnimationSkill();
											}
										}
									}

								}, 14000);
							}
						}
					}
				}

			}, 9000);
		}
	}

	private void delete() {
		AI2Actions.deleteOwner(this);
	}

}
