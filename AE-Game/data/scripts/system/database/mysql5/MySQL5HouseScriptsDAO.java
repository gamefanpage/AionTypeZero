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
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.HouseScriptsDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.model.gameobjects.player.PlayerScripts;

/**
 * @author Rolandas
 */
public class MySQL5HouseScriptsDAO extends HouseScriptsDAO {

	private static Logger log = LoggerFactory.getLogger(MySQL5HouseScriptsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `house_scripts` (`house_id`,`index`,`script`) VALUES (?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE `house_scripts` SET `script`=? WHERE `house_id`=? AND `index`=?";
	public static final String DELETE_QUERY = "DELETE FROM `house_scripts` WHERE `house_id`=? AND `index`=?";
	private static final String SELECT_QUERY = "SELECT `index`,`script` FROM `house_scripts` WHERE `house_id`=?";

	@Override
	public void addScript(int houseId, int position, String scriptXML) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, houseId);
			stmt.setInt(2, position);
			if (scriptXML == null)
				stmt.setNull(3, Types.LONGNVARCHAR);
			else
				stmt.setString(3, scriptXML);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not save script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public PlayerScripts getPlayerScripts(int houseId) {
		Connection con = null;
		PlayerScripts scripts = new PlayerScripts(houseId);
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, houseId);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int position = rset.getInt("index");
				String scriptXML = rset.getString("script");
				scripts.addScript(position, scriptXML);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return scripts;
	}

	@Override
	public void updateScript(int houseId, int position, String scriptXML) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			if (scriptXML == null)
				stmt.setNull(1, Types.LONGNVARCHAR);
			else
				stmt.setString(1, scriptXML);
			stmt.setInt(2, houseId);
			stmt.setInt(3, position);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not save script data for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void deleteScript(int houseId, int position) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, houseId);
			stmt.setInt(2, position);
			stmt.executeUpdate();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not delete script for houseId: " + houseId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
