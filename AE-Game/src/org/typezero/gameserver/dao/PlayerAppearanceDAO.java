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
package org.typezero.gameserver.dao;

//~--- non-JDK imports --------------------------------------------------------

import com.aionemu.commons.database.dao.DAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;

/**
 * Class that is responsible for loading/storing player appearance
 *
 * @author SoulKeeper
 */
public abstract class PlayerAppearanceDAO implements DAO {

	/**
	 * Returns unique identifier for PlayerAppearanceDAO
	 *
	 * @return unique identifier for PlayerAppearanceDAO
	 */
	@Override
	public final String getClassName() {
		return PlayerAppearanceDAO.class.getName();
	}

	/**
	 * Loads player apperance DAO by player ID.<br>
	 * Returns null if not found in database
	 *
	 * @param playerId
	 *          player id
	 * @return player appearance or null
	 */
	public abstract PlayerAppearance load(int playerId);

	/**
	 * Saves player appearance in database.<br>
	 * Actually calls {@link #store(int, org.typezero.gameserver.model.gameobjects.player.PlayerAppearance)}
	 *
	 * @param player
	 *          whos appearance to store
	 * @return true, if sql query was successful, false overwise
	 */
	public final boolean store(Player player) {
		return store(player.getObjectId(), player.getPlayerAppearance());
	}

	/**
	 * Stores appearance in database
	 *
	 * @param id
	 *          player id
	 * @param playerAppearance
	 *          player appearance
	 * @return true, if sql query was successful, false overwise
	 */
	public abstract boolean store(int id, PlayerAppearance playerAppearance);
}
