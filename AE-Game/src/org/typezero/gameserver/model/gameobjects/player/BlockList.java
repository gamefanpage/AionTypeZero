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

import com.aionemu.commons.utils.internal.chmv8.PlatformDependent;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a players list of blocked users<br />
 * Blocks via a player's CommonData
 *
 * @author Ben
 */
public class BlockList implements Iterable<BlockedPlayer> {

	/**
	 * The maximum number of users a block list can contain
	 */
	public static final int MAX_BLOCKS = 10;

	// Indexes blocked players by their player ID
	private final Map<Integer, BlockedPlayer> blockedList;

	/**
	 * Constructs a new (empty) blocked list
	 */
	public BlockList() {
		this.blockedList = PlatformDependent.newConcurrentHashMap();
	}

	/**
	 * Constructs a new blocked list with the given initial items
	 *
	 * @param initialList
	 *          A map of blocked players indexed by their object IDs
	 */
	public BlockList(Map<Integer, BlockedPlayer> initialList) {
		this.blockedList = PlatformDependent.newConcurrentHashMap(initialList);

	}

	/**
	 * Adds a player to the blocked users list<br />
	 * <ul>
	 * <li>Does not send packets or update the database</li>
	 * </ul>
	 *
	 * @param playerToBlock
	 *          The player to be blocked
	 * @param reason
	 *          The reason for blocking this user
	 */
	public void add(BlockedPlayer plr) {
		blockedList.put(plr.getObjId(), plr);
	}

	/**
	 * Removes a player from the blocked users list<br />
	 * <ul>
	 * <li>Does not send packets or update the database</li>
	 * </ul>
	 *
	 * @param objIdOfPlayer
	 */
	public void remove(int objIdOfPlayer) {
		blockedList.remove(objIdOfPlayer);
	}

	/**
	 * Returns the blocked player with this name if they exist
	 *
	 * @param name
	 * @return CommonData of player with this name, null if not blocked
	 */
	public BlockedPlayer getBlockedPlayer(String name) {
		Iterator<BlockedPlayer> iterator = blockedList.values().iterator();

		while (iterator.hasNext()) {
			BlockedPlayer entry = iterator.next();
			if (entry.getName().equalsIgnoreCase(name))
				return entry;
		}
		return null;
	}

	public BlockedPlayer getBlockedPlayer(int playerObjId) {
		return blockedList.get(playerObjId);
	}

	public boolean contains(int playerObjectId) {
		return blockedList.containsKey(playerObjectId);
	}

	/**
	 * Returns the number of blocked players in this list
	 *
	 * @return blockedList.size()
	 */
	public int getSize() {
		return blockedList.size();
	}

	public boolean isFull() {
		return getSize() >= MAX_BLOCKS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<BlockedPlayer> iterator() {
		return blockedList.values().iterator();
	}

}
