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

package ai.instance.steelRake;

import ai.GeneralNpcAI2;
import com.aionemu.commons.network.util.ThreadPoolManager;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("tamer_anikiki")
public class TamerAnikikiAI2 extends GeneralNpcAI2 {

	private AtomicBoolean isStartedWalkEvent = new AtomicBoolean(false);

	@Override
	public boolean canThink() {
		return false;
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		super.handleCreatureMoved(creature);
		if (getNpcId() == 219040 && isInRange(creature, 10) && creature instanceof Player) {
			if (isStartedWalkEvent.compareAndSet(false, true)) {
				getSpawnTemplate().setWalkerId("3004600001");
				WalkManager.startWalking(this);
				getOwner().setState(1);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
				// Key Box
				spawn(700553, 611, 481, 936, (byte) 90);
				spawn(700553, 657, 482, 936, (byte) 60);
				spawn(700553, 626, 540, 936, (byte) 1);
				spawn(700553, 645, 534, 936, (byte) 75);
				PacketSendUtility.sendPacket((Player) creature, new SM_QUEST_ACTION(0, 180));
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1400262);
				NpcShoutsService.getInstance().sendMsg(getOwner(), 1400262, getObjectId(), 0, 0);
			}
		}
	}

	@Override
	protected void handleMoveArrived() {
		int point = getOwner().getMoveController().getCurrentPoint();
		super.handleMoveArrived();
		if (getNpcId() == 219040) {
			if (point == 8) {
				getOwner().setState(64);
				PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
			}
			if (point == 12) {
				getSpawnTemplate().setWalkerId(null);
				WalkManager.stopWalking(this);
				AI2Actions.deleteOwner(this);
			}
		}
	}

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		if (getNpcId() != 219040) {
			ThreadPoolManager.getInstance().schedule(new Runnable() {

				@Override
				public void run() {
					SkillEngine.getInstance().getSkill(getOwner(), 18189, 20, getOwner()).useNoAnimationSkill();
				}

			}, 5000);
		}
	}

	@Override
	public int modifyDamage(int damage) {
		if (getNpcId() == 219037)
			return damage;
		else
		return 1;
	}
}
