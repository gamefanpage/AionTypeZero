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

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerPasskeyDAO;

/**
 * @author cura
 */
public class MySQL5PlayerPasskeyDAO extends PlayerPasskeyDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerPasskeyDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_passkey` (`account_id`, `passkey`) VALUES (?,?)";
	public static final String UPDATE_QUERY = "UPDATE `player_passkey` SET `passkey`=? WHERE `account_id`=? AND `passkey`=?";
	public static final String UPDATE_FORCE_QUERY = "UPDATE `player_passkey` SET `passkey`=? WHERE `account_id`=?";
	public static final String CHECK_QUERY = "SELECT COUNT(*) cnt FROM `player_passkey` WHERE `account_id`=? AND `passkey`=?";
	public static final String EXIST_CHECK_QUERY = "SELECT COUNT(*) cnt FROM `player_passkey` WHERE `account_id`=?";

	@Override
	public void insertPlayerPasskey(int accountId, String passkey) {
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);

			stmt.setInt(1, accountId);
			stmt.setString(2, passkey);

			stmt.execute();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error saving PlayerPasskey. accountId: " + accountId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean updatePlayerPasskey(int accountId, String oldPasskey, String newPasskey) {
		boolean result = false;
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);

			stmt.setString(1, newPasskey);
			stmt.setInt(2, accountId);
			stmt.setString(3, oldPasskey);

			if (stmt.executeUpdate() > 0)
				result = true;
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error updating PlayerPasskey. accountId: " + accountId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return result;
	}

	@Override
	public boolean updateForcePlayerPasskey(int accountId, String newPasskey) {
		boolean result = false;
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_FORCE_QUERY);

			stmt.setString(1, newPasskey);
			stmt.setInt(2, accountId);

			if (stmt.executeUpdate() > 0)
				result = true;
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error updaing PlayerPasskey. accountId: " + accountId, e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return result;
	}

	@Override
	public boolean checkPlayerPasskey(int accountId, String passkey) {
		boolean passkeyChecked = false;
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(CHECK_QUERY);

			stmt.setInt(1, accountId);
			stmt.setString(2, passkey);

			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				if (rset.getInt("cnt") == 1)
					passkeyChecked = true;
			}

			rset.close();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error loading PlayerPasskey. accountId: " + accountId, e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return passkeyChecked;
	}

	@Override
	public boolean existCheckPlayerPasskey(int accountId) {
		boolean existPasskeyChecked = false;
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(EXIST_CHECK_QUERY);

			stmt.setInt(1, accountId);

			ResultSet rset = stmt.executeQuery();
			if (rset.next()) {
				if (rset.getInt("cnt") == 1)
					existPasskeyChecked = true;
			}

			rset.close();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error loading PlayerPasskey. accountId: " + accountId, e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}

		return existPasskeyChecked;
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
