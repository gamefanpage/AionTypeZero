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
import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.walker.WalkerTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author xTz
 */
@AIName("imprisoned_reian")
public class ImprisonedReianAI2 extends GeneralNpcAI2 {

private AtomicBoolean isSaved = new AtomicBoolean(false);
private AtomicBoolean isAsked = new AtomicBoolean(false);
private String walkerId;
private WalkerTemplate template;

	@Override
	protected void handleSpawned() {
		walkerId = getSpawnTemplate().getWalkerId();
		getSpawnTemplate().setWalkerId(null);
		if (walkerId != null) {
			template = DataManager.WALKER_DATA.getWalkerTemplate(walkerId);
		}
		super.handleSpawned();
	}

	@Override
	protected void handleMoveArrived() {
		int point = getOwner().getMoveController().getCurrentPoint();
		super.handleMoveArrived();
		if (template.getRouteSteps().size() - 4 == point) {
			getSpawnTemplate().setWalkerId(null);
			WalkManager.stopWalking(this);
			AI2Actions.deleteOwner(this);
		}
	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (walkerId != null) {
			if (creature instanceof Player) {
				final Player player = (Player) creature;
				if (MathUtil.getDistance(getOwner(), player) <= 21) {
					if (isAsked.compareAndSet(false, true)) {
						switch (Rnd.get(1, 10)) {
							case 1:
								sendMsg(390563);
								break;
							case 2:
								sendMsg(390567);
								break;
						}
					}
				}
				if (MathUtil.getDistance(getOwner(), player) <= 6) {
					if (isSaved.compareAndSet(false, true)) {
						getSpawnTemplate().setWalkerId(walkerId);
						WalkManager.startWalking(this);
						getOwner().setState(1);
						PacketSendUtility.broadcastPacket(getOwner(), new SM_EMOTION(getOwner(), EmotionType.START_EMOTE2, 0, getObjectId()));
						switch (Rnd.get(1, 10)) {
							case 1:
								sendMsg(342410);
								break;
							case 2:
								sendMsg(342411);
								break;
						}
					}
				}
			}
		}
	}

	private void sendMsg(int msg) {
		NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 0, 0);
	}
}
