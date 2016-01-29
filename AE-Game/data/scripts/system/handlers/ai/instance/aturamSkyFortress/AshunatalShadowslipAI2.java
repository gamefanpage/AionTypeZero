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

package ai.instance.aturamSkyFortress;

import ai.AggressiveNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("ashunatal_shadowslip")
public class AshunatalShadowslipAI2 extends AggressiveNpcAI2 {

	private AtomicBoolean isHome = new AtomicBoolean(true);
	private boolean isSummoned;
	private boolean canThink = true;

	@Override
	public boolean canThink() {
		return canThink;
	}

	@Override
	protected void handleAttack(Creature creature) {
		super.handleAttack(creature);
		if (isHome.compareAndSet(true, false)) {
			getPosition().getWorldMapInstance().getDoors().get(17).setOpen(true);
			getPosition().getWorldMapInstance().getDoors().get(2).setOpen(true);
		}
		checkPercentage(getLifeStats().getHpPercentage());
	}

	private void checkPercentage(int hpPercentage) {
		if (hpPercentage <= 50 && !isSummoned) {
			isSummoned = true;
			SkillEngine.getInstance().getSkill(getOwner(), 19428, 1, getOwner()).useNoAnimationSkill();
			doSchedule();
		}
	}

	@Override
	protected void handleBackHome() {
		isHome.set(true);
		isSummoned = false;
		super.handleBackHome();
		getPosition().getWorldMapInstance().getDoors().get(17).setOpen(true);
		getPosition().getWorldMapInstance().getDoors().get(2).setOpen(false);
		Npc npc = getPosition().getWorldMapInstance().getNpc(219186);
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	private void doSchedule() {
		if (!isAlreadyDead()) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					if (!isAlreadyDead()) {
						SkillEngine.getInstance().getSkill(getOwner(), 19417, 49, getOwner()).useNoAnimationSkill();
						ThreadPoolManager.getInstance().schedule(new Runnable() {

							@Override
							public void run() {
								if (!isAlreadyDead()) {
									WorldPosition p = getPosition();
									spawn(219186, p.getX(), p.getY(), p.getZ(), p.getHeading());
									canThink = false;
									getSpawnTemplate().setWalkerId("3002400001");
									setStateIfNot(AIState.WALKING);
									think();
									getOwner().setState(1);
									PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
									ThreadPoolManager.getInstance().schedule(new Runnable() {

										@Override
										public void run() {
											if (!isAlreadyDead()) {
												despawn();
											}
										}

									}, 4000);
								}
							}

						}, 3000);
					}
				}

			}, 2000);

		}
	}

	private void despawn() {
		AI2Actions.deleteOwner(this);
	}
}
