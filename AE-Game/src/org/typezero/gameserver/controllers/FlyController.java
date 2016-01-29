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

package org.typezero.gameserver.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.skillengine.effect.AbnormalState;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;

/**
 * @author ATracer
 */
public class FlyController {

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(FlyController.class);

	private static final long FLY_REUSE_TIME = 10000;
	private Player player;

	private ActionObserver glideObserver = new ActionObserver(ObserverType.ABNORMALSETTED) {

		@Override
		public void abnormalsetted(AbnormalState state) {
			if ((state.getId() & AbnormalState.CANT_MOVE_STATE.getId()) > 0	&& !player.isInvulnerableWing()) {
				player.getFlyController().onStopGliding(true);
			}
		}
	};

	public FlyController(Player player) {
		this.player = player;
	}

	/**
	 *
	 */
	public void onStopGliding(boolean removeWings) {
		if (player.isInState(CreatureState.GLIDING)) {
			player.unsetState(CreatureState.GLIDING);

			if (player.isInState(CreatureState.FLYING)) {
				player.setFlyState(1);
			}
			else {
				player.setFlyState(0);
				player.getLifeStats().triggerFpRestore();
				if (removeWings)
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);
			}

			//remove observer
			player.getObserveController().removeObserver(glideObserver);

			player.getGameStats().updateStatsAndSpeedVisually();
		}
	}

	/**
	 * Ends flying 1) by CM_EMOTION (pageDown or fly button press) 2) from server side during teleportation (abyss gates
	 * should not break flying) 3) when FP is decreased to 0
	 */
	public void endFly(boolean forceEndFly) {
		// unset flying and gliding
		if (player.isInState(CreatureState.FLYING) || player.isInState(CreatureState.GLIDING)) {
			player.unsetState(CreatureState.FLYING);
			player.unsetState(CreatureState.GLIDING);
			player.unsetState(CreatureState.FLOATING_CORPSE);
			player.setFlyState(0);

			// this is probably needed to change back fly speed into speed.
			//TODO remove this and just send in update?
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);

			if(forceEndFly)
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.LAND, 0, 0), true);

			//remove observer
			player.getObserveController().removeObserver(glideObserver);

			player.getGameStats().updateStatsAndSpeedVisually();
			player.getLifeStats().triggerFpRestore();
		}
	}

	/**
	 * This method is called to start flying (called by CM_EMOTION when pageUp or pressed fly button)
	 */
	public void startFly() {
		if (player.getFlyReuseTime() > System.currentTimeMillis()){
			AuditLogger.info(player, "No Flight Cooldown Hack. Reuse time: "+((player.getFlyReuseTime()-System.currentTimeMillis())/1000));
			return;
		}
		player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
		player.setState(CreatureState.FLYING);
		if (player.isInPlayerMode(PlayerMode.RIDE)) {
			player.setState(CreatureState.FLOATING_CORPSE);
		}
		player.setFlyState(1);
		player.getLifeStats().triggerFpReduce();
		//TODO remove it?
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
		player.getGameStats().updateStatsAndSpeedVisually();
	}

	/**
	 * Switching to glide mode (called by CM_MOVE with VALIDATE_GLIDE movement type) 1) from standing state 2) from flying
	 * state If from stand to glide - start fp reduce + emotions/stats if from fly to glide - only emotions/stats
	 */
	public boolean switchToGliding() {
		if (!player.isInState(CreatureState.GLIDING) && player.canPerformMove()) {
			if (player.getFlyReuseTime() > System.currentTimeMillis()){
				return false;
			}
			player.setFlyReuseTime(System.currentTimeMillis() + FLY_REUSE_TIME);
			player.setState(CreatureState.GLIDING);
			if (player.getFlyState() == 0)
				player.getLifeStats().triggerFpReduce();
			player.setFlyState(2);

			//add glideObserver
			player.getObserveController().addObserver(this.glideObserver);

			player.getGameStats().updateStatsAndSpeedVisually();
		}
		return true;
	}
}
