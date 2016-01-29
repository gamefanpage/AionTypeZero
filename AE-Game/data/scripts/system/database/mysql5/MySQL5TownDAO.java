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

package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.TownDAO;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.town.Town;


/**
 * @author ViAl
 *
 */
public class MySQL5TownDAO extends TownDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5TownDAO.class);
	private static final String SELECT_QUERY = "SELECT * FROM `towns` WHERE `race` = ?";
	private static final String INSERT_QUERY = "INSERT INTO `towns`(`id`,`level`,`points`, `race`) VALUES (?,?,?,?)";
	private static final String UPDATE_QUERY = "UPDATE `towns` SET `level` = ?, `points` = ?, `level_up_date` = ? WHERE `id` = ?";

	@Override
	public Map<Integer, Town> load(Race race) {
		Map<Integer, Town> towns = new HashMap<Integer, Town>();
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
			stmt.setString(1, race.toString());
			ResultSet rset = stmt.executeQuery();
			while(rset.next()) {
				int id = rset.getInt("id");
				int level = rset.getInt("level");
				int points = rset.getInt("points");
				Timestamp levelUpDate = rset.getTimestamp("level_up_date");
				Town town = new Town(id, level, points, race, levelUpDate);
				towns.put(town.getId(), town);
			}
			rset.close();
			stmt.close();
		}
		catch(SQLException e) {
			log.error("Can't load towns info. "+e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
		return towns;
	}

	@Override
	public void store(Town town) {
		switch(town.getPersistentState()) {
			case NEW:
				insertTown(town);
				break;
			case UPDATE_REQUIRED:
				updateTown(town);
				break;
		}
	}

	private void insertTown(Town town) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, town.getId());
			stmt.setInt(2, town.getLevel());
			stmt.setInt(3, town.getPoints());
			stmt.setString(4, town.getRace().toString());
			stmt.executeUpdate();
			stmt.close();
			town.setPersistentState(PersistentState.UPDATED);
		}
		catch(SQLException e) {
			log.error("Can insert new town into database! Town id:"+town.getId()+". "+e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	private void updateTown(Town town) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, town.getLevel());
			stmt.setInt(2, town.getPoints());
			stmt.setTimestamp(3, town.getLevelUpDate());
			stmt.setInt(4, town.getId());
			stmt.executeUpdate();
			stmt.close();
			town.setPersistentState(PersistentState.UPDATED);
		}
		catch(SQLException e) {
			log.error("Can insert new town into database! Town id:"+town.getId()+". "+e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
