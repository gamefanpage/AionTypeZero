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

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.SiegeDAO;
import org.typezero.gameserver.model.siege.SiegeLocation;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sarynth
 */
public class MySQL5SiegeDAO extends SiegeDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5SiegeDAO.class);

	public static final String SELECT_QUERY = "SELECT `id`, `race`, `legion_id` FROM `siege_locations`";
	public static final String INSERT_QUERY = "INSERT INTO `siege_locations` (`id`, `race`, `legion_id`) VALUES(?, ?, ?)";
	public static final String UPDATE_QUERY = "UPDATE `siege_locations` SET  `race` = ?, `legion_id` = ? WHERE `id` = ?";

	@Override
	public boolean loadSiegeLocations(final Map<Integer, SiegeLocation> locations) {
		boolean success = true;
		Connection con = null;
		List<Integer> loaded = new ArrayList<Integer>();

		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				SiegeLocation loc = locations.get(resultSet.getInt("id"));
				loc.setRace(SiegeRace.valueOf(resultSet.getString("race")));
				loc.setLegionId(resultSet.getInt("legion_id"));
				loaded.add(loc.getLocationId());
			}
			resultSet.close();
		} catch (Exception e) {
			log.warn("Error loading Siege informaiton from database: " + e.getMessage(), e);
			success = false;
		} finally {
			DatabaseFactory.close(stmt, con);
		}

		for(Map.Entry<Integer, SiegeLocation> entry : locations.entrySet()){
			SiegeLocation sLoc = entry.getValue();
			if(!loaded.contains(sLoc.getLocationId())){
				insertSiegeLocation(sLoc);
			}
		}

		return success;
	}

	@Override
	public boolean updateSiegeLocation(final SiegeLocation siegeLocation) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setString(1, siegeLocation.getRace().toString());
			stmt.setInt(2, siegeLocation.getLegionId());
			stmt.setInt(3, siegeLocation.getLocationId());
			stmt.execute();
		} catch (Exception e) {
			log.error("Error update Siege Location: " + siegeLocation.getLocationId() + " to race: "
					+ siegeLocation.getRace().toString(), e);
			return false;
		} finally {
			DatabaseFactory.close(stmt, con);
		}
		return true;
	}

	/**
	 * @param siegeLocation
	 * @return success
	 */
	private boolean insertSiegeLocation(final SiegeLocation siegeLocation) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, siegeLocation.getLocationId());
			stmt.setString(2, siegeLocation.getRace().toString());
			stmt.setInt(3, siegeLocation.getLegionId());
			stmt.execute();
		} catch (Exception e) {
			log.error("Error insert Siege Location: " + siegeLocation.getLocationId(), e);
			return false;
		} finally {
			DatabaseFactory.close(stmt, con);

		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
