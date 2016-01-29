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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionengine.loginserver.dao.AccountTimeDAO;
import com.aionengine.loginserver.model.AccountTime;

/**
 * MySQL5 AccountTimeDAO implementation
 *
 * @author EvilSpirit
 * @author Antraxx
 */
public class MySQL5AccountTimeDAO extends AccountTimeDAO {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5AccountTimeDAO.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean updateAccountTime(final int accountId, final AccountTime accountTime) {
		return DB.insertUpdate(
				"UPDATE account_data SET last_active = ?, expiration_time = ?, session_duration = ?, accumulated_online = ?, accumulated_rest = ?, penalty_end = ? WHERE id = ?",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setTimestamp(1, accountTime.getLastLoginTime());
						preparedStatement.setTimestamp(2, accountTime.getExpirationTime());
						preparedStatement.setLong(3, accountTime.getSessionDuration());
						preparedStatement.setLong(4, accountTime.getAccumulatedOnlineTime());
						preparedStatement.setLong(5, accountTime.getAccumulatedRestTime());
						preparedStatement.setTimestamp(6, accountTime.getPenaltyEnd());
						preparedStatement.setLong(7, accountId);
						preparedStatement.execute();
					}
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountTime getAccountTime(int accountId) {
		AccountTime accountTime = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE id = ?");

		try {
			st.setLong(1, accountId);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				accountTime = new AccountTime();
				accountTime.setLastLoginTime(rs.getTimestamp("last_active"));
				accountTime.setSessionDuration(rs.getLong("session_duration"));
				accountTime.setAccumulatedOnlineTime(rs.getLong("accumulated_online"));
				accountTime.setAccumulatedRestTime(rs.getLong("accumulated_rest"));
				accountTime.setPenaltyEnd(rs.getTimestamp("penalty_end"));
				accountTime.setExpirationTime(rs.getTimestamp("expiration_time"));
			}
		} catch (Exception e) {
			log.error("Can't get account time for account with id: " + accountId, e);
		} finally {
			DB.close(st);
		}

		return accountTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String database, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
	}

}
