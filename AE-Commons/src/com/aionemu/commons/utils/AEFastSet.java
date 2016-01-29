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

package com.aionemu.commons.utils;

import javolution.util.FastCollection.Record;
import javolution.util.FastMap;

import java.util.Iterator;
import java.util.Set;

/**
 * @author NB4L1
 */
@SuppressWarnings("unchecked")
public class AEFastSet<E> extends AEFastCollection<E> implements Set<E> {

	private static final Object NULL = new Object();

	private final FastMap<E, Object> map;

	public AEFastSet() {
		map = new FastMap<E, Object>();
	}

	public AEFastSet(int capacity) {
		map = new FastMap<E, Object>(capacity);
	}

	public AEFastSet(Set<? extends E> elements) {
		map = new FastMap<E, Object>(elements.size());

		addAll(elements);
	}

	/*
	 * public AEFastSet<E> setShared(boolean isShared) { map.setShared(isShared); return this; }
	 */

	public boolean isShared() {
		return map.isShared();
	}

	@Override
	public Record head() {
		return map.head();
	}

	@Override
	public Record tail() {
		return map.tail();
	}

	@Override
	public E valueOf(Record record) {
		return ((FastMap.Entry<E, Object>) record).getKey();
	}

	@Override
	public void delete(Record record) {
		map.remove(((FastMap.Entry<E, Object>) record).getKey());
	}

	@Override
	public void delete(Record record, E value) {
		map.remove(value);
	}

	@Override
	public boolean add(E value) {
		return map.put(value, NULL) == null;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	@Override
	public boolean remove(Object o) {
		return map.remove(o) != null;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public String toString() {
		return super.toString() + "-" + map.keySet().toString();
	}
}
