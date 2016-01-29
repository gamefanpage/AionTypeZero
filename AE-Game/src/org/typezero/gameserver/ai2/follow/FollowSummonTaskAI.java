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

package org.typezero.gameserver.ai2.follow;

import org.typezero.gameserver.ai2.event.AIEventType;
import org.typezero.gameserver.model.TaskId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.utils.MathUtil;

import java.util.concurrent.Future;

/**
 *
 * @author xTz
 */
public class FollowSummonTaskAI implements Runnable {

	private Creature target;
	private Summon summon;
	private Player master;
	private float targetX;
	private float targetY;
	private float targetZ;
	private Future<?> task;

	public FollowSummonTaskAI(Creature target, Summon summon) {
		this.target = target;
		this.summon = summon;
		this.master = summon.getMaster();
		task = summon.getMaster().getController().getTask(TaskId.SUMMON_FOLLOW);
		setLeadingCoordinates();
	}

	private void setLeadingCoordinates() {
		targetX = target.getX();
		targetY = target.getY();
		targetZ = target.getZ();
	}

	@Override
	public void run() {
		if (target == null || summon == null || master == null) {
			if (task != null) {
				task.cancel(true);
			}
			return;
		}
		if (!isInMasterRange()) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.DISTANCE);
			return;
		}
		if (!isInTargetRange()) {
			if (targetX != target.getX() || targetY != target.getY() || targetZ != target.getZ()) {
				setLeadingCoordinates();
				onOutOfTargetRange();
			}
		}
		else if (!master.equals(target)) {
			onDestination();
		}
	}

	private boolean isInTargetRange() {
		return MathUtil.isIn3dRange(target, summon, 2);
	}

	private boolean isInMasterRange() {
		return MathUtil.isIn3dRange(master, summon, 50);
	}

	protected void onDestination() {
		summon.getAi2().onCreatureEvent(AIEventType.ATTACK, target);
	}

	private void onOutOfTargetRange() {
		summon.getAi2().onGeneralEvent(AIEventType.MOVE_VALIDATE);
	}
}
