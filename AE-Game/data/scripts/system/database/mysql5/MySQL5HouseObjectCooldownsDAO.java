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
import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.HouseObjectCooldownsDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.model.gameobjects.player.Player;

/**
 * @author Rolandas
 */
public class MySQL5HouseObjectCooldownsDAO extends HouseObjectCooldownsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5CraftCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `house_object_cooldowns` (`player_id`, `object_id`, `reuse_time`) VALUES (?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `house_object_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `object_id`, `reuse_time` FROM `house_object_cooldowns` WHERE `player_id`=?";

	@Override
	public void loadHouseObjectCooldowns(final Player player) {
		Connection con = null;
		FastMap<Integer, Long> houseObjectCoolDowns = new FastMap<Integer, Long>();
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);

			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				int objectId = rset.getInt("object_id");
				long reuseTime = rset.getLong("reuse_time");
				int delay = (int) ((reuseTime - System.currentTimeMillis()) / 1000);

				if (delay > 0) {
					houseObjectCoolDowns.put(objectId, reuseTime);
				}
			}
			player.getHouseObjectCooldownList().setHouseObjectCooldowns(houseObjectCoolDowns);
			rset.close();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("LoadHouseObjectCooldowns", e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void storeHouseObjectCooldowns(final Player player) {
		deleteHouseObjectCoolDowns(player);
		Map<Integer, Long> houseObjectCoolDowns = player.getHouseObjectCooldownList().getHouseObjectCooldowns();

		if (houseObjectCoolDowns == null)
			return;

		for (Map.Entry<Integer, Long> entry : houseObjectCoolDowns.entrySet()) {
			final int templateId = entry.getKey();
			final long reuseTime = entry.getValue();

			if (reuseTime < System.currentTimeMillis())
				continue;

			Connection con = null;

			try {
				con = DatabaseFactory.getConnection();
				PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);

				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, templateId);
				stmt.setLong(3, reuseTime);
				stmt.execute();
			}
			catch (SQLException e) {
				log.error("storeHouseObjectCoolDowns", e);
			}
			finally {
				DatabaseFactory.close(con);
			}
		}
	}

	private void deleteHouseObjectCoolDowns(final Player player) {
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);

			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		}
		catch (SQLException e) {
			log.error("deleteHouseObjectCoolDowns", e);
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
