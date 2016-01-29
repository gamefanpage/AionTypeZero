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

package com.aionemu.commons.utils.concurrent;

import com.aionemu.commons.network.util.ThreadUncaughtExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author -Nemesiss-
 */
public class PriorityThreadFactory implements ThreadFactory {

	/**
	 * Priority of new threads
	 */
	private int prio;
	/**
	 * Thread group name
	 */
	private String name;

	/*
	 * Default pool for the thread group, can be null for default
	 */
	private ExecutorService threadPool;

	/**
	 * Number of created threads
	 */
	private AtomicInteger threadNumber = new AtomicInteger(1);
	/**
	 * ThreadGroup for created threads
	 */
	private ThreadGroup group;

	/**
	 * Constructor.
	 *
	 * @param name
	 * @param prio
	 */
	public PriorityThreadFactory(final String name, final int prio) {
		this.prio = prio;
		this.name = name;
		group = new ThreadGroup(this.name);
	}

	public PriorityThreadFactory(final String name, ExecutorService defaultPool) {
		this(name, Thread.NORM_PRIORITY);
		setDefaultPool(defaultPool);
	}

	protected void setDefaultPool(ExecutorService pool) {
		threadPool = pool;
	}

	protected ExecutorService getDefaultPool() {
		return threadPool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Thread newThread(final Runnable r) {
		Thread t = new Thread(group, r);
		t.setName(name + "-" + threadNumber.getAndIncrement());
		t.setPriority(prio);
		t.setUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		return t;
	}
}
