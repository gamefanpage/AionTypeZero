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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.WeddingDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author synchro2
 */

public class MySQL5WeddingDAO extends WeddingDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PortalCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `weddings` (`player1`, `player2`) VALUES (?,?)";
	public static final String SELECT_QUERY = "SELECT `player1`, `player2` FROM `weddings` WHERE `player1`=? OR `player2`=?";
    public static final String SELECT_NAME_QUERY = "SELECT `name` FROM `players` WHERE `id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `weddings` WHERE (`player1`=? AND `player2`=?) OR (`player2`=? AND `player1`=?)";
	public static final String DELETE_QUERY_SINGLE = "DELETE FROM `weddings` WHERE (`player1`=? OR `player2`=?)";

	@Override
	public int loadPartnerId(final Player player) {
		Connection con = null;
		int playerId = player.getObjectId();
		int partnerId = 0;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, playerId);
			ResultSet rset = stmt.executeQuery();
			int partner1Id = 0;
			int partner2Id = 0;
			if (rset.next()) {
				partner1Id = rset.getInt("player1");
				partner2Id = rset.getInt("player2");
			}
			partnerId = playerId == partner1Id ? partner2Id : partner1Id;
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not get partner for player: " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		return partnerId;
	}
    @Override
    public String loadPartnerName(final int partnerId) {
        Connection con = null;
        String partnerName = "";
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(SELECT_NAME_QUERY);
            stmt.setInt(1, partnerId);
            ResultSet rset = stmt.executeQuery();
            if (rset.next()) {
                partnerName = rset.getString("name");
            }
            rset.close();
            stmt.close();
        }
        catch (Exception e) {
            log.error("Could not get partner name for Id: " + partnerId + " from DB: " + e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
        return partnerName;
	}

	@Override
	public void storeWedding(final Player partner1, final Player partner2) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(INSERT_QUERY);

			stmt.setInt(1, partner1.getObjectId());
			stmt.setInt(2, partner2.getObjectId());
			stmt.execute();
		}
		catch (SQLException e) {
			log.error("storeWeddings", e);
		}
		finally {
			DatabaseFactory.close(stmt, con);
		}
	}

	@Override
	public void deleteWedding(final Player partner1, final Player partner2) {
		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY);

			stmt.setInt(1, partner1.getObjectId());
			stmt.setInt(2, partner2.getObjectId());
			stmt.setInt(3, partner1.getObjectId());
			stmt.setInt(4, partner2.getObjectId());
			stmt.execute();
		}
		catch (SQLException e) {
			log.error("deleteWedding", e);
		}
		finally {
			DatabaseFactory.close(stmt, con);
		}
	}

    @Override
    public void deleteWeddingSingle(final Player partner1) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(DELETE_QUERY_SINGLE);

            stmt.setInt(1, partner1.getObjectId());
            stmt.setInt(2, partner1.getObjectId());
            stmt.execute();
        }
        catch (SQLException e) {
            log.error("deleteWedding", e);
        }
        finally {
            DatabaseFactory.close(stmt, con);
        }
    }

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}
