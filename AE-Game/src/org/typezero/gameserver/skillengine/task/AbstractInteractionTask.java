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

import java.util.concurrent.Future;

import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author ATracer
 */
public abstract class AbstractInteractionTask {

	private Future<?> task;
	private int interval = 2500;

	protected Player requestor;
	protected VisibleObject responder;

	/**
	 * @param requestor
	 * @param responder
	 */
	public AbstractInteractionTask(Player requestor, VisibleObject responder) {
		// super();
		this.requestor = requestor;
		if (responder == null)
			this.responder = requestor;
		else
			this.responder = responder;
	}

	/**
	 * Called on each interaction
	 *
	 * @return
	 */
	protected abstract boolean onInteraction();

	/**
	 * Called when interaction is complete
	 */
	protected abstract void onInteractionFinish();

	/**
	 * Called before interaction is started
	 */
	protected abstract void onInteractionStart();

	/**
	 * Called when interaction is not complete and need to be aborted
	 */
	protected abstract void onInteractionAbort();

	/**
	 * Interaction scheduling method
	 */
	public void start() {
		onInteractionStart();

		task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				if (!validateParticipants())
					stop(true);

				boolean stopTask = onInteraction();
				if (stopTask)
					stop(false);
			}

		}, 1000, interval);
	}

	/**
	 * Stop current interaction
	 */
	public void stop(boolean participantNull) {
		if (!participantNull)
			onInteractionFinish();

		if (task != null && !task.isCancelled()) {
			task.cancel(false);
			task = null;
		}
	}

	/**
	 * Abort current interaction
	 */
	public void abort() {
		onInteractionAbort();
		stop(false);
	}

	/**
	 * @return true or false
	 */
	public boolean isInProgress() {
		return task != null && !task.isCancelled();
	}

	/**
	 * @return true or false
	 */
	public boolean validateParticipants() {
		return requestor != null;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}
}
