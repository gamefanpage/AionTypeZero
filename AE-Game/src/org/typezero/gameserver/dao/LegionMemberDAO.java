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

import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.team.legion.LegionMemberEx;

import java.util.ArrayList;

/**
 * Class that is responsible for storing/loading legion data
 *
 * @author Simple
 */

public abstract class LegionMemberDAO implements IDFactoryAwareDAO {

	/**
	 * Returns true if name is used, false in other case
	 *
	 * @param name
	 *          name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isIdUsed(int playerObjId);

	/**
	 * Creates legion member in DB
	 *
	 * @param legionMember
	 */
	public abstract boolean saveNewLegionMember(LegionMember legionMember);

	/**
	 * Stores legion member to DB
	 *
	 * @param player
	 */
	public abstract void storeLegionMember(int playerObjId, LegionMember legionMember);

	/**
	 * Loads a legion member
	 *
	 * @param playerObjId
	 * @param legionService
	 * @return LegionMember
	 */
	public abstract LegionMember loadLegionMember(int playerObjId);

	/**
	 * Loads an off line legion member by id
	 *
	 * @param playerObjId
	 * @param legionService
	 * @return LegionMemberEx
	 */
	public abstract LegionMemberEx loadLegionMemberEx(int playerObjId);

	/**
	 * Loads an off line legion member by name
	 *
	 * @param playerName
	 * @param legionService
	 * @return LegionMemberEx
	 */
	public abstract LegionMemberEx loadLegionMemberEx(String playerName);

	/**
	 * Loads all legion members of a legion
	 *
	 * @param legionId
	 * @return ArrayList<Integer>
	 */
	public abstract ArrayList<Integer> loadLegionMembers(int legionId);

	/**
	 * Removes legion member and all related data (Done by CASCADE DELETION)
	 *
	 * @param playerId
	 *          legion member to delete
	 */
	public abstract void deleteLegionMember(int playerObjId);

	/**
	 * Identifier name for all LegionDAO classes
	 *
	 * @return LegionDAO.class.getName()
	 */
	@Override
	public final String getClassName() {
		return LegionMemberDAO.class.getName();
	}

}
