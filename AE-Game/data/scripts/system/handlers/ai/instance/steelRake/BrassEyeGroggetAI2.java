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

package ai.instance.steelRake;

import ai.SummonerAI2;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.ai.Percentage;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author xTz
 */
@AIName("brasseyegrogget")
public class BrassEyeGroggetAI2 extends SummonerAI2 {

	// todo 4 towers in the room center and fix coordinates of monsters
	// need snif

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
	}

	@Override
	protected void handleIndividualSpawnedSummons(Percentage percent) {
		spawn(percent.getPercent());
	}

	private void spawn(int percent) {
		int i = 0;
		if (percent < 81 && percent > 60) {
			i = 1;
		}
		else if (percent < 61 && percent > 30) {
			i = 2;
		}
		else if (percent < 31) {
			i = 3;
		}
		final int nrSpawn = i;

		// to do move boss to initial position and set pause move and atack
		// after 9 sec first spawn
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {

				spawnHelpers1(nrSpawn);
			}
		}, 9000);
	}

	private void spawnHelpers1(final int nrSpawn) {
		switch (nrSpawn) {
			case 1:
				spawn(281184, 381.3756f, 495.24835f, 1072.1212f, (byte) 13);
				spawn(281181, 379.4199f, 495.36453f, 1072.1212f, (byte) 13);
				break;
			case 2:
				spawn(281184, 383.76724f, 527.02856f, 1072.1212f, (byte) 100);
				spawn(281181, 381.26767f, 526.40845f, 1072.1212f, (byte) 100);
				break;
			case 3:
				spawn(281182, 416.2482f, 500.6516f, 1071.8457f, (byte) 52);
				spawn(281182, 415.66647f, 519.5354f, 1071.8457f, (byte) 52);
				break;
		}

		// next spawn after 35 sec
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				spawnHelpers2(nrSpawn);
			}
		}, 35000);
	}

	private void spawnHelpers2(int nrSpawn) {
		switch (nrSpawn) {
			case 1:
				spawn(281183, 383.26193f, 528.38403f, 1072.1212f, (byte) 100);
				spawn(281184, 383.76724f, 527.02856f, 1072.1212f, (byte) 100);
				spawn(281181, 381.26767f, 526.40845f, 1072.1212f, (byte) 100);
				break;
			case 2:
				spawn(281182, 429.55338f, 525.7714f, 1075.3801f, (byte) 62);
				spawn(281182, 429.52865f, 492.56076f, 1075.3801f, (byte) 62);
				spawn(281181, 376.42566f, 502.19736f, 1072.1212f, (byte) 1);
				break;
			case 3:
				spawn(281187, 376.42566f, 502.19736f, 1072.1212f, (byte) 1);
				spawn(281181, 381.26767f, 526.40845f, 1072.1212f, (byte) 100);
				break;
		}

		// remove effect after 21 sec
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
				if (getEffectController().hasAbnormalEffect(18191)) {
					getEffectController().removeEffect(18191);
				}
				// to do move boss in the room center and remove pause
				// to do some skill boss use
			}
		}, 21000);
	}
}
