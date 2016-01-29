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

package org.typezero.gameserver.utils;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;
import com.aionemu.commons.utils.internal.chmv8.ForkJoinPool;
import com.aionemu.commons.utils.internal.chmv8.ForkJoinPool.ForkJoinWorkerThreadFactory;
import com.aionemu.commons.utils.internal.chmv8.ForkJoinWorkerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rolandas
 */
public class WorkStealThreadFactory extends PriorityThreadFactory implements ForkJoinWorkerThreadFactory {

	public WorkStealThreadFactory(String namePrefix) {
		super(namePrefix, Thread.NORM_PRIORITY);
	}

	public void setDefaultPool(ForkJoinPool pool) {
		if (pool == null)
			pool = ForkJoinPool.commonPool();
		super.setDefaultPool(pool);
	}

	@Override
	public ForkJoinPool getDefaultPool() {
		return (ForkJoinPool) super.getDefaultPool();
	}

	@Override
	public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
		return new WorkStealThread(pool);
	}

	private static class WorkStealThread extends ForkJoinWorkerThread {

		private static final Logger log = LoggerFactory.getLogger(WorkStealThread.class);

		public WorkStealThread(ForkJoinPool pool) {
			super(pool);
		}

		@Override
		protected void onStart() {
			super.onStart();
		}

		@Override
		protected void onTermination(Throwable exception) {
			if (exception != null)
				log.error("Error - Thread: " + this.getName() + " terminated abnormaly: " + exception);
			super.onTermination(exception);
		}
	}
}
