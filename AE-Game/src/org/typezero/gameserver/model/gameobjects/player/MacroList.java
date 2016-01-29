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

package org.typezero.gameserver.model.gameobjects.player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

/**
 * Player macrosses collection, contains all player macrosses.
 * <p/>
 * Created on: 13.07.2009 16:28:23
 *
 * @author Aquanox, nrg
 */
public class MacroList {

	/**
	 * Class logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MacroList.class);

	/**
	 * Container of macrosses, position to xml.
	 */
	private final Map<Integer, String> macrosses;

	/**
	 * Creates an empty macro list
	 */
	public MacroList() {
		this.macrosses = new HashMap<Integer, String>(12);
	}

	/**
	 * Create new instance of <tt>MacroList</tt>.
	 *
	 * @param arg
	 */
	public MacroList(Map<Integer, String> arg) {
		this.macrosses = arg;
	}

	/**
	 * Returns map with all macrosses
	 *
	 * @return all macrosses
	 */
	public Map<Integer, String> getMacrosses() {
		return Collections.unmodifiableMap(macrosses);
	}

	/**
	 * Add macro to the collection.
	 *
	 * @param macroPosition
	 *          Macro order.
	 * @param macroXML
	 *          Macro Xml contents.
	 * @return <tt>true</tt> if macro addition was successful, and it can be stored into database. Otherwise
	 *         <tt>false</tt>.
	 */
	public synchronized boolean addMacro(int macroPosition, String macroXML) {
		if (macrosses.containsKey(macroPosition)) {
			macrosses.remove(macroPosition);
			macrosses.put(macroPosition, macroXML);
			return false;
		}

		macrosses.put(macroPosition, macroXML);
		return true;
	}

	/**
	 * Remove macro from the list.
	 *
	 * @param macroPosition
	 * @return <tt>true</tt> if macro deletion was successful, and changes can be stored into database. Otherwise
	 *         <tt>false</tt>.
	 */
	public synchronized boolean removeMacro(int macroPosition) {
		String m = macrosses.remove(macroPosition);
		if (m == null)//
		{
			logger.warn("Trying to remove non existing macro.");
			return false;
		}
		return true;
	}

	/**
	 * Returns count of available macrosses.
	 *
	 * @return count of available macrosses.
	 */
	public int getSize() {
		return macrosses.size();
	}

	/**
	 * Returns an unmodifiable map of macro id to macro contents.
	 * NOTE: Retail sends only 7 macros per packet, that's why we have to split macros
	 */
	public Map<Integer, String> getMarcosPart(boolean secondPart) {
		Map<Integer, String> macrosPart = new HashMap<Integer, String>();
		int currentIndex = secondPart ? 7 : 0;
		int endIndex = secondPart ? 11 : 6;

    for(;currentIndex <= endIndex; currentIndex++) {
    	macrosPart.put(currentIndex, macrosses.get(currentIndex));
    }
		return Collections.unmodifiableMap(macrosPart);
    }

    /**
     * Returns an entry set of macro id to macro contents.
     * @return
     */
    public Set<Map.Entry<Integer, String>> entrySet() {
        return Collections.unmodifiableSet(getMacrosses().entrySet());
    }
}
