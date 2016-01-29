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

import javolution.util.FastList;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.RewardServiceDAO;
import org.typezero.gameserver.model.templates.rewards.RewardEntryItem;


/**
 *
 * @author KID
 *
 */
public class MySQL5RewardServiceDAO extends RewardServiceDAO {
	private static final Logger log = LoggerFactory.getLogger(MySQL5RewardServiceDAO.class);
	public static final String UPDATE_QUERY = "UPDATE `web_reward` SET `rewarded`=?, received=NOW() WHERE `unique`=?";
	public static final String SELECT_QUERY = "SELECT * FROM `web_reward` WHERE `item_owner`=? AND `rewarded`=?";

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}

	@Override
	public FastList<RewardEntryItem> getAvailable(int playerId) {
		FastList<RewardEntryItem> list = FastList.newInstance();

		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, 0);

			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int unique = rset.getInt("unique");
				int item_id = rset.getInt("item_id");
				long count = rset.getLong("item_count");
				list.add(new RewardEntryItem(unique, item_id, count));
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.warn("getAvailable() for " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}

		return list;
	}

	@Override
	public void uncheckAvailable(FastList<Integer> ids) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt;
			for(int uniqid : ids)
			{
				stmt = con.prepareStatement(UPDATE_QUERY);
				stmt.setInt(1, 1);
				stmt.setInt(2, uniqid);
				stmt.execute();
				stmt.close();
			}
		}
		catch (Exception e) {
			log.error("uncheckAvailable", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}
}
