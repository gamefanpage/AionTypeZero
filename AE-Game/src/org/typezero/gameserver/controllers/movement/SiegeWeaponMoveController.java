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

package org.typezero.gameserver.controllers.movement;

import org.typezero.gameserver.ai2.AISubState;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOVE;
import org.typezero.gameserver.taskmanager.tasks.MoveTaskManager;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 *
 * @author xTz
 */
public class SiegeWeaponMoveController extends SummonMoveController {

	private float pointX;
	private float pointY;
	private float pointZ;
	private float offset = 0.1f;
	public static final float MOVE_CHECK_OFFSET = 0.1f;

	public SiegeWeaponMoveController(Summon owner) {
		super(owner);
	}

	/**
	 * @return if destination reached
	 */
	@Override
	public void moveToDestination() {
		if (!owner.canPerformMove() || (owner.getAi2().getSubState() == AISubState.CAST)) {
			if (started.compareAndSet(true, false)) {
				setAndSendStopMove(owner);
			}
			updateLastMove();
			return;
		}
		else if (started.compareAndSet(false, true)) {
			movementMask = -32;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}

		if (MathUtil.getDistance(owner.getTarget(), pointX, pointY, pointZ) > MOVE_CHECK_OFFSET) {
			pointX = owner.getTarget().getX();
			pointY = owner.getTarget().getY();
			pointZ = owner.getTarget().getZ();
		}
		moveToLocation(pointX, pointY, pointZ, offset);
		updateLastMove();
	}

	@Override
	public void moveToTargetObject() {
		updateLastMove();
		MoveTaskManager.getInstance().addCreature(owner);
	}

	/**
	 * @param targetX
	 * @param targetY
	 * @param targetZ
	 * @param offset
	 * @return
	 */
	protected void moveToLocation(float targetX, float targetY, float targetZ, float offset) {
		boolean directionChanged;
		float ownerX = owner.getX();
		float ownerY = owner.getY();
		float ownerZ = owner.getZ();

		directionChanged = targetX != targetDestX || targetY != targetDestY || targetZ != targetDestZ;

		if (directionChanged) {
			heading = (byte) (Math.toDegrees(Math.atan2(targetY - ownerY, targetX - ownerX)) / 3);
		}

		targetDestX = targetX;
		targetDestY = targetY;
		targetDestZ = targetZ;

		float currentSpeed = owner.getGameStats().getMovementSpeedFloat();
		float futureDistPassed = currentSpeed * (System.currentTimeMillis() - lastMoveUpdate) / 1000f;

		float dist = (float) MathUtil.getDistance(ownerX, ownerY, ownerZ, targetX, targetY, targetZ);

		if (dist == 0) {
			return;
		}

		if (futureDistPassed > dist) {
			futureDistPassed = dist;
		}

		float distFraction = futureDistPassed / dist;
		float newX = (targetDestX - ownerX) * distFraction + ownerX;
		float newY = (targetDestY - ownerY) * distFraction + ownerY;
		float newZ = (targetDestZ - ownerZ) * distFraction + ownerZ;
		World.getInstance().updatePosition(owner, newX, newY, newZ, heading, false);
		if (directionChanged) {
			movementMask = -32;
			PacketSendUtility.broadcastPacket(owner, new SM_MOVE(owner));
		}
	}
	
}
