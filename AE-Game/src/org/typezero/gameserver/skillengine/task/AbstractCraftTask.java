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

package org.typezero.gameserver.skillengine.task;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author ATracer, synchro2
 */
public abstract class AbstractCraftTask extends AbstractInteractionTask {

	protected int completeValue = 100;
	protected int currentSuccessValue;
	protected int currentFailureValue;
	protected int skillLvlDiff;

	/**
	 * @param requestor
	 * @param responder
	 * @param successValue
	 * @param failureValue
	 */
	public AbstractCraftTask(Player requestor, VisibleObject responder, int skillLvlDiff) {
		super(requestor, responder);
		this.skillLvlDiff = skillLvlDiff;
	}

	@Override
	protected boolean onInteraction() {
		if (currentSuccessValue == completeValue) {
			return onSuccessFinish();
		}
		if (currentFailureValue == completeValue) {
			onFailureFinish();
			return true;
		}

		analyzeInteraction();

		sendInteractionUpdate();
		return false;
	}

	/**
	 * Perform interaction calculation
	 */
	private void analyzeInteraction() {
		// TODO better random
		// if(Rnd.nextBoolean())
		int multi = Math.max(0, 33 - skillLvlDiff * 5);
		if (Rnd.get(100) > multi) {
			currentSuccessValue += Rnd.get(completeValue / (multi + 1) / 2, completeValue);
		}
		else {
			currentFailureValue += Rnd.get(completeValue / (multi + 1) / 2, completeValue);
		}

		if (currentSuccessValue >= completeValue) {
			currentSuccessValue = completeValue;
		}
		else if (currentFailureValue >= completeValue) {
			currentFailureValue = completeValue;
		}
	}

	protected abstract void sendInteractionUpdate();

	protected abstract boolean onSuccessFinish();

	protected abstract void onFailureFinish();
}
