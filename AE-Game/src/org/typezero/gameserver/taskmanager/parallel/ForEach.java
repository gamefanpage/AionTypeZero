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

package org.typezero.gameserver.taskmanager.parallel;

import com.aionemu.commons.utils.internal.chmv8.CountedCompleter;
import com.aionemu.commons.utils.internal.chmv8.ForkJoinTask;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

/**
 * @author Rolandas <br>
 *         To use forEach method, statically import the method</tt>
 */

public final class ForEach<E> extends CountedCompleter<E> {

	private static final Logger log = LoggerFactory.getLogger(ForEach.class);
	private static final long serialVersionUID = 7902148320917998146L;

	/**
	 * Calls predicate for each element in the collection asynchronously. Utilizes Fork/Join framework to speed up
	 * processing, by using a divide/conquer algorithm
	 *
	 * @param list
	 *          - element list to loop
	 * @param operation
	 *          - operation to perform on each element
	 */
	public static <E> ForkJoinTask<E> forEach(Collection<E> list, Predicate<E> operation) {
		if (list.size() > 0) {
			@SuppressWarnings("unchecked")
			E[] objects = list.toArray((E[]) new Object[list.size()]);
			CountedCompleter<E> completer = new ForEach<E>(null, operation, 0, objects.length, objects);
			return completer;
		}
		return null;
	}

	/**
	 * See {@link #forEach(Collection, Predicate) forEach(Collection&lt;E&gt; list, Predicate&lt;E&gt; operation)}
	 */
	public static <E> ForkJoinTask<E> forEach(Predicate<E> operation, E... list) {
		if (list != null && list.length > 0) {
			CountedCompleter<E> completer = new ForEach<E>(null, operation, 0, list.length, list);
			return completer;
		}
		return null;
	}

	final E[] list;
	final Predicate<E> operation;
	final int lo, hi;

	private ForEach(CountedCompleter<E> rootTask, Predicate<E> operation, int lo, int hi, E... list) {
		super(rootTask);
		this.list = list;
		this.operation = operation;
		this.lo = lo;
		this.hi = hi;
	}

	@Override
	public void compute() {
		int l = lo, h = hi;
		while (h - l >= 2) {
			int mid = (l + h) >>> 1;
			addToPendingCount(1);
			new ForEach<E>(this, operation, mid, h, list).fork(); // right child
			h = mid;
		}
		if (h > l) {
			try {
				operation.apply(list[l]);
			}
			catch (Throwable ex) {
				// we want to complete without an exception re-thrown
				// otherwise, should call completeExceptionally(ex);
				onExceptionalCompletion(ex, this);
			}
		}
		propagateCompletion();

	}

	@Override
	public boolean onExceptionalCompletion(Throwable ex, CountedCompleter<?> caller) {
		log.warn("", ex);
		// returning false would result in infinite wait when calling join();
		return true;
	}

}
