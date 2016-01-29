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

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerBindPointDAO;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.BindPointPosition;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author evilset
 */
public class MySQL5PlayerBindPointDAO extends PlayerBindPointDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerBindPointDAO.class);

	public static final String INSERT_QUERY = "REPLACE INTO `player_bind_point` (`player_id`, `map_id`, `x`, `y`, `z`, `heading`) VALUES (?,?,?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT `map_id`, `x`, `y`, `z`, `heading` FROM `player_bind_point` WHERE `player_id`=?";
	public static final String UPDATE_QUERY = "UPDATE player_bind_point set `map_id`=?, `x`=?, `y`=? , `z`=?, `heading`=? WHERE `player_id`=?";

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public void loadBindPoint(Player player) {

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				int mapId = rset.getInt("map_id");
				float x = rset.getFloat("x");
				float y = rset.getFloat("y");
				float z = rset.getFloat("z");
				byte heading = rset.getByte("heading");
				BindPointPosition bind = new BindPointPosition(mapId, x, y, z, heading);
				bind.setPersistentState(PersistentState.UPDATED);
				player.setBindPoint(bind);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore BindPointPosition data for playerObjId: " + player.getObjectId() + " from DB: "
					+ e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean insertBindPoint(Player player) {

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			BindPointPosition bpp = player.getBindPoint();
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, bpp.getMapId());
			stmt.setFloat(3, bpp.getX());
			stmt.setFloat(4, bpp.getY());
			stmt.setFloat(5, bpp.getZ());
			stmt.setByte(6, bpp.getHeading());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not store BindPointPosition data for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean updateBindPoint(Player player) {

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			BindPointPosition bpp = player.getBindPoint();
			stmt.setInt(1, bpp.getMapId());
			stmt.setFloat(2, bpp.getX());
			stmt.setFloat(3, bpp.getY());
			stmt.setFloat(4, bpp.getZ());
			stmt.setByte(5, bpp.getHeading());
			stmt.setFloat(6, player.getObjectId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not update BindPointPosition data for player " + player.getObjectId() + " from DB: " + e.getMessage(),
					e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean store(final Player player) {
		boolean insert = false;
		BindPointPosition bind = player.getBindPoint();

		switch (bind.getPersistentState()) {
			case NEW:
				insert = insertBindPoint(player);
				break;
			case UPDATE_REQUIRED:
				insert = updateBindPoint(player);
				break;
		}
		bind.setPersistentState(PersistentState.UPDATED);
		return insert;
	}
}
