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

package org.typezero.gameserver.services.rift;

import org.typezero.gameserver.model.Race;

/**
 *
 * @author Source
 */
public enum RiftEnum {

	KAISINEL_AM(1170, "KAISINEL_AM", "KAISINEL_AS", 24, 45, 65, Race.ASMODIANS, true),
	ELTNEN_AM(2120, "ELTNEN_AM", "MORHEIM_AS", 12, 20, 65, Race.ASMODIANS),
	ELTNEN_BM(2121, "ELTNEN_BM", "MORHEIM_BS", 20, 20, 65, Race.ASMODIANS),
	ELTNEN_CM(2122, "ELTNEN_CM", "MORHEIM_CS", 35, 20, 65, Race.ASMODIANS),
	ELTNEN_DM(2123, "ELTNEN_DM", "MORHEIM_DS", 35, 20, 65, Race.ASMODIANS),
	ELTNEN_EM(2124, "ELTNEN_EM", "MORHEIM_ES", 45, 20, 65, Race.ASMODIANS),
	ELTNEN_FM(2125, "ELTNEN_FM", "MORHEIM_FS", 50, 20, 65, Race.ASMODIANS),
	ELTNEN_GM(2126, "ELTNEN_GM", "MORHEIM_GS", 50, 20, 65, Race.ASMODIANS),
	HEIRON_AM(2140, "HEIRON_AM", "BELUSLAN_AS", 24, 20, 65, Race.ASMODIANS),
	HEIRON_BM(2141, "HEIRON_BM", "BELUSLAN_BS", 36, 20, 65, Race.ASMODIANS),
	HEIRON_CM(2142, "HEIRON_CM", "BELUSLAN_CS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_DM(2143, "HEIRON_DM", "BELUSLAN_DS", 48, 20, 65, Race.ASMODIANS),
	HEIRON_EM(2144, "HEIRON_EM", "BELUSLAN_ES", 60, 20, 65, Race.ASMODIANS),
	HEIRON_FM(2145, "HEIRON_FM", "BELUSLAN_FS", 72, 20, 65, Race.ASMODIANS),
	HEIRON_GM(2146, "HEIRON_GM", "BELUSLAN_GS", 72, 20, 65, Race.ASMODIANS),
	INGGISON_AM(2150, "INGGISON_AM", "GELKMAROS_AS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_BM(2151, "INGGISON_BM", "GELKMAROS_BS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_CM(2152, "INGGISON_CM", "GELKMAROS_CS", 150, 20, 65, Race.ASMODIANS),
	INGGISON_DM(2153, "INGGISON_DM", "GELKMAROS_DS", 150, 20, 65, Race.ASMODIANS),
	SINGEA_AM(2170, "SINGEA_AM", "ENSHAR_AS", 12, 20, 65, Race.ASMODIANS),
	SINGEA_BM(2171, "SINGEA_BM", "ENSHAR_BS", 36, 20, 65, Race.ASMODIANS),
	SINGEA_CM(2172, "SINGEA_CM", "ENSHAR_CS", 48, 20, 65, Race.ASMODIANS),
	SINGEA_DM(2173, "SINGEA_DM", "ENSHAR_DS", 48, 20, 65, Race.ASMODIANS),
	SINGEA_EM(2174, "SINGEA_EM", "ENSHAR_ES", 48, 20, 65, Race.ASMODIANS),
	SINGEA_FM(2175, "SINGEA_FM", "ENSHAR_FS", 48, 20, 65, Race.ASMODIANS),

	MARCHUTAN_AM(1280, "MARCHUTAN_AM", "MARCHUTAN_AS", 24, 45, 65, Race.ELYOS, true),
	MORHEIM_AM(2220, "MORHEIM_AM", "ELTNEN_AS", 12, 20, 65, Race.ELYOS),
	MORHEIM_BM(2221, "MORHEIM_BM", "ELTNEN_BS", 20, 20, 65, Race.ELYOS),
	MORHEIM_CM(2222, "MORHEIM_CM", "ELTNEN_CS", 35, 20, 65, Race.ELYOS),
	MORHEIM_DM(2223, "MORHEIM_DM", "ELTNEN_DS", 35, 20, 65, Race.ELYOS),
	MORHEIM_EM(2224, "MORHEIM_EM", "ELTNEN_ES", 45, 20, 65, Race.ELYOS),
	MORHEIM_FM(2225, "MORHEIM_FM", "ELTNEN_FS", 50, 20, 65, Race.ELYOS),
	MORHEIM_GM(2226, "MORHEIM_GM", "ELTNEN_GS", 50, 20, 65, Race.ELYOS),
	BELUSLAN_AM(2240, "BELUSLAN_AM", "HEIRON_AS", 24, 20, 65, Race.ELYOS),
	BELUSLAN_BM(2241, "BELUSLAN_BM", "HEIRON_BS", 36, 20, 65, Race.ELYOS),
	BELUSLAN_CM(2242, "BELUSLAN_CM", "HEIRON_CS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_DM(2243, "BELUSLAN_DM", "HEIRON_DS", 48, 20, 65, Race.ELYOS),
	BELUSLAN_EM(2244, "BELUSLAN_EM", "HEIRON_ES", 60, 20, 65, Race.ELYOS),
	BELUSLAN_FM(2245, "BELUSLAN_FM", "HEIRON_FS", 72, 20, 65, Race.ELYOS),
	BELUSLAN_GM(2246, "BELUSLAN_GM", "HEIRON_GS", 72, 20, 65, Race.ELYOS),
	GELKMAROS_AM(2270, "GELKMAROS_AM", "INGGISON_AS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_BM(2271, "GELKMAROS_BM", "INGGISON_BS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_CM(2272, "GELKMAROS_CM", "INGGISON_CS", 150, 20, 65, Race.ELYOS),
	GELKMAROS_DM(2273, "GELKMAROS_DM", "INGGISON_DS", 150, 20, 65, Race.ELYOS),
	ENSHAR_AM(2280, "ENSHAR_AM", "SINGEA_AS", 12, 20, 65, Race.ELYOS),
	ENSHAR_BM(2281, "ENSHAR_BM", "SINGEA_BS", 36, 20, 65, Race.ELYOS),
	ENSHAR_CM(2282, "ENSHAR_CM", "SINGEA_CS", 48, 20, 65, Race.ELYOS),
	ENSHAR_DM(2283, "ENSHAR_DM", "SINGEA_DS", 48, 20, 65, Race.ELYOS),
	ENSHAR_EM(2284, "ENSHAR_EM", "SINGEA_ES", 48, 20, 65, Race.ELYOS),
	ENSHAR_FM(2285, "ENSHAR_FM", "SINGEA_ES", 48, 20, 65, Race.ELYOS);

	private int id;
	private String master;
	private String slave;
	private int entries;
	private int minLevel;
	private int maxLevel;
	private Race destination;
	private boolean vortex;

	private RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination) {
		this(id, master, slave, entries, minLevel, maxLevel, destination, false);
	}

	private RiftEnum(int id, String master, String slave, int entries, int minLevel, int maxLevel, Race destination, boolean vortex) {
		this.id = id;
		this.master = master;
		this.slave = slave;
		this.entries = entries;
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.destination = destination;
		this.vortex = vortex;
	}

	public static RiftEnum getRift(int id) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.getId() == id) {
				return rift;
			}
		}

		throw new IllegalArgumentException("Unsupported rift id: " + id);
	}

	public static RiftEnum getVortex(Race race) throws IllegalArgumentException {
		for (RiftEnum rift : RiftEnum.values()) {
			if (rift.isVortex() && rift.getDestination().equals(race)) {
				return rift;
			}
		}

		throw new IllegalArgumentException("Unsupported vortex race: " + race);
	}

	/**
	 * @return rift id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the master
	 */
	public String getMaster() {
		return master;
	}

	/**
	 * @return the slave
	 */
	public String getSlave() {
		return slave;
	}

	/**
	 * @return the entries
	 */
	public int getEntries() {
		return entries;
	}

	/**
	 * @return the minLevel
	 */
	public int getMinLevel() {
		return minLevel;
	}

	/**
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * @return the destination
	 */
	public Race getDestination() {
		return destination;
	}

	public boolean isVortex() {
		return vortex;
	}

}
