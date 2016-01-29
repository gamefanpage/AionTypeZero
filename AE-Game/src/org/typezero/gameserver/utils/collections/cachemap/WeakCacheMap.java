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
import java.lang.ref.WeakReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a simple map implementation for cache usage.<br>
 * <br>
 * Values from the map will be removed after the first garbage collector run if there isn't any strong reference to the
 * value object.
 *
 * @author Luno
 */
class WeakCacheMap<K, V> extends AbstractCacheMap<K, V> implements CacheMap<K, V> {

	private static final Logger log = LoggerFactory.getLogger(WeakCacheMap.class);

	/**
	 * This class is a {@link WeakReference} with additional responsibility of holding key object
	 *
	 * @author Luno
	 */
	private class Entry extends WeakReference<V> {

		private K key;

		Entry(K key, V referent, ReferenceQueue<? super V> q) {
			super(referent, q);
			this.key = key;
		}

		K getKey() {
			return key;
		}
	}

	WeakCacheMap(String cacheName, String valueName) {
		super(cacheName, valueName, log);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected synchronized void cleanQueue() {
		Entry en = null;
		while ((en = (Entry) refQueue.poll()) != null) {
			K key = en.getKey();
			if (log.isDebugEnabled())
				log.debug(cacheName + " : cleaned up " + valueName + " for key: " + key);
			cacheMap.remove(key);
		}
	}

	@Override
	protected Reference<V> newReference(K key, V value, ReferenceQueue<V> vReferenceQueue) {
		return new Entry(key, value, vReferenceQueue);
	}
}
