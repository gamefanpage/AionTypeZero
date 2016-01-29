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

package org.typezero.gameserver.taskmanager;

import java.util.concurrent.locks.ReentrantLock;

import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author NB4L1
 */
public abstract class FIFOExecutableQueue implements Runnable {

	private static final byte NONE = 0;
	private static final byte QUEUED = 1;
	private static final byte RUNNING = 2;

	private final ReentrantLock lock = new ReentrantLock();

	private volatile byte state = NONE;

	protected final void execute() {
		lock();
		try {
			if (state != NONE)
				return;

			state = QUEUED;
		}
		finally {
			unlock();
		}

		ThreadPoolManager.getInstance().execute(this);
	}

	public final void lock() {
		lock.lock();
	}

	public final void unlock() {
		lock.unlock();
	}

	public final void run() {
		try {
			while (!isEmpty()) {
				setState(QUEUED, RUNNING);

				try {
					while (!isEmpty())
						removeAndExecuteFirst();
				}
				finally {
					setState(RUNNING, QUEUED);
				}
			}
		}
		finally {
			setState(QUEUED, NONE);
		}
	}

	private void setState(byte expected, byte value) {
		lock();
		try {
			if (state != expected)
				throw new IllegalStateException("state: " + state + ", expected: " + expected);
		}
		finally {
			state = value;

			unlock();
		}
	}

	protected abstract boolean isEmpty();

	protected abstract void removeAndExecuteFirst();
}
