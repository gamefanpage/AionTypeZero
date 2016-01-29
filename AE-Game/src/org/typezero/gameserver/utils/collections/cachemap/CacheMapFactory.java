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

import org.typezero.gameserver.configs.main.CacheConfig;

/**
 * @author Luno
 */
public class CacheMapFactory {

	/**
	 * Returns new instance of either {@link WeakCacheMap} or {@link SoftCacheMap} depending on
	 * {@link CacheConfig#SOFT_CACHE_MAP} setting.
	 *
	 * @param <K>
	 *          - Type of keys
	 * @param <V>
	 *          - Type of values
	 * @param cacheName
	 *          - The name for this cache map
	 * @param valueName
	 *          - Mnemonic name for values stored in the cache
	 * @return CacheMap<K, V>
	 */
	public static <K, V> CacheMap<K, V> createCacheMap(String cacheName, String valueName) {
		if (CacheConfig.SOFT_CACHE_MAP)
			return createSoftCacheMap(cacheName, valueName);
		else
			return createWeakCacheMap(cacheName, valueName);
	}

	/**
	 * Creates and returns an instance of {@link SoftCacheMap}
	 *
	 * @param <K>
	 *          - Type of keys
	 * @param <V>
	 *          - Type of values
	 * @param cacheName
	 *          - The name for this cache map
	 * @param valueName
	 *          - Mnemonic name for values stored in the cache
	 * @return CacheMap<K, V>
	 */
	public static <K, V> CacheMap<K, V> createSoftCacheMap(String cacheName, String valueName) {
		return new SoftCacheMap<K, V>(cacheName, valueName);
	}

	/**
	 * Creates and returns an instance of {@link WeakCacheMap}
	 *
	 * @param <K>
	 *          - Type of keys
	 * @param <V>
	 *          - Type of values
	 * @param cacheName
	 *          - The name for this cache map
	 * @param valueName
	 *          - Mnemonic name for values stored in the cache
	 * @return CacheMap<K, V>
	 */
	public static <K, V> CacheMap<K, V> createWeakCacheMap(String cacheName, String valueName) {
		return new WeakCacheMap<K, V>(cacheName, valueName);
	}
}
