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
package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.configs.administration.AdminConfig;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SoulKeeper
 * @author_fix nerolory
 */
public class CM_EMOTION extends AionClientPacket {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(CM_EMOTION.class);
	/**
	 * Emotion number
	 */
	EmotionType emotionType;
	/**
	 * Emotion number
	 */
	int emotion;
	/**
	 * Coordinates of player
	 */
	float x;
	float y;
	float z;
	byte heading;

	int targetObjectId;

	/**
	 * Constructs new client packet instance.
	 *
	 * @param opcode
	 */
	public CM_EMOTION(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * Read data
	 */
	@Override
	protected void readImpl() {
		int et;
		et = readC();
		emotionType = EmotionType.getEmotionTypeById(et);

		switch (emotionType) {
			case SELECT_TARGET:// select target
			case JUMP: // jump
			case SIT: // resting
			case STAND: // end resting
			case LAND_FLYTELEPORT: // fly teleport land
			case FLY: // fly up
			case LAND: // land
			case DIE: // die
			case END_DUEL: // duel end
			case WALK: // walk on
			case RUN: // walk off
			case OPEN_DOOR: // open static doors
			case CLOSE_DOOR: // close static doors
			case POWERSHARD_ON: // powershard on
			case POWERSHARD_OFF: // powershard off
			case ATTACKMODE: // get equip weapon
			case ATTACKMODE2: // get equip weapon
			case NEUTRALMODE: // remove equip weapon
			case NEUTRALMODE2: // remove equip weapon
			case START_SPRINT:
			case END_SPRINT:
			case WINDSTREAM_STRAFE:
				break;
			case EMOTE:
				emotion = readH();
				targetObjectId = readD();
				break;
			case CHAIR_SIT: // sit on chair
			case CHAIR_UP: // stand on chair
				x = readF();
				y = readF();
				z = readF();
				heading = (byte) readC();
				break;
			default:
				log.error("Unknown emotion type? 0x" + Integer.toHexString(et/* !!!!! */).toUpperCase());
				break;
		}
	}

	/**
	 * Send emotion packet
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (player.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (player.getState() == CreatureState.PRIVATE_SHOP.getId() || player.isAttackMode()
			&& (emotionType == EmotionType.CHAIR_SIT || emotionType == EmotionType.JUMP)) {
                return;
            }

		if (player.getState() == CreatureState.LOOTING.getId() && emotionType == EmotionType.NEUTRALMODE) {
				return;
		}

		player.getController().cancelUseItem();
		if (emotionType != EmotionType.SELECT_TARGET) {
                player.getController().cancelCurrentSkill();
            }


		// check for stance
		if (player.getController().isUnderStance()) {
			if (emotionType == EmotionType.SIT || emotionType == EmotionType.JUMP ||
				emotionType == EmotionType.NEUTRALMODE	|| emotionType == EmotionType.NEUTRALMODE2 ||
				emotionType == EmotionType.ATTACKMODE	|| emotionType == EmotionType.ATTACKMODE2) {
                        player.getController().stopStance();
                    }
		}

		switch (emotionType) {
			case SELECT_TARGET:
				return;
			case SIT:
				if (player.isInState(CreatureState.PRIVATE_SHOP)){
					return;
				}
				player.setState(CreatureState.RESTING);
				break;
			case STAND:
				player.unsetState(CreatureState.RESTING);
				break;
			case CHAIR_SIT:
				if (!player.isInState(CreatureState.WEAPON_EQUIPPED)) {
                player.setState(CreatureState.CHAIR);
            }
				break;
			case CHAIR_UP:
				player.unsetState(CreatureState.CHAIR);
				break;
			case LAND_FLYTELEPORT:
				player.getController().onFlyTeleportEnd();
				break;
			case FLY:
				if (player.getAccessLevel() < AdminConfig.GM_FLIGHT_FREE) {
					if (!player.isInsideZoneType(ZoneType.FLY)) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_FLYING_FORBIDDEN_HERE);
						return;
					}
				}
				// If player is under NoFly Effect, show the retail message for it and return
				if (player.isUnderNoFly()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANT_FLY_NOW_DUE_TO_NOFLY);
					return;
				}
				player.getFlyController().startFly();
				break;
			case LAND:
				player.getFlyController().endFly(false);
				break;
			case ATTACKMODE2:
			case ATTACKMODE:
				player.setAttackMode(true);
				player.setState(CreatureState.WEAPON_EQUIPPED);
				break;
			case NEUTRALMODE2:
			case NEUTRALMODE:
				player.setAttackMode(false);
				player.unsetState(CreatureState.WEAPON_EQUIPPED);
				break;
			case WALK:
				// cannot toggle walk when you flying or gliding
				if (player.getFlyState() > 0) {
                return;
            }
				player.setState(CreatureState.WALKING);
				break;
			case RUN:
				player.unsetState(CreatureState.WALKING);
				break;
			case OPEN_DOOR:
			case CLOSE_DOOR:
				break;
			case POWERSHARD_ON:
				if (!player.getEquipment().isPowerShardEquipped()) {
					PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WEAPON_BOOST_NO_BOOSTER_EQUIPED);
					return;
				}
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WEAPON_BOOST_BOOST_MODE_STARTED);
				player.setState(CreatureState.POWERSHARD);
				break;
			case POWERSHARD_OFF:
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_WEAPON_BOOST_BOOST_MODE_ENDED);
				player.unsetState(CreatureState.POWERSHARD);
				break;
			case START_SPRINT:
				if (!player.isInPlayerMode(PlayerMode.RIDE) || player.getLifeStats().getCurrentFp() < player.ride.getStartFp()
						|| player.isInState(CreatureState.FLYING) || !player.ride.canSprint()) {
					return;
				}
				player.setSprintMode(true);
				player.getLifeStats().triggerFpReduceByCost(player.ride.getCostFp());
				break;
			case END_SPRINT:
				if (!player.isInPlayerMode(PlayerMode.RIDE) || !player.ride.canSprint()) {
					return;
				}
				player.setSprintMode(false);
				player.getLifeStats().triggerFpRestore();
				break;
		}

		if (player.getEmotions().canUse(emotion)) {
			PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, emotionType, emotion, x, y, z, heading, getTargetObjectId(player)), true);
		}
	}

	/**
	 * @param player
	 * @return
	 */
	private final int getTargetObjectId(Player player) {
		int target = player.getTarget() == null ? 0 : player.getTarget().getObjectId();
		return target != 0 ? target : this.targetObjectId;
	}
}
