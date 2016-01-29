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

import com.aionemu.commons.configs.CommonsConfig;
import javolution.text.TextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author NB4L1
 */
public class ExecuteWrapper implements Executor {

	private static final Logger log = LoggerFactory.getLogger(ExecuteWrapper.class);

	@Override
	public void execute(Runnable runnable) {
		execute(runnable, Long.MAX_VALUE);
	}

	public static void execute(Runnable runnable, long maximumRuntimeInMillisecWithoutWarning) {
		long begin = System.nanoTime();

		try {
			runnable.run();
		} catch (Throwable t) {
			log.warn("Exception in a Runnable execution:", t);
		} finally {

			long runtimeInNanosec = System.nanoTime() - begin;
			Class<? extends Runnable> clazz = runnable.getClass();

			if (CommonsConfig.RUNNABLESTATS_ENABLE) {
				RunnableStatsManager.handleStats(clazz, runtimeInNanosec);
			}

			long runtimeInMillisec = TimeUnit.NANOSECONDS.toMillis(runtimeInNanosec);
			if (runtimeInMillisec > maximumRuntimeInMillisecWithoutWarning) {
				TextBuilder tb = TextBuilder.newInstance();
				tb.append(clazz);
				tb.append(" - execution time: ");
				tb.append(runtimeInMillisec);
				tb.append("msec");
				log.warn(tb.toString());
			}
		}
	}
}
