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

package org.typezero.gameserver.questEngine.model;

import java.util.Timer;
import java.util.TimerTask;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Hilgert
 */
public class QuestTimer {

	private Timer timer;

	private int Time = 0;

	@SuppressWarnings("unused")
	private int questId;

	private boolean isTicking = false;

	private Player player;

	/**
	 * @param questId
	 */
	public QuestTimer(int questId, int seconds, Player player) {
		this.questId = questId;
		this.Time = seconds * 1000;
		this.player = player;
	}

	/**
	 * @param seconds
	 * @param player
	 * @return
	 */
	public void Start() {
		PacketSendUtility.sendMessage(player, "Timer started");
		timer = new Timer();
		isTicking = true;
		// TODO Send Packet that timer start
		TimerTask task = new TimerTask() {

			public void run() {
				PacketSendUtility.sendMessage(player, "Timer is over");
				onEnd();
			}
		};

		timer.schedule(task, Time);
	}

	public void Stop() {
		timer.cancel();
		onEnd();
	}

	public void onEnd() {
		// TODO Send Packet that timer end
		isTicking = false;
	}

	/**
	 * @return true - if Timer started, and ticking.
	 * @return false - if Timer not started or stoped.
	 */
	public boolean isTicking() {
		return this.isTicking;
	}

	/**
	 * @return
	 */
	public int getTimeSeconds() {
		return (int) this.Time / 1000;
	}
}
