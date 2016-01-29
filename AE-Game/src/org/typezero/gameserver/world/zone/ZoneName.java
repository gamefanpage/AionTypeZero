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

package org.typezero.gameserver.world.zone;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rolandas
 */
public final class ZoneName {

	private final static Logger log = LoggerFactory.getLogger(ZoneName.class);

	private static final FastMap<String, ZoneName> zoneNames = new FastMap<String, ZoneName>();
	public static final String NONE = "NONE";
	public static final String ABYSS_CASTLE = "_ABYSS_CASTLE_AREA_";

	static {
		zoneNames.put(NONE, new ZoneName(NONE));
		zoneNames.put(ABYSS_CASTLE, new ZoneName(ABYSS_CASTLE));
	}

	private String _name;

	private ZoneName(String name) {
		this._name = name;
	}

	public String name() {
		return _name;
	}

	public int id() {
		return _name.hashCode();
	}

	public static final ZoneName createOrGet(String name) {
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
			return zoneNames.get(name);
		ZoneName newZone = new ZoneName(name);
		zoneNames.put(name, newZone);
		return newZone;
	}

	public static final int getId(String name) {
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
			return zoneNames.get(name).id();
		return zoneNames.get(NONE).id();
	}

	public static final ZoneName get(String name) {
		name = name.toUpperCase();
		if (zoneNames.containsKey(name))
			return zoneNames.get(name);
		log.warn("Missing zone : " + name);
		return zoneNames.get(NONE);
	}

	@Override
	public String toString() {
		return _name;
	}

}
