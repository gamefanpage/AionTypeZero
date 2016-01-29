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

import com.aionemu.commons.utils.ExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.*;

/**
 * @author -Nemesiss-, ATracer
 */
public class DeadLockDetector extends Thread {

	private static final Logger log = LoggerFactory.getLogger(DeadLockDetector.class);
	/**
	 * What should we do on DeadLock
	 */
	public static final byte NOTHING = 0;
	/**
	 * What should we do on DeadLock
	 */
	public static final byte RESTART = 1;

	/**
	 * how often check for deadlocks
	 */
	private final int sleepTime;
	/**
	 * ThreadMXBean
	 */
	private final ThreadMXBean tmx;
	/**
	 * What should we do on DeadLock
	 */
	private final byte doWhenDL;

	/**
	 * Create new DeadLockDetector with given values.
	 *
	 * @param sleepTime
	 * @param doWhenDL
	 */
	public DeadLockDetector(final int sleepTime, final byte doWhenDL) {
		super("DeadLockDetector");
		this.sleepTime = sleepTime * 1000;
		this.tmx = ManagementFactory.getThreadMXBean();
		this.doWhenDL = doWhenDL;
	}

	/**
	 * Check if there is a DeadLock.
	 */
	@Override
	public final void run() {
		boolean deadlock = false;
		while (!deadlock)
			try {
				long[] ids = tmx.findDeadlockedThreads();

				if (ids != null) {
					/** deadlock found :/ */
					deadlock = true;
					ThreadInfo[] tis = tmx.getThreadInfo(ids, true, true);
					String info = "DeadLock Found!\n";
					for (ThreadInfo ti : tis)
						info += ti.toString();

					for (ThreadInfo ti : tis) {
						LockInfo[] locks = ti.getLockedSynchronizers();
						MonitorInfo[] monitors = ti.getLockedMonitors();
						if (locks.length == 0 && monitors.length == 0)
						/** this thread is deadlocked but its not guilty */
							continue;

						ThreadInfo dl = ti;
						info += "Java-level deadlock:\n";
						info += createShortLockInfo(dl);
						while ((dl = tmx.getThreadInfo(new long[]{dl.getLockOwnerId()}, true, true)[0]).getThreadId() != ti
								.getThreadId())
							info += createShortLockInfo(dl);

						info += "\nDumping all threads:\n";
						for (ThreadInfo dumpedTI : tmx.dumpAllThreads(true, true)) {
							info += printDumpedThreadInfo(dumpedTI);
						}
					}
					log.warn(info);

					if (doWhenDL == RESTART)
						System.exit(ExitCode.CODE_RESTART);
				}
				Thread.sleep(sleepTime);
			} catch (Exception e) {
				log.warn("DeadLockDetector: " + e, e);
			}
	}

	/**
	 * Example:
	 * <p>
	 * Java-level deadlock:<br>
	 * Thread-0 is waiting to lock java.lang.Object@276af2 which is held by main. Locked synchronizers:0 monitors:1<br>
	 * main is waiting to lock java.lang.Object@fa3ac1 which is held by Thread-0. Locked synchronizers:0 monitors:1<br>
	 * </p>
	 */
	private String createShortLockInfo(ThreadInfo threadInfo) {
		StringBuilder sb = new StringBuilder("\t");
		sb.append(threadInfo.getThreadName());
		sb.append(" is waiting to lock ");
		sb.append(threadInfo.getLockInfo().toString());
		sb.append(" which is held by ");
		sb.append(threadInfo.getLockOwnerName());
		sb.append(". Locked synchronizers:");
		sb.append(threadInfo.getLockedSynchronizers().length);
		sb.append(" monitors:");
		sb.append(threadInfo.getLockedMonitors().length);
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * Full thread info (short info and stacktrace)<br>
	 * Example:
	 * <p>
	 * "Thread-0" Id=10 BLOCKED <br>
	 * at org.typezero.gameserver.DeadlockTest$1$1.run(DeadlockTest.java:70)<br>
	 * - locked java.lang.Object@fa3ac1<br>
	 * at java.lang.Thread.run(Thread.java:662)
	 * </p>
	 */
	private String printDumpedThreadInfo(ThreadInfo threadInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\"" + threadInfo.getThreadName() + "\"" + " Id=" + threadInfo.getThreadId() + " "
				+ threadInfo.getThreadState() + "\n");
		StackTraceElement[] stacktrace = threadInfo.getStackTrace();
		for (int i = 0; i < stacktrace.length; i++) {
			StackTraceElement ste = stacktrace[i];
			sb.append("\t" + "at " + ste.toString() + "\n");
			for (MonitorInfo mi : threadInfo.getLockedMonitors()) {
				if (mi.getLockedStackDepth() == i) {
					sb.append("\t-  locked " + mi);
					sb.append('\n');
				}
			}
		}
		return sb.toString();
	}
}
