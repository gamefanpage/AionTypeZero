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
import com.aionengine.loginserver.dao.AccountDAO;
import com.aionengine.loginserver.model.Account;
import com.aionengine.loginserver.model.AccountMembership;

/**
 * MySQL5 Account DAO implementation
 *
 * @author SoulKeeper
 * @author xTz
 * @author Dr2co
 */
public class MySQL5AccountDAO extends AccountDAO {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5AccountDAO.class);

	@Override
	public Account getAccount(String name) {
		Account account = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `name` = ?");

		try {
			st.setString(1, name);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				account = new Account();
				account.setId(rs.getInt("id"));
				account.setName(name);
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setLastMac(rs.getString("last_mac"));
				account.setIpForce(rs.getString("ip_force"));
				getMembership(account);
			}
		} catch (Exception e) {
			log.error("Can't select account with name: " + name, e);
		} finally {
			DB.close(st);
		}

		return account;
	}

	@Override
	public Account getAccount(int id) {
		Account account = null;
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `id` = ?");

		try {
			st.setInt(1, id);
			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				account = new Account();
				account.setId(rs.getInt("id"));
				account.setName(rs.getString("name"));
				account.setPasswordHash(rs.getString("password"));
				account.setAccessLevel(rs.getByte("access_level"));
				account.setActivated(rs.getByte("activated"));
				account.setLastServer(rs.getByte("last_server"));
				account.setLastIp(rs.getString("last_ip"));
				account.setLastMac(rs.getString("last_mac"));
				account.setIpForce(rs.getString("ip_force"));
				getMembership(account);
			}
		} catch (Exception e) {
			log.error("Can't select account with name: " + id, e);
		} finally {
			DB.close(st);
		}

		return account;
	}

	@Override
	public int getAccountId(String name) {
		int id = -1;
		PreparedStatement st = DB.prepareStatement("SELECT `id` FROM account_data WHERE `name` = ?");

		try {
			st.setString(1, name);
			ResultSet rs = st.executeQuery();
			rs.next();
			id = rs.getInt("id");
		} catch (SQLException e) {
			log.error("Can't select id after account insertion", e);
		} finally {
			DB.close(st);
		}

		return id;
	}

	@Override
	public int getAccountCount() {
		PreparedStatement st = DB.prepareStatement("SELECT count(*) AS c FROM account_data");
		ResultSet rs = DB.executeQuerry(st);

		try {
			rs.next();
			return rs.getInt("c");
		} catch (SQLException e) {
			log.error("Can't get account count", e);
		} finally {
			DB.close(st);
		}

		return -1;
	}

	@Override
	public boolean insertAccount(Account account) {
		int result = 0;
		PreparedStatement st = DB.prepareStatement("INSERT INTO account_data (`name`, `password`, access_level, activated, last_server, last_ip, last_mac, ip_force) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

		try {
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getActivated());
			st.setByte(5, account.getLastServer());
			st.setString(6, account.getLastIp());
			st.setString(7, account.getLastMac());
			st.setString(8, account.getIpForce());
			result = st.executeUpdate();
		} catch (SQLException e) {
			log.error("Can't inser account", e);
		} finally {
			DB.close(st);
		}

		if (result > 0) {
			account.setId(getAccountId(account.getName()));
		}

		return result > 0;
	}

	@Override
	public boolean updateAccount(Account account) {
		int result = 0;
		PreparedStatement st = DB.prepareStatement("UPDATE account_data SET `name` = ?, `password` = ?, access_level = ?, membership = ?, last_server = ?, last_ip = ?, last_mac = ?, ip_force = ? WHERE `id` = ?");

		try {
			st.setString(1, account.getName());
			st.setString(2, account.getPasswordHash());
			st.setByte(3, account.getAccessLevel());
			st.setByte(4, account.getLastServer());
			st.setString(5, account.getLastIp());
			st.setString(6, account.getLastMac());
			st.setString(7, account.getIpForce());
			st.setInt(8, account.getId());
			updateMembership(account);
			result = st.executeUpdate();
		} catch (SQLException e) {
			log.error("Can't update account");
		} finally {
			DB.close(st);
		}

		return result > 0;
	}

	@Override
	public boolean updateLastServer(final int accountId, final byte lastServer) {
		return DB.insertUpdate(
				"UPDATE account_data SET last_server = ? WHERE id = ?",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setByte(1, lastServer);
						preparedStatement.setInt(2, accountId);
						preparedStatement.execute();
					}
				});
	}

	@Override
	public boolean updateLastIp(final int accountId, final String ip) {
		return DB.insertUpdate(
				"UPDATE account_data SET last_ip = ? WHERE id = ?",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, ip);
						preparedStatement.setInt(2, accountId);
						preparedStatement.execute();
					}
				});
	}

	@Override
	public String getLastIp(final int accountId) {
		String lastIp = "";
		PreparedStatement st = DB.prepareStatement("SELECT `last_ip` FROM `account_data` WHERE `id` = ?");

		try {
			st.setInt(1, accountId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				lastIp = rs.getString("last_ip");
			}
		} catch (Exception e) {
			log.error("Can't select last IP of account ID: " + accountId, e);
			return "";
		} finally {
			DB.close(st);
		}

		return lastIp;
	}

	@Override
	public boolean updateLastMac(final int accountId, final String mac) {
		return DB.insertUpdate(
				"UPDATE `account_data` SET `last_mac` = ? WHERE `id` = ?",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setString(1, mac);
						preparedStatement.setInt(2, accountId);
						preparedStatement.execute();
					}
				});
	}

	@Override
	public void updateMembership(final int accountId) {
		DB.insertUpdate(
				"UPDATE account_data SET membership = old_membership, membership_expire = NULL WHERE id = ? and membership_expire < CURRENT_TIMESTAMP",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setInt(1, accountId);
						preparedStatement.execute();
					}
				});
		DB.insertUpdate(
				"UPDATE account_data SET apship = old_apship, apship_expire = NULL WHERE id = ? and apship_expire < CURRENT_TIMESTAMP",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setInt(1, accountId);
						preparedStatement.execute();
					}
				});
		DB.insertUpdate(
				"UPDATE account_data SET craftship = old_craftship, craftship_expire = NULL WHERE id = ? and craftship_expire < CURRENT_TIMESTAMP",
				new IUStH() {
					@Override
					public void handleInsertUpdate(
							PreparedStatement preparedStatement)
									throws SQLException {
						preparedStatement.setInt(1, accountId);
						preparedStatement.execute();
					}
				});
		DB.insertUpdate(
				"UPDATE account_data SET collectionship = old_collectionship, collectionship_expire = NULL WHERE id = ? and collectionship_expire < CURRENT_TIMESTAMP",
				new IUStH() {
					@Override
					public void handleInsertUpdate(PreparedStatement preparedStatement) throws SQLException {
						preparedStatement.setInt(1, accountId);
						preparedStatement.execute();
					}
				});
	}

	@Override
	public void deleteInactiveAccounts(int daysOfInactivity) {
		PreparedStatement statement = DB.prepareStatement("DELETE FROM account_data WHERE UNIX_TIMESTAMP(CURDATE()) - UNIX_TIMESTAMP(last_active) > ? * 24 * 60 * 60");
		try {
			statement.setInt(1, daysOfInactivity);
		} catch (SQLException e) {
			log.error("Some crap, can't set int parameter to PreparedStatement", e);
		}
		DB.executeUpdateAndClose(statement);
	}

	private void getMembership(Account account) {
		PreparedStatement st = DB.prepareStatement("SELECT * FROM account_data WHERE `id` = ?");

		try {
			st.setInt(1, account.getId());
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				AccountMembership membership = new AccountMembership();
				membership.setMembership(rs.getByte("membership"));
				membership.setMembershipExpire(rs.getDate("membership_expire"));
				membership.setCraftship(rs.getByte("craftship"));
				membership.setCraftshipExpire(rs.getDate("craftship_expire"));
				membership.setApship(rs.getByte("apship"));
				membership.setApshipExpire(rs.getDate("apship_expire"));
				membership.setCollectionship(rs.getByte("collectionship"));
				membership.setCollectionshipExpire(rs.getDate("collectionship_expire"));
				account.setMemberShip(membership);
			}
		} catch (Exception e) {
			log.error("Can't select account_data with name: " + account.getName(), e);
		} finally {
			DB.close(st);
		}
	}

	@Override
	public boolean updateMembership(Account account) {
		int result = 0;
		PreparedStatement st = DB.prepareStatement("UPDATE account_data SET `membership` = ?, `membership_expire` = ?, `craftship` = ?, `craftship_expire` = ?, `apship` = ?, `apship_expire` = ?, `collectionship` = ?, `collectionship_expire` = ? WHERE `id` = ?");

		try {
			st.setByte(1, account.getMembership().getMembership());
			st.setDate(2, account.getMembership().getMembershipExpire());
			st.setByte(3, account.getMembership().getCraftship());
			st.setDate(4, account.getMembership().getCraftshipExpire());
			st.setByte(5, account.getMembership().getApship());
			st.setDate(6, account.getMembership().getApshipExpire());
			st.setByte(7, account.getMembership().getCollectionship());
			st.setDate(8, account.getMembership().getCollectionshipExpire());
			st.setInt(9, account.getId());
			result = st.executeUpdate();
		} catch (SQLException e) {
			log.error("Can't update account_data");
		} finally {
			DB.close(st);
		}

		return result > 0;
	}

	@Override
	public boolean supports(String database, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(database, majorVersion, minorVersion);
	}

}
