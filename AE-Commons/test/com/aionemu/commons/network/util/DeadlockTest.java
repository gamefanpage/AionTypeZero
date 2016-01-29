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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This test is for checking print of deadlock report. Should not be executed during unit test phase
 *
 * @author ATracer
 */
public class DeadlockTest {

	private final Object lock1 = new Object();
	private final Object lock2 = new Object();

	@Test(enabled = false)
	public void testCommon() {
		DeadLockDetector dd = new DeadLockDetector(2, DeadLockDetector.NOTHING);
		dd.start();
		createDeadlock();
	}

	/**
	 * This "smart" logic is for generating long stacktrace
	 */
	private void createDeadlock() {
		final Collection<String> coll = new ArrayList<String>();
		coll.add("1");
		synchronized (lock1) {
			Collection<Integer> filtered = Collections2.filter(Collections2.transform(coll, new Function<String, Integer>() {

				@Override
				public Integer apply(String input) {

					new Thread(new Runnable() {

						@Override
						public void run() {
							System.out.println("Locking lock 2 from thread 2");
							synchronized (lock2) {
								System.out.println("Deadlocking");
								synchronized (lock1) {
									System.out.println("This will not be printed");
								}
							}
						}
					}).start();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (lock2) {
						System.out.println("This will not be printed");
					}
					return Integer.valueOf(input);
				}
			}), new Predicate<Integer>() {

				@Override
				public boolean apply(Integer input) {
					return true;
				}
			});
			System.out.println(filtered.size());
		}
	}
}
