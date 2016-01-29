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

package org.typezero.gameserver.network.aion.clientpackets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.gameobjects.player.FriendList.Status;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_SEARCH;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.World;

/**
 * Received when a player searches using the social search panel
 *
 * @author Ben
 */
public class CM_PLAYER_SEARCH extends AionClientPacket {

	/**
	 * The max number of players to return as results
	 */
	public static final int MAX_RESULTS = 104; //3.0

	private String name;
	private int region;
	private int classMask;
	private int minLevel;
	private int maxLevel;
	private int lfgOnly;

	public CM_PLAYER_SEARCH(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		name = readS(52);
		if (name != null) {
			name = Util.convertName(name);
		}
		region = readD();
		classMask = readD();
		minLevel = readC();
		maxLevel = readC();
		lfgOnly = readC();
		readC(); // 0x00 in search pane 0x30 in /who?
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		Iterator<Player> it = World.getInstance().getPlayersIterator();

		List<Player> matches = new ArrayList<Player>(MAX_RESULTS);

		if (activePlayer.getLevel() < CustomConfig.LEVEL_TO_SEARCH) {
			sendPacket(SM_SYSTEM_MESSAGE.STR_CANT_WHO_LEVEL(String.valueOf(CustomConfig.LEVEL_TO_SEARCH)));
			return;
		}
		while (it.hasNext() && matches.size() < MAX_RESULTS) {
			Player player = it.next();
			if (!player.isSpawned())
				continue;
			else if (player.getFriendList().getStatus() == Status.OFFLINE)
				continue;
			else if (player.isGM() && !CustomConfig.SEARCH_GM_LIST){
				continue;
			}
			else if (lfgOnly == 1 && !player.isLookingForGroup())
				continue;
			else if (!name.isEmpty() && !player.getName().toLowerCase().contains(name.toLowerCase()))
				continue;
			else if (minLevel != 0xFF && player.getLevel() < minLevel)
				continue;
			else if (maxLevel != 0xFF && player.getLevel() > maxLevel)
				continue;
			else if (classMask > 0 && (player.getPlayerClass().getMask() & classMask) == 0)
				continue;
			else if (region > 0 && player.getActiveRegion().getMapId() != region)
				continue;
			else if ((player.getRace() != activePlayer.getRace())
				&& (CustomConfig.FACTIONS_SEARCH_MODE == false)  && !activePlayer.isGM())
				continue;
			else
			// This player matches criteria
			{
				matches.add(player);
			}
		}

		sendPacket(new SM_PLAYER_SEARCH(matches, region));
	}

}
