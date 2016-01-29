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

import org.typezero.gameserver.controllers.movement.MovementMask;
import org.typezero.gameserver.controllers.movement.SummonMoveController;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOVE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author ATracer
 */
public class CM_SUMMON_MOVE extends AionClientPacket {

	private byte type;
	private byte heading;
	private float x = 0f, y = 0f, z = 0f, x2 = 0f, y2 = 0f, z2 = 0f, vehicleX = 0f, vehicleY = 0f, vehicleZ = 0f,
		vectorX = 0f, vectorY = 0f, vectorZ = 0f;
	private byte glideFlag;
	private int unk1, unk2;

	public CM_SUMMON_MOVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();

		if (player == null || !player.isSpawned())
			return;

		readD();// object id

		x = readF();
		y = readF();
		z = readF();

		heading = (byte) readC();
		type = (byte) readC();

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				/* [xTz] in packet is missed this for type 0xC0
				vectorX = readF();
				vectorY = readF();
				vectorZ = readF();*/
				x2 = vectorX + x;
				y2 = vectorY + y;
				z2 = vectorZ + z;
			}
			else {
				x2 = readF();
				y2 = readF();
				z2 = readF();
			}
		}
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			glideFlag = (byte) readC();
		}
		if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			unk1 = readD();
			unk2 = readD();
			vehicleX = readF();
			vehicleY = readF();
			vehicleZ = readF();
		}
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		Summon summon = player.getSummon();
		if (summon == null)
			return;
		if (summon.getEffectController().isUnderFear())
			return;
		SummonMoveController m = summon.getMoveController();
		m.movementMask = type;

		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			m.glideFlag = glideFlag;
		}

		if (type == 0) {
			summon.getController().onStopMove();
		}
		else if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				m.vectorX = vectorX;
				m.vectorY = vectorY;
				m.vectorZ = vectorZ;
			}
			summon.getMoveController().setNewDirection(x2, y2, z2, heading);
			summon.getController().onStartMove();
		}
		else
			summon.getController().onMove();

		if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			m.unk1 = unk1;
			m.unk2 = unk2;
			m.vehicleX = vehicleX;
			m.vehicleY = vehicleY;
			m.vehicleZ = vehicleZ;
		}
		World.getInstance().updatePosition(summon, x, y, z, heading);

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE || type == 0)
			PacketSendUtility.broadcastPacket(summon, new SM_MOVE(summon));
	}
}
