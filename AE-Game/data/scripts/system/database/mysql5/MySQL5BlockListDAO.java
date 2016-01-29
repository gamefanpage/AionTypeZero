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
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.BlockListDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.BlockList;
import org.typezero.gameserver.model.gameobjects.player.BlockedPlayer;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;

/**
 * @author Ben
 */
public class MySQL5BlockListDAO extends BlockListDAO {

	public static final String LOAD_QUERY = "SELECT blocked_player, reason FROM blocks WHERE player=?";
	public static final String ADD_QUERY = "INSERT INTO blocks (player, blocked_player, reason) VALUES (?, ?, ?)";
	public static final String DEL_QUERY = "DELETE FROM blocks WHERE player=? AND blocked_player=?";
	public static final String SET_REASON_QUERY = "UPDATE blocks SET reason=? WHERE player=? AND blocked_player=?";
	private static Logger log = LoggerFactory.getLogger(MySQL5BlockListDAO.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addBlockedUser(final int playerObjId, final int objIdToBlock, final String reason) {
		return DB.insertUpdate(ADD_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerObjId);
				stmt.setInt(2, objIdToBlock);
				stmt.setString(3, reason);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean delBlockedUser(final int playerObjId, final int objIdToDelete) {
		return DB.insertUpdate(DEL_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, playerObjId);
				stmt.setInt(2, objIdToDelete);
				stmt.execute();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BlockList load(final Player player) {
		final Map<Integer, BlockedPlayer> list = new HashMap<Integer, BlockedPlayer>();

		DB.select(LOAD_QUERY, new ParamReadStH() {

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				PlayerDAO playerDao = DAOManager.getDAO(PlayerDAO.class);
				while (rset.next()) {
					int blockedOid = rset.getInt("blocked_player");
					PlayerCommonData pcd = playerDao.loadPlayerCommonData(blockedOid);
					if (pcd == null) {
						log.error("Attempt to load block list for " + player.getName()
							+ " tried to load a player which does not exist: " + blockedOid);
					}
					else {
						list.put(blockedOid, new BlockedPlayer(pcd, rset.getString("reason")));
					}
				}

			}

			@Override
			public void setParams(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, player.getObjectId());
			}
		});
		return new BlockList(list);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setReason(final int playerObjId, final int blockedPlayerObjId, final String reason) {
		return DB.insertUpdate(SET_REASON_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setString(1, reason);
				stmt.setInt(2, playerObjId);
				stmt.setInt(3, blockedPlayerObjId);
				stmt.execute();

			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}
}
