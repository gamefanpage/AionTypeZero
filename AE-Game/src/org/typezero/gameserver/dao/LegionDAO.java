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

import java.sql.Timestamp;
import java.util.TreeMap;

import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.model.team.legion.LegionEmblem;
import org.typezero.gameserver.model.team.legion.LegionHistory;
import org.typezero.gameserver.model.team.legion.LegionWarehouse;

/**
 * Class that is responsible for storing/loading legion data
 *
 * @author Simple
 */

public abstract class LegionDAO implements IDFactoryAwareDAO {

	/**
	 * Returns true if name is used, false in other case
	 *
	 * @param name
	 *          name to check
	 * @return true if name is used, false in other case
	 */
	public abstract boolean isNameUsed(String name);

	/**
	 * Creates legion in DB
	 *
	 * @param legion
	 */
	public abstract boolean saveNewLegion(Legion legion);

	/**
	 * Stores legion to DB
	 *
	 * @param legion
	 */
	public abstract void storeLegion(Legion legion);

	/**
	 * Loads a legion
	 *
	 * @param legionName
	 * @return
	 */
	public abstract Legion loadLegion(String legionName);

	/**
	 * Loads a legion
	 *
	 * @param legionId
	 * @return Legion
	 */
	public abstract Legion loadLegion(int legionId);

	/**
	 * Removes legion and all related data (Done by CASCADE DELETION)
	 *
	 * @param legionId
	 *          legion to delete
	 */
	public abstract void deleteLegion(int legionId);

	/**
	 * Returns the announcement list of a legion
	 *
	 * @param legion
	 * @return announcementList
	 */
	public abstract TreeMap<Timestamp, String> loadAnnouncementList(int legionId);

	/**
	 * Creates announcement in DB
	 *
	 * @param legionId
	 * @param currentTime
	 * @param message
	 * @return true or false
	 */
	public abstract boolean saveNewAnnouncement(int legionId, Timestamp currentTime, String message);

	/**
	 * Identifier name for all LegionDAO classes
	 *
	 * @return LegionDAO.class.getName()
	 */
	@Override
	public final String getClassName() {
		return LegionDAO.class.getName();
	}

	/**
	 * Stores a legion emblem in the database
	 *
	 * @param legionId
	 * @param emblemId
	 * @param red
	 * @param green
	 * @param blue
	 */
	public abstract void storeLegionEmblem(int legionId, LegionEmblem legionEmblem);

	/**
	 * @param legionId
	 * @param key
	 * @return
	 */
	public abstract void removeAnnouncement(int legionId, Timestamp key);

	/**
	 * Loads a legion emblem
	 *
	 * @param legion
	 * @return LegionEmblem
	 */
	public abstract LegionEmblem loadLegionEmblem(int legionId);

	/**
	 * Loads the warehouse of legions
	 *
	 * @param legion
	 * @return Storage
	 */
	public abstract LegionWarehouse loadLegionStorage(Legion legion);

	/**
	 * @param legion
	 */
	public abstract void loadLegionHistory(Legion legion);

	/**
	 * @param legionId
	 * @param legionHistory
	 * @return true if query successful
	 */
	public abstract boolean saveNewLegionHistory(int legionId, LegionHistory legionHistory);
}
