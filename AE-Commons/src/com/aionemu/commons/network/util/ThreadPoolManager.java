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

package com.aionemu.commons.network.util;

import com.aionemu.commons.utils.concurrent.PriorityThreadFactory;
import com.aionemu.commons.utils.concurrent.RunnableWrapper;
import com.google.common.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author -Nemesiss-, Rolandas
 */
public class ThreadPoolManager implements Executor {

	/**
	 * PriorityThreadFactory creating new threads for ThreadPoolManager
	 */

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final ThreadPoolManager instance = new ThreadPoolManager();
	}

	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(ThreadPoolManager.class);

	/**
	 * @return ThreadPoolManager instance.
	 */
	public static final ThreadPoolManager getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * STPE for normal scheduled tasks
	 */
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private ListeningScheduledExecutorService scheduledThreadPool;
	/**
	 * TPE for execution of gameserver client packets
	 */
	private final ThreadPoolExecutor generalPacketsThreadPoolExecutor;
	private final ListeningExecutorService generalPacketsThreadPool;

	/**
	 * Constructor.
	 */
	private ThreadPoolManager() {
		new DeadLockDetector(60, DeadLockDetector.RESTART).start();

		scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(4, new PriorityThreadFactory("ScheduledThreadPool",
				Thread.NORM_PRIORITY));
		scheduledThreadPool = MoreExecutors.listeningDecorator(scheduledThreadPoolExecutor);

		generalPacketsThreadPoolExecutor = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<Runnable>());
		generalPacketsThreadPool = MoreExecutors.listeningDecorator(generalPacketsThreadPoolExecutor);
	}

	/**
	 * Executes Runnable - GameServer Client packet.
	 *
	 * @param pkt
	 */
	@Override
	public void execute(final Runnable pkt) {
		generalPacketsThreadPool.execute(new RunnableWrapper(pkt));
	}

	/**
	 * @return the packetsThreadPool
	 */
	public ListeningExecutorService getPacketsThreadPool() {
		return generalPacketsThreadPool;
	}

	/**
	 * Schedule
	 *
	 * @param <T>
	 * @param r
	 * @param delay
	 * @return ScheduledFuture
	 */

	@SuppressWarnings("unchecked")
	public <T extends Runnable> ListenableFuture<T> schedule(final T r, long delay) {
		try {
			if (delay < 0)
				delay = 0;
			return (ListenableFuture<T>) JdkFutureAdapters.listenInPoolThread(scheduledThreadPool.schedule(r, delay,
					TimeUnit.MILLISECONDS));
		} catch (RejectedExecutionException e) {
			return null; /* shutdown, ignore */
		}
	}

	/**
	 * Schedule at fixed rate
	 *
	 * @param <T>
	 * @param r
	 * @param initial
	 * @param delay
	 * @return ScheduledFuture
	 */
	@SuppressWarnings("unchecked")
	public <T extends Runnable> ListenableFuture<T> scheduleAtFixedRate(final T r, long initial, long delay) {
		try {
			if (delay < 0)
				delay = 0;
			if (initial < 0)
				initial = 0;
			return (ListenableFuture<T>) JdkFutureAdapters.listenInPoolThread(scheduledThreadPool.scheduleAtFixedRate(r,
					initial, delay, TimeUnit.MILLISECONDS));
		} catch (RejectedExecutionException e) {
			return null;
		}
	}

	/**
	 * Shutdown all thread pools.
	 */
	public void shutdown() {
		try {
			scheduledThreadPool.shutdown();
			generalPacketsThreadPool.shutdown();
			scheduledThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			generalPacketsThreadPool.awaitTermination(2, TimeUnit.SECONDS);
			log.info("All ThreadPools are now stopped.");
		} catch (InterruptedException e) {
			log.error("Can't shutdown ThreadPoolManager", e);
		}
	}
}
