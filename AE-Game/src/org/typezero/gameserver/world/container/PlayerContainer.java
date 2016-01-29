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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.world.exceptions.DuplicateAionObjectException;
import org.typezero.gameserver.world.knownlist.Visitor;

import java.util.Collection;
import java.util.Iterator;

/**
 * Container for storing Players by objectId and name.
 *
 * @author -Nemesiss-
 */
public class PlayerContainer implements Iterable<Player> {

	private static final Logger log = LoggerFactory.getLogger(PlayerContainer.class);

	/**
	 * Map<ObjectId,Player>
	 */
	private final FastMap<Integer, Player> playersById = new FastMap<Integer, Player>().shared();
	/**
	 * Map<Name,Player>
	 */
	private final FastMap<String, Player> playersByName = new FastMap<String, Player>().shared();

	/**
	 * Add Player to this Container.
	 *
	 * @param player
	 */
	public void add(Player player) {
		if (playersById.put(player.getObjectId(), player) != null)
			throw new DuplicateAionObjectException();
		if (playersByName.put(Util.convertName(player.getName()), player) != null)
			throw new DuplicateAionObjectException();
	}

	/**
	 * Remove Player from this Container.
	 *
	 * @param player
	 */
	public void remove(Player player) {
		playersById.remove(player.getObjectId());
		playersByName.remove(player.getName());
	}

	/**
	 * Get Player object by objectId.
	 *
	 * @param objectId
	 *          - ObjectId of player.
	 * @return Player with given ojectId or null if Player with given objectId is not logged.
	 */
	public Player get(int objectId) {
		return playersById.get(objectId);
	}

	/**
	 * Get Player object by name.
	 *
	 * @param name
	 *          - name of player
	 * @return Player with given name or null if Player with given name is not logged.
	 */
	public Player get(String name) {
		return playersByName.get(name);
	}

	@Override
	public Iterator<Player> iterator() {
		return playersById.values().iterator();
	}

	/**
	 * @param visitor
	 */
	public void doOnAllPlayers(Visitor<Player> visitor) {
		try {
			for (FastMap.Entry<Integer, Player> e = playersById.head(), mapEnd = playersById.tail(); (e = e.getNext()) != mapEnd;) {
				Player player = e.getValue();
				if (player != null) {
					visitor.visit(player);
				}
			}
		}
		catch (Exception ex) {
			log.error("Exception when running visitor on all players" + ex);
		}
	}

	public Collection<Player> getAllPlayers() {
		return playersById.values();
	}
}
