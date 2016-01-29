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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * Sent to fill the search panel of a players social window<br />
 * I.E.: In response to a <tt>CM_PLAYER_SEARCH</tt>
 *
 * @author Ben
 */
public class SM_PLAYER_SEARCH extends AionServerPacket {

	private static final Logger log = LoggerFactory.getLogger(SM_PLAYER_SEARCH.class);

	private List<Player> players;
	private int region;

	/**
	 * Constructs a new packet that will send these players
	 *
	 * @param players
	 *          List of players to show
	 * @param region
	 *          of search - should be passed as parameter to prevent null in player.getActiveRegion()
	 */
	public SM_PLAYER_SEARCH(List<Player> players, int region) {
		this.players = new ArrayList<Player>(players);
		this.region = region;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		writeH(players.size());
		for (Player player : players) {
			if (player.getActiveRegion() == null) {
				log.warn("CHECKPOINT: null active region for " + player.getObjectId() + "-" + player.getX() + "-"
					+ player.getY() + "-" + player.getZ());
			}
			writeD(player.getActiveRegion() == null ? region : player.getActiveRegion().getMapId());
			writeF(player.getPosition().getX());
			writeF(player.getPosition().getY());
			writeF(player.getPosition().getZ());
			writeC(player.getPlayerClass().getClassId());
			writeC(player.getGender().getGenderId());
			writeC(player.getLevel());
			if(player.isInGroup2())
				writeC(3);
			else if(player.isLookingForGroup())
				writeC(2);
			else
				writeC(0);
			writeS(player.getName(), 56);

		}
	}

}
