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

package org.typezero.gameserver.world.container;

import javolution.util.FastMap;
import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.world.exceptions.DuplicateAionObjectException;

import java.util.Iterator;
import java.util.Map;

/**
 * Container for storing Legions by legionId and name.
 *
 * @author Simple
 */
public class LegionContainer implements Iterable<Legion> {

	/**
	 * Map<LegionId, Legion>
	 */
	private final Map<Integer, Legion> legionsById = new FastMap<Integer, Legion>().shared();
	/**
	 * Map<LegionName, Legion>
	 */
	private final Map<String, Legion> legionsByName = new FastMap<String, Legion>().shared();

	/**
	 * Add Legion to this Container.
	 *
	 * @param legion
	 */
	public void add(Legion legion) {
		if (legion == null || legion.getLegionName() == null)
			return;

		if (legionsById.put(legion.getLegionId(), legion) != null)
			throw new DuplicateAionObjectException();
		if (legionsByName.put(legion.getLegionName().toLowerCase(), legion) != null)
			throw new DuplicateAionObjectException();
	}

	/**
	 * Remove Legion from this Container.
	 *
	 * @param legion
	 */
	public void remove(Legion legion) {
		legionsById.remove(legion.getLegionId());
		legionsByName.remove(legion.getLegionName().toLowerCase());
	}

	/**
	 * Get Legion object by objectId.
	 *
	 * @param legionId
	 *          - legionId of legion.
	 * @return Legion with given ojectId or null if Legion with given legionId is not logged.
	 */
	public Legion get(int legionId) {
		return legionsById.get(legionId);
	}

	/**
	 * Get Legion object by name.
	 *
	 * @param name
	 *          - name of legion
	 * @return Legion with given name or null if Legion with given name is not logged.
	 */
	public Legion get(String name) {
		return legionsByName.get(name.toLowerCase());
	}

	/**
	 * Returns true if legion is in cached by id
	 *
	 * @param legionId
	 * @return true or false
	 */
	public boolean contains(int legionId) {
		return legionsById.containsKey(legionId);
	}

	/**
	 * Returns true if legion is in cached by name
	 *
	 * @param name
	 * @return true or false
	 */
	public boolean contains(String name) {
		return legionsByName.containsKey(name.toLowerCase());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Legion> iterator() {
		return legionsById.values().iterator();
	}

	public void clear() {
		legionsById.clear();
		legionsByName.clear();
	}
}
