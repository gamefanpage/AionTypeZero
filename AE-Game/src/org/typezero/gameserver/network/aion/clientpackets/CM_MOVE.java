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

import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.controllers.movement.MovementMask;
import org.typezero.gameserver.controllers.movement.PlayerMoveController;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.state.CreatureVisualState;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOVE;
import org.typezero.gameserver.services.antihack.AntiHackService;
import org.typezero.gameserver.taskmanager.tasks.TeamMoveUpdater;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * Packet about player movement.
 *
 * @author -Nemesiss-
 */
public class CM_MOVE extends AionClientPacket {

	private byte type;
	private byte heading;
	private float x, y, z, x2, y2, z2, vehicleX, vehicleY, vehicleZ, vectorX, vectorY, vectorZ;
	private byte glideFlag;
	private int unk1, unk2;

	public CM_MOVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		Player player = getConnection().getActivePlayer();

		if (player == null || !player.isSpawned()) {
			return;
		}

		x = readF();
		y = readF();
		z = readF();

		heading = (byte) readC();
		type = (byte) readC();

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				vectorX = readF();
				vectorY = readF();
				vectorZ = readF();
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
		final Player player = getConnection().getActivePlayer();
		// packet was not read correctly
		if (player.getLifeStats().isAlreadyDead())
			return;

		if (player.getEffectController().isUnderFear())
			return;

		PlayerMoveController m = player.getMoveController();
		m.movementMask = type;

		// Admin Teleportation
		if (player.getAdminTeleportation() && ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE)
				&& ((type & MovementMask.MOUSE) == MovementMask.MOUSE)) {
			m.setNewDirection(x2, y2, z2);
			World.getInstance().updatePosition(player, x2, y2, z2, heading);
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_MOVE(player));
		}

		float speed = player.getGameStats().getMovementSpeedFloat();
		if ((type & MovementMask.GLIDE) == MovementMask.GLIDE) {
			m.glideFlag = glideFlag;
			player.getFlyController().switchToGliding();
		}
		else
			player.getFlyController().onStopGliding(false);

		if (type == 0) {
			player.getController().onStopMove();
			player.getFlyController().onStopGliding(false);
		}
		else if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE) {
			if ((type & MovementMask.MOUSE) == 0) {
				speed = player.getGameStats().getMovementSpeedFloat();
				m.vectorX = vectorX;
				m.vectorY = vectorY;
				m.vectorZ = vectorZ;
			}
			player.getMoveController().setNewDirection(x2, y2, z2, heading);
			player.getController().onStartMove();
		}
		else {
			player.getController().onMove();
			if ((type & MovementMask.MOUSE) == 0) {
				speed = player.getGameStats().getMovementSpeedFloat();
				player.getMoveController().setNewDirection(x + m.vectorX * speed * 1.5f, y + m.vectorY * speed * 1.5f,
						z + m.vectorZ * speed * 1.5f, heading);
			}
		}

		if ((type & MovementMask.VEHICLE) == MovementMask.VEHICLE) {
			m.unk1 = unk1;
			m.unk2 = unk2;
			m.vehicleX = vehicleX;
			m.vehicleY = vehicleY;
			m.vehicleZ = vehicleZ;
		}

		if (!AntiHackService.canMove(player, x, y, z, speed, type))
			return;

		World.getInstance().updatePosition(player, x, y, z, heading);
		m.updateLastMove();

		if (player.isInGroup2() || player.isInAlliance2()) {
			TeamMoveUpdater.getInstance().startTask(player);
		}

		if ((type & MovementMask.STARTMOVE) == MovementMask.STARTMOVE || type == 0) {
			player.getKnownList().doOnAllPlayers(new Visitor<Player>() {
				@Override
				public void visit(Player observer) {
					if (observer.isOnline()) {
						if (SecurityConfig.INVIS && (!observer.canSee(player) || player.isInVisualState(CreatureVisualState.BLINKING))) {
							return;
						}

						PacketSendUtility.sendPacket(observer, new SM_MOVE(player));
					}
				}

			});
		}

		if ((type & MovementMask.FALL) == MovementMask.FALL) {
			m.updateFalling(z);
		}
		else {
			m.stopFalling(z);
		}

		if (type != 0 && player.isProtectionActive()) {
			player.getController().stopProtectionActiveTask();
		}
	}

	@Override
	public String toString() {
		return "CM_MOVE [type=" + type + ", heading=" + heading + ", x=" + x + ", y=" + y + ", z=" + z + ", x2=" + x2
				+ ", y2=" + y2 + ", z2=" + z2 + ", vehicleX=" + vehicleX + ", vehicleY=" + vehicleY + ", vehicleZ=" + vehicleZ
				+ ", vectorX=" + vectorX + ", vectorY=" + vectorY + ", vectorZ=" + vectorZ + ", glideFlag=" + glideFlag
				+ ", unk1=" + unk1 + ", unk2=" + unk2 + "]";
	}

}