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

package org.typezero.gameserver.utils.collections.cachemap;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * Base class for {@link WeakCacheMap} and {@link SoftCacheMap}
 *
 * @author Luno
 * @param <K>
 * @param <V>
 */
abstract class AbstractCacheMap<K, V> implements CacheMap<K, V> {

	private final Logger log;

	protected final String cacheName;
	protected final String valueName;

	/** Map storing references to cached objects */
	protected final Map<K, Reference<V>> cacheMap = new HashMap<K, Reference<V>>();

	protected final ReferenceQueue<V> refQueue = new ReferenceQueue<V>();

	/**
	 * @param cacheName
	 * @param valueName
	 */
	AbstractCacheMap(String cacheName, String valueName, Logger log) {
		this.cacheName = "#CACHE  [" + cacheName + "]#  ";
		this.valueName = valueName;
		this.log = log;
	}

	/** {@inheritDoc} */
	@Override
	public void put(K key, V value) {
		cleanQueue();

		if (cacheMap.containsKey(key))
			throw new IllegalArgumentException("Key: " + key + " already exists in map");

		Reference<V> entry = newReference(key, value, refQueue);

		cacheMap.put(key, entry);

		if (log.isDebugEnabled())
			log.debug(cacheName + " : added " + valueName + " for key: " + key);
	}

	/** {@inheritDoc} */
	@Override
	public V get(K key) {
		cleanQueue();

		Reference<V> reference = cacheMap.get(key);

		if (reference == null)
			return null;

		V res = reference.get();

		if (res != null && log.isDebugEnabled())
			log.debug(cacheName + " : obtained " + valueName + " for key: " + key);

		return res;
	}

	@Override
	public boolean contains(K key) {
		cleanQueue();
		return cacheMap.containsKey(key);
	}

	protected abstract void cleanQueue();

	@Override
	public void remove(K key) {
		cacheMap.remove(key);
	}

	protected abstract Reference<V> newReference(K key, V value, ReferenceQueue<V> queue);
}
