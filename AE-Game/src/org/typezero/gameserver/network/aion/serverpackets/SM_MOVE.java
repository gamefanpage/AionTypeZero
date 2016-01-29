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

package org.typezero.gameserver.network.aion.serverpackets;

import org.typezero.gameserver.controllers.movement.MoveController;
import org.typezero.gameserver.controllers.movement.MovementMask;
import org.typezero.gameserver.controllers.movement.PlayableMoveController;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * This packet is displaying movement of players etc.
 *
 * @author -Nemesiss-
 */
public class SM_MOVE extends AionServerPacket {

	/**
	 * Object that is moving.
	 */
	private Creature creature;

	public SM_MOVE(Creature creature) {
		this.creature = creature;
	}

	@Override
	protected void writeImpl(AionConnection client) {
		MoveController moveData = creature.getMoveController();
		writeD(creature.getObjectId());
		writeF(creature.getX());
		writeF(creature.getY());
		writeF(creature.getZ());
		writeC(creature.getHeading());

		writeC(moveData.getMovementMask());

		if (moveData instanceof PlayableMoveController) {
			PlayableMoveController<?> playermoveData = (PlayableMoveController<?>) moveData;
			if ((moveData.getMovementMask() & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
				if ((moveData.getMovementMask() & MovementMask.MOUSE) == 0) {
					writeF(playermoveData.vectorX);
					writeF(playermoveData.vectorY);
					writeF(playermoveData.vectorZ);
				}
				else {
					writeF(moveData.getTargetX2());
					writeF(moveData.getTargetY2());
					writeF(moveData.getTargetZ2());
				}
			}
			if ((moveData.getMovementMask() & MovementMask.GLIDE) == MovementMask.GLIDE) {
				writeC(playermoveData.glideFlag);
			}
			if ((moveData.getMovementMask() & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
				writeD(playermoveData.unk1);
				writeD(playermoveData.unk2);
				writeF(playermoveData.vectorX);
				writeF(playermoveData.vectorY);
				writeF(playermoveData.vectorZ);
			}
		}
		else {
			if ((moveData.getMovementMask() & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
				writeF(moveData.getTargetX2());
				writeF(moveData.getTargetY2());
				writeF(moveData.getTargetZ2());
			}
		}
	}

}
