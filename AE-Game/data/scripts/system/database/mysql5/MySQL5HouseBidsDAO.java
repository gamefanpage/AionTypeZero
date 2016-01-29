package mysql5;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.HouseBidsDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.model.house.PlayerHouseBid;

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


/**
 * @author Rolandas
 */
public class MySQL5HouseBidsDAO extends HouseBidsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5HouseBidsDAO.class);

	public static final String LOAD_QUERY = "SELECT * FROM `house_bids`";
	public static final String INSERT_QUERY = "INSERT INTO `house_bids` (`player_id`,`house_id`, `bid`, `bid_time`) VALUES (?, ?, ?, ?)";
	public static final String DELETE_QUERY = "DELETE FROM `house_bids` WHERE `house_id` = ?";
	public static final String UPDATE_QUERY = "UPDATE `house_bids` SET bid = ?, bid_time = ? WHERE player_id = ? AND house_id = ?";

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public Set<PlayerHouseBid> loadBids() {
		Connection con = null;
		Set<PlayerHouseBid> results = new HashSet<PlayerHouseBid>();
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LOAD_QUERY);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int playerId = rset.getInt("player_id");
				int houseId = rset.getInt("house_id");
				long bidOffer = rset.getLong("bid");
				Timestamp time = rset.getTimestamp("bid_time");
				PlayerHouseBid bid = new PlayerHouseBid(playerId, houseId, bidOffer, time);
				results.add(bid);
			}
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot read house bids", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return results;
	}

	@Override
	public boolean addBid(int playerId, int houseId, long bidOffer, Timestamp time) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, houseId);
			stmt.setLong(3, bidOffer);
			stmt.setTimestamp(4, time);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot insert house bid", e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public void changeBid(int playerId, int houseId, long newBidOffer, Timestamp time) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setLong(1, newBidOffer);
			stmt.setTimestamp(2, time);
			stmt.setInt(3, playerId);
			stmt.setInt(4, houseId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot update house bid", e);
		}
		finally {
			DatabaseFactory.close(con);
		}

	}

	@Override
	public void deleteHouseBids(int houseId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, houseId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Cannot delete house bids", e);
		}
		finally {
			DatabaseFactory.close(con);
		}

	}

}
