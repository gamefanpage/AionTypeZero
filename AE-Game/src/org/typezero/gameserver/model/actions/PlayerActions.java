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

package org.typezero.gameserver.model.actions;

import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.player.InRoll;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.ride.RideInfo;
import org.typezero.gameserver.model.templates.windstreams.WindstreamPath;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
public class PlayerActions extends CreatureActions {

	public static boolean isInPlayerMode(Player player, PlayerMode mode) {
		switch(mode) {
			case RIDE:
				return player.ride != null;
			case IN_ROLL:
				return player.inRoll != null;
			case WINDSTREAM:
				return player.windstreamPath != null;
		}
		return false;
	}

	public static void setPlayerMode(Player player, PlayerMode mode, Object obj) {
		switch(mode) {
			case RIDE:
				player.ride = (RideInfo) obj;
				break;
			case IN_ROLL:
				player.inRoll = (InRoll) obj;
				break;
			case WINDSTREAM:
				player.windstreamPath = (WindstreamPath) obj;
				break;
		}
	}

	public static boolean unsetPlayerMode(Player player, PlayerMode mode) {
		switch (mode) {
			case RIDE:
				RideInfo ride = player.ride;
				if (ride == null) {
					return false;
				}
				//check for sprinting when forcefully dismounting player
				if (player.isInSprintMode()) {
					player.getLifeStats().triggerFpRestore();
					player.setSprintMode(false);
				}
				player.unsetState(CreatureState.RESTING);
				player.unsetState(CreatureState.FLOATING_CORPSE);
				player.setState(CreatureState.ACTIVE);
				player.ride = null;
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.START_EMOTE2, 0, 0), true);
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RIDE_END), true);

				player.getGameStats().updateStatsAndSpeedVisually();

				//remove rideObservers
				for (ActionObserver observer : player.getRideObservers()) {
					player.getObserveController().removeObserver(observer);
				}
				player.getRideObservers().clear();
				return true;
			case IN_ROLL:
				if (player.inRoll == null) {
					return false;
				}
				player.inRoll = null;
				return true;
			case WINDSTREAM:
				if (player.windstreamPath == null) {
					return false;
				}
				player.windstreamPath = null;
				return true;
			default:
				return false;
		}
	}

}
