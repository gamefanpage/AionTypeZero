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

package org.typezero.gameserver.network.aion.serverpackets;

import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.configs.main.SiegeConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.team.legion.LegionEmblem;
import org.typezero.gameserver.model.team.legion.LegionEmblemType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.LegionService;
import org.typezero.gameserver.services.SiegeService;

import java.util.Map;

/**
 * @author Sarynth
 */
public class SM_SIEGE_LOCATION_INFO extends AionServerPacket {

	/**
	 * infoType 0 - reset 1 - update
	 */
	private int infoType;
	private Map<Integer, SiegeLocation> locations;
	private static final Logger log = LoggerFactory.getLogger(SM_SIEGE_LOCATION_INFO.class);

	public SM_SIEGE_LOCATION_INFO() {
		this.infoType = 0;
		locations = SiegeService.getInstance().getSiegeLocations();
	}

	public SM_SIEGE_LOCATION_INFO(SiegeLocation loc) {
		this.infoType = 1;
		locations = new FastMap<Integer, SiegeLocation>();
		locations.put(loc.getLocationId(), loc);
	}

	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();
		if (!SiegeConfig.SIEGE_ENABLED) {
			writeC(0);
			writeH(0);
			return;
		}

		writeC(infoType);
		writeH(locations.size());

		for (SiegeLocation loc : locations.values()) {
			LegionEmblem emblem = new LegionEmblem();
			writeD(loc.getLocationId());

			int legionId = loc.getLegionId();
			writeD(legionId);

			if (legionId != 0)
				if (LegionService.getInstance().getLegion(legionId) == null)
					log.error("Can't find or load legion with id " + legionId);
				else
					emblem = LegionService.getInstance().getLegion(legionId).getLegionEmblem();

			if (emblem.getEmblemType() == LegionEmblemType.DEFAULT) {
				writeD(emblem.getEmblemId());
				writeC(255);
				writeC(emblem.getColor_r());
				writeC(emblem.getColor_g());
				writeC(emblem.getColor_b());
			}
			else {
				writeD(emblem.getCustomEmblemData().length);
				writeC(255);
				writeC(emblem.getColor_r());
				writeC(emblem.getColor_g());
				writeC(emblem.getColor_b());
			}

			writeC(loc.getRace().getRaceId());

			// is vulnerable (0 - no, 2 - yes)
			writeC(loc.isVulnerable() ? 2 : 0);

			// faction can teleport (0 - no, 1 - yes)
			writeC(loc.isCanTeleport(player)? 1 : 0);

			// Next State (0 - invulnerable, 1 - vulnerable)
			writeC(loc.getNextState());

			writeH(0); // unk
			writeH(1);
			switch (loc.getLocationId()) {
				case 2111: // veille timer
				case 3111: // mastarius timer
					writeD(SiegeService.getInstance().getRemainingSiegeTimeInSeconds(loc.getLocationId()));
					break;
				default:
					writeD(10000);
					break;
			}
            writeD(0);//unk 4.7
            writeD(67);//unk 4.7
            writeD(0);//unk 4.7
		}
	}

}
