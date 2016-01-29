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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.FriendListDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Friend;
import org.typezero.gameserver.model.gameobjects.player.FriendList;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * @author Ben
 */
public class MySQL5FriendListDAO extends FriendListDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5FriendListDAO.class);
	public static final String LOAD_QUERY = "SELECT * FROM `friends` WHERE `player`=?";
	public static final String ADD_QUERY = "INSERT INTO `friends` (`player`,`friend`) VALUES (?, ?)";
	public static final String DEL_QUERY = "DELETE FROM friends WHERE player = ? AND friend = ?";

	@Override
	public FriendList load(final Player player) {
		final List<Friend> friends = new ArrayList<Friend>();
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(LOAD_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			PlayerDAO dao = DAOManager.getDAO(PlayerDAO.class);
			while (rset.next()) {
				int objId = rset.getInt("friend");

				PlayerCommonData pcd = dao.loadPlayerCommonData(objId);
				if (pcd != null) {
					Friend friend = new Friend(pcd);
					friends.add(friend);
				}
			}
		}
		catch (Exception e) {
			log.error("Could not restore QuestStateList data for player: " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return new FriendList(player, friends);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addFriends(final Player player, final Player friend) {
		return DB.insertUpdate(ADD_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, friend.getObjectId());
				ps.addBatch();

				ps.setInt(1, friend.getObjectId());
				ps.setInt(2, player.getObjectId());
				ps.addBatch();

				ps.executeBatch();
			}
		});

	}

	@Override
	public boolean delFriends(final int playerOid, final int friendOid) {
		return DB.insertUpdate(DEL_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerOid);
				ps.setInt(2, friendOid);
				ps.addBatch();

				ps.setInt(1, friendOid);
				ps.setInt(2, playerOid);
				ps.addBatch();

				ps.executeBatch();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
