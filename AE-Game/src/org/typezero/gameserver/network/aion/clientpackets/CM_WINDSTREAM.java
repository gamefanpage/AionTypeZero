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

import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.PlayerMode;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureState;
import org.typezero.gameserver.model.templates.windstreams.WindstreamPath;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_WINDSTREAM;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CM_WINDSTREAM extends AionClientPacket {

	private final Logger log = LoggerFactory.getLogger(CM_WINDSTREAM.class);
	int teleportId;
	int distance;
	int state;

	public CM_WINDSTREAM(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		teleportId = readD();
		distance = readD();
		state = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		switch (state) {
			case 0:
			case 4:
			case 7:
			case 8:
				if (state == 0) {
					player.unsetPlayerMode(PlayerMode.RIDE);
				}
				else if (state == 7) { // start boost
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM_START_BOOST, 0, 0), true);
				}
				else if (state == 8) { // end boost
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM_END_BOOST, 0, 0), true);
				}
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM(state, 1));
				break;
			case 1:
				if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
					return;
				}
				if (player.isInState(CreatureState.GLIDING) || player.isInState(CreatureState.FLYING)) {
					player.setPlayerMode(PlayerMode.WINDSTREAM, new WindstreamPath(teleportId, distance));
					player.unsetState(CreatureState.ACTIVE);
					player.unsetState(CreatureState.GLIDING);
					player.setState(CreatureState.FLYING);
					PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.WINDSTREAM, teleportId, distance), true);
					player.getLifeStats().triggerFpRestore();
					QuestEngine.getInstance().onEnterWindStream(new QuestEnv(null, player, 0, 0), teleportId);
				}
				break;
			case 2:
			case 3:
				if (!player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
					return;
				}
				player.unsetState(CreatureState.FLYING);
				player.setState(CreatureState.ACTIVE);
				if (state == 2) {
					player.setState(CreatureState.GLIDING);
					player.getLifeStats().triggerFpReduce();
				}
				PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, state == 2 ? EmotionType.WINDSTREAM_END
						: EmotionType.WINDSTREAM_EXIT, 0, 0), true);
				player.getGameStats().updateStatsAndSpeedVisually();
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM(state, 1));
				player.unsetPlayerMode(PlayerMode.WINDSTREAM);
				break;
			default:
				log.error("Unknown Windstream state #" + state + " was found!");
		}
	}

}
