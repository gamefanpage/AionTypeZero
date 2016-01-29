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

package org.typezero.gameserver.utils.collections;

import com.aionemu.commons.utils.internal.chmv8.PlatformDependent;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Rolandas
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class LastUsedCache<K extends Comparable, V> implements ICache<K, V>, Serializable {

	private static final long serialVersionUID = 3674312987828041877L;
	Map<K, Item> map = PlatformDependent.newConcurrentHashMap();
	Item startItem = new Item();
	Item endItem = new Item();
	int maxSize;
	private final Object syncRoot = new Object();

	static class Item {

		public Item(Comparable k, Object v) {
			key = k;
			value = v;
		}

		public Item() {
		}
		public Comparable key;
		public Object value;
		public Item previous;
		public Item next;
	}

	void removeItem(Item item) {
		synchronized (syncRoot) {
			item.previous.next = item.next;
			item.next.previous = item.previous;
		}
	}

	void insertHead(Item item) {
		synchronized (syncRoot) {
			item.previous = startItem;
			item.next = startItem.next;
			startItem.next.previous = item;
			startItem.next = item;
		}
	}

	void moveToHead(Item item) {
		synchronized (syncRoot) {
			item.previous.next = item.next;
			item.next.previous = item.previous;
			item.previous = startItem;
			item.next = startItem.next;
			startItem.next.previous = item;
			startItem.next = item;
		}
	}

	public LastUsedCache(int maxObjects) {
		maxSize = maxObjects;
		startItem.next = endItem;
		endItem.previous = startItem;
	}

	@Override
	public CachePair[] getAll() {
		CachePair p[] = new CachePair[maxSize];
		int count = 0;

		synchronized (syncRoot) {
			Item cur = startItem.next;
			while (cur != endItem) {
				p[count] = new CachePair(cur.key, cur.value);
				count++;
				cur = cur.next;
			}
		}

		CachePair np[] = new CachePair[count];
		System.arraycopy(p, 0, np, 0, count);
		return np;
	}

	/**
	 * Gets a value by key. Returns null if not found
	 */
	@Override
	public V get(K key) {
		Item cur = map.get(key);
		if (cur == null)
			return null;

		if (cur != startItem.next)
			moveToHead(cur);
		return (V) cur.value;
	}

	/**
	 * Adds or renews a cache item pair
	 */
	@Override
	public void put(K key, V value) {
		Item cur = map.get(key);
		if (cur != null) {
			cur.value = value;
			moveToHead(cur);
			return;
		}

		if (map.size() >= maxSize && maxSize != 0) {
			cur = endItem.previous;
			map.remove(cur.key);
			removeItem(cur);
		}

		Item item = new Item(key, value);
		insertHead(item);
		map.put(key, item);
	}

	@Override
	public void remove(K key) {
		Item cur = map.get(key);
		if (cur == null)
			return;
		map.remove(key);
		removeItem(cur);
	}

	@Override
	public int size() {
		return map.size();
	}
}
