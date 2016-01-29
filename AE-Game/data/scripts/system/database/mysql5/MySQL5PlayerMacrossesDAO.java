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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerMacrossesDAO;
import org.typezero.gameserver.model.gameobjects.player.MacroList;

/**
 * @author Aquanox
 */
public class MySQL5PlayerMacrossesDAO extends PlayerMacrossesDAO {

	private static Logger log = LoggerFactory.getLogger(MySQL5PlayerMacrossesDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_macrosses` (`player_id`, `order`, `macro`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_macrosses` SET `macro`=? WHERE `player_id`=? AND `order`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_macrosses` WHERE `player_id`=? AND `order`=?";
	public static final String SELECT_QUERY = "SELECT `order`, `macro` FROM `player_macrosses` WHERE `player_id`=?";

	/**
	 * Add a macro information into database
	 *
	 * @param playerId
	 *          player object id
	 * @param macro
	 *          macro contents.
	 */
	@Override
	public void addMacro(final int playerId, final int macroPosition, final String macro) {
		DB.insertUpdate(INSERT_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				log.debug("[DAO: MySQL5PlayerMacrossesDAO] storing macro " + playerId + " " + macroPosition);
				stmt.setInt(1, playerId);
				stmt.setInt(2, macroPosition);
				stmt.setString(3, macro);
				stmt.execute();
			}
		});
	}

	@Override
	public void updateMacro(final int playerId, final int macroPosition, final String macro) {
		DB.insertUpdate(UPDATE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				log.debug("[DAO: MySQL5PlayerMacrossesDAO] updating macro " + playerId + " " + macroPosition);
				stmt.setString(1, macro);
				stmt.setInt(2, playerId);
				stmt.setInt(3, macroPosition);
				stmt.execute();
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public void deleteMacro(final int playerId, final int macroPosition) {
		DB.insertUpdate(DELETE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				log.debug("[DAO: MySQL5PlayerMacrossesDAO] removing macro " + playerId + " " + macroPosition);
				stmt.setInt(1, playerId);
				stmt.setInt(2, macroPosition);
				stmt.execute();
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public MacroList restoreMacrosses(final int playerId) {
		final Map<Integer, String> macrosses = new HashMap<Integer, String>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			ResultSet rset = stmt.executeQuery();
			log.debug("[DAO: MySQL5PlayerMacrossesDAO] loading macroses for playerId: " + playerId);
			while (rset.next()) {
				int order = rset.getInt("order");
				String text = rset.getString("macro");
				macrosses.put(order, text);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore MacroList data for player " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return new MacroList(macrosses);
	}

	/** {@inheritDoc} */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
