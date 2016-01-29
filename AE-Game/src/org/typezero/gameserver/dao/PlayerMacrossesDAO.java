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

import com.aionemu.commons.database.dao.DAO;
import org.typezero.gameserver.model.gameobjects.player.MacroList;

/**
 * Macrosses DAO
 * <p/>
 * Created on: 13.07.2009 17:05:56
 *
 * @author Aquanox
 */
public abstract class PlayerMacrossesDAO implements DAO {

	/**
	 * Returns unique identifier for PlayerMacroDAO
	 *
	 * @return unique identifier for PlayerMacroDAO
	 */
	@Override
	public final String getClassName() {
		return PlayerMacrossesDAO.class.getName();
	}

	/**
	 * Returns a list of macrosses for player
	 *
	 * @param playerId
	 *          Player object id.
	 * @return a list of macrosses for player
	 */
	public abstract MacroList restoreMacrosses(int playerId);

	/**
	 * Add a macro information into database
	 *
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          macro order # of player
	 * @param macro
	 *          macro contents.
	 */
	public abstract void addMacro(int playerId, int macroPosition, String macro);

	/**
	 * Update a macro information into database
	 *
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          macro order # of player
	 * @param macro
	 *          macro contents.
	 */
	public abstract void updateMacro(int playerId, int macroPosition, String macro);

	/**
	 * Remove macro in database
	 *
	 * @param playerId
	 *          player object id
	 * @param macroPosition
	 *          order of macro in macro list
	 */
	public abstract void deleteMacro(int playerId, int macroPosition);
}
