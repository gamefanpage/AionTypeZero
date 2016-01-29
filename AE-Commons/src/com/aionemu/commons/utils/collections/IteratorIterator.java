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
package com.aionemu.commons.utils.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is representing an iterator, that is used to iterate through the collection that has format
 * Iterable&lt;Iterable&lt;V&gt;&gt;.<br>
 * <p/>
 * <pre>
 * &lt;code&gt;
 * Usage:&lt;br&gt;
 * List&lt;List&lt;Integer&gt;&gt; someList = ....
 * IteratorIterator&lt;Integer&gt; iterator = new IteratorIterator&lt;Integer&gt;(someList)
 *
 * OR:
 *
 * Map&lt;Integer, Set&lt;SomeClass&gt;&gt; mapOfSets = ....
 * IteratorIterator&lt;SomeCLass&gt; iterator = new IteratorIterator&lt;SomeClass&gt;(mapsOfSets.values());
 * &lt;/code&gt;
 * </pre>
 * <p/>
 * This iterator is not thread-safe. <br>
 * This iterator omits null values for first level collection, which means that if we have:
 * <p/>
 * <pre>
 * &lt;code&gt;
 * Set&lt;Set&lt;Integer&gt;&gt; setOfSets = ....
 * setOfSets.add(null);
 * setOfSets.add(someSetOfIntegers); // Where someSetsOfIntegers is a set containing 1 and 2
 *
 * IteratorIterator&lt;Integer&gt; it = new IteratorIterator&lt;Integer&gt;(setOfSets);
 * &lt;/code&gt;
 * </pre>
 * <p/>
 * This <code>it</code> iterator will return only 2 values ( 1 and 2 )
 *
 * @param <V> Type of the values over which this iterator iterates
 * @author Luno
 */
public class IteratorIterator<V> implements Iterator<V> {

	/**
	 * 1st Level iterator
	 */
	private Iterator<? extends Iterable<V>> firstLevelIterator;

	/**
	 * 2nd level iterator
	 */
	private Iterator<V> secondLevelIterator;

	/**
	 * Constructor of <tt>IteratorIterator</tt>
	 *
	 * @param itit an Iterator that iterate over Iterable<Value>
	 */
	public IteratorIterator(Iterable<? extends Iterable<V>> itit) {
		this.firstLevelIterator = itit.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		if (secondLevelIterator != null && secondLevelIterator.hasNext())
			return true;

		while (firstLevelIterator.hasNext()) {
			Iterable<V> iterable = firstLevelIterator.next();

			if (iterable != null) {
				secondLevelIterator = iterable.iterator();

				if (secondLevelIterator.hasNext())
					return true;
			}
		}
		return false;
	}

	/**
	 * Returns next value of collection.<br>
	 * If there is no next value, then {@link NoSuchElementException} thrown.
	 */
	@Override
	public V next() {
		if (secondLevelIterator == null || !secondLevelIterator.hasNext())
			throw new NoSuchElementException();
		return secondLevelIterator.next();
	}

	/**
	 * <font color="red"><b>NOT IMPLEMENTED</b></font>
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("This operation is not supported.");
	}
}
