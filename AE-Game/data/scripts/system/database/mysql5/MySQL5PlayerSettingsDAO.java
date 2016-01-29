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

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerSettingsDAO;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerSettings;

/**
 * @author ATracer
 */
public class MySQL5PlayerSettingsDAO extends PlayerSettingsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerSettingsDAO.class);

	/**
	 * TODO 1) analyze possibility to zip settings 2) insert/update instead of replace 0 - uisettings 1 - shortcuts 2 -
	 * display 3 - deny
	 */
	@Override
	public void loadSettings(final Player player) {
		final int playerId = player.getObjectId();
		final PlayerSettings playerSettings = new PlayerSettings();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT * FROM player_settings WHERE player_id = ?");
			statement.setInt(1, playerId);
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				int type = resultSet.getInt("settings_type");
				switch (type) {
					case 0:
						playerSettings.setUiSettings(resultSet.getBytes("settings"));
						break;
					case 1:
						playerSettings.setShortcuts(resultSet.getBytes("settings"));
						break;
					case 2:
						playerSettings.setHouseBuddies(resultSet.getBytes("settings"));
						break;
					case -1:
						playerSettings.setDisplay(resultSet.getInt("settings"));
						break;
					case -2:
						playerSettings.setDeny(resultSet.getInt("settings"));
						break;
				}
			}
			resultSet.close();
			statement.close();
		}
		catch (Exception e) {
			log.error("Could not restore PlayerSettings data for player " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		playerSettings.setPersistentState(PersistentState.UPDATED);
		player.setPlayerSettings(playerSettings);
	}

	@Override
	public void saveSettings(final Player player) {
		final int playerId = player.getObjectId();

		PlayerSettings playerSettings = player.getPlayerSettings();
		if (playerSettings.getPersistentState() == PersistentState.UPDATED)
			return;

		final byte[] uiSettings = playerSettings.getUiSettings();
		final byte[] shortcuts = playerSettings.getShortcuts();
		final byte[] houseBuddies = playerSettings.getHouseBuddies();
		final int display = playerSettings.getDisplay();
		final int deny = playerSettings.getDeny();

		if (uiSettings != null) {
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
					stmt.setInt(2, 0);
					stmt.setBytes(3, uiSettings);
					stmt.execute();
				}
			});
		}

		if (shortcuts != null) {
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
					stmt.setInt(2, 1);
					stmt.setBytes(3, shortcuts);
					stmt.execute();
				}
			});
		}

		if (houseBuddies != null) {
			DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {

				@Override
				public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, playerId);
					stmt.setInt(2, 2);
					stmt.setBytes(3, houseBuddies);
					stmt.execute();
				}
			});
		}

		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerId);
				stmt.setInt(2, -1);
				stmt.setInt(3, display);
				stmt.execute();
			}
		});

		DB.insertUpdate("REPLACE INTO player_settings values (?, ?, ?)", new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerId);
				stmt.setInt(2, -2);
				stmt.setInt(3, deny);
				stmt.execute();
			}
		});

	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
