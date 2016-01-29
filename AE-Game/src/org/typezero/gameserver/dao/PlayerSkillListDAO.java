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
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.skill.PlayerSkillList;

/**
 * Created on: 15.07.2009 19:33:07 Edited On: 13.09.2009 19:48:00
 *
 * @author IceReaper, orfeo087, Avol, AEJTester
 */
public abstract class PlayerSkillListDAO implements DAO {

	/**
	 * Returns unique identifier for PlayerSkillListDAO
	 *
	 * @return unique identifier for PlayerSkillListDAO
	 */
	@Override
	public final String getClassName() {
		return PlayerSkillListDAO.class.getName();
	}

	/**
	 * Returns a list of skilllist for player
	 *
	 * @param playerId
	 *          Player object id.
	 * @return a list of skilllist for player
	 */
	public abstract PlayerSkillList loadSkillList(int playerId);

	/**
	 * Updates skill with new information
	 *
	 * @param playerId
	 * @param skillId
	 * @param skillLevel
	 */
	public abstract boolean storeSkills(Player player);

}
