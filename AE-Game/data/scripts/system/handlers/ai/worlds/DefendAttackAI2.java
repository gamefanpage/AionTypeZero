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

package ai.worlds;

import org.typezero.gameserver.model.CreatureType;
import javolution.util.FastMap;
import ai.GeneralNpcAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.controllers.observer.AttackObserver;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_SETTINGS;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author zxl001
 */
@AIName("defendattack")
public class DefendAttackAI2 extends GeneralNpcAI2 {

	private FastMap<Integer, AttackObserver> observed = new FastMap<Integer, AttackObserver>().shared();
	private boolean isAttack = false;

	@Override
	protected void handleCreatureSee(final Creature creature) {
		super.handleCreatureSee(creature);
		if (isAttack) {
			return;
		}
		if (creature instanceof Player) {
			final Player player = (Player) creature;
			final AttackObserver observer = new AttackObserver() {

				@Override
				public void attacked() {
					if (!isAttack) {
						getOwner().setNpcType(CreatureType.ATTACKABLE.getId());
						sendAttackSetting(creature);
						isAttack = true;
					}
				}
			};
			if (!observed.containsKey(player.getObjectId())) {
				player.getObserveController().addObserver(observer);
				observed.put(player.getObjectId(), observer);
			}
		}
	}

	private void sendAttackSetting(final Creature creature) {
		for (Integer objId : observed.keySet()) {
			Player player = World.getInstance().findPlayer(objId);
			if (player != null) {
				PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getObjectId(), 0, 0, 0));
				PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getObjectId(), 2, 1, 0));
				getOwner().setTarget(creature);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(getOwner(), EmotionType.ATTACKMODE), true);
				getOwner().getAggroList().addHate(creature, 1);
			}
		}
	}

	@Override
	protected void handleCreatureNotSee(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			AttackObserver obj = observed.remove(player.getObjectId());
			if (obj != null)
				player.getObserveController().removeObserver(obj);
		}
	}

	@Override
	protected void handleAttack(Creature creature) {
		SkillEngine.getInstance().getSkill(getOwner(), 20672, 65, creature).useSkill();
		SkillEngine.getInstance().getSkill(getOwner(), 21263, 65, creature).useSkill();
		super.handleAttack(creature);
	}

	@Override
	protected void handleBackHome() {
		isAttack = false;
		getOwner().setState(1);
		getOwner().setNpcType(CreatureType.FRIEND.getId());
		for (Integer objId : observed.keySet()) {
			Player player = World.getInstance().findPlayer(objId);
			if (!getOwner().canSee(player))
				handleCreatureNotSee(player);
			PacketSendUtility.sendPacket(player, new SM_CUSTOM_SETTINGS(getOwner().getObjectId(), 0, 38, 0));
			PacketSendUtility.sendPacket(player, new SM_EMOTION(getOwner(), EmotionType.NEUTRALMODE));
		}
	}
}
