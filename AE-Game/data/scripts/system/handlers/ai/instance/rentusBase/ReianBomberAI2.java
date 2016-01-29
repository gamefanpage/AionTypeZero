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
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.AIState;
import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.WorldPosition;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("reian_bomber")
public class ReianBomberAI2 extends GeneralNpcAI2 {

	private AtomicBoolean hasArrivedBoss = new AtomicBoolean(false);
	private int position = 1;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		getSpawnTemplate().setWalkerId("30028000024");
		WalkManager.startWalking(this);
		getOwner().setState(1);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
	}

	@Override
	protected void handleMoveArrived() {
		int point = getOwner().getMoveController().getCurrentPoint();
		super.handleMoveArrived();
		if (hasArrivedBoss.get()) {
			startHelpEvent();
		}
		else if (point == 7 && hasArrivedBoss.compareAndSet(false, true)) {
			getSpawnTemplate().setWalkerId(null);
			WalkManager.stopWalking(this);
			startHelpEvent();
		}
	}

	private void startHelpEvent() {
		getMoveController().abortMove();
		setStateIfNot(AIState.IDLE);
		setSubStateIfNot(AISubState.NONE);
		SkillEngine.getInstance().getSkill(getOwner(), 19374, 60, getOwner()).useNoAnimationSkill();
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (!isAlreadyDead()) {
					setSubStateIfNot(AISubState.WALK_RANDOM);
					setStateIfNot(AIState.WALKING);
					switch(position) {
						case 1:
							help(359.763f, 585.597f, 145.525f);
							getMoveController().moveToPoint(346.47787f, 604.0337f, 145.8766f);
							position ++;
							break;
						case 2:
							help(346.086f, 597.062f, 146.119f);
							getMoveController().moveToPoint(370.93597f, 607.6427f, 145.41916f);
							position ++;
							break;
						case 3:
							help(362.143f, 604.723f, 146.125f);
							getMoveController().moveToPoint(361.7722f, 584.4937f, 145.63573f);
							position = 1;
							break;
					}
				}
			}
		}, 8000);

	}

	private void deleteNpc(Npc npc) {
		if (npc != null) {
			npc.getController().onDelete();
		}
	}

	private void help(float x, float y, float z) {
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		if (instance != null) {
			for (Npc npc : instance.getNpcs(282530)) {
				WorldPosition p = npc.getPosition();
				if (p.getX() == x && p.getY() == y) {
					deleteNpc(npc);
				}
			}
			for (Npc npc : instance.getNpcs(282387)) {
				WorldPosition p = npc.getPosition();
				if (p.getX() == x && p.getY() == y) {
					return;
				}
			}
			Npc npc = (Npc) spawn(282387, x, y, z, (byte) 0);
			SkillEngine.getInstance().getSkill(npc, 19731, 1, npc).useNoAnimationSkill();
		}
	}

}
