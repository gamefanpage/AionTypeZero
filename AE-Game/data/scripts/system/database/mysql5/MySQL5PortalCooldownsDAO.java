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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.IUStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PortalCooldownsDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PortalCooldown;
import javolution.util.FastMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class MySQL5PortalCooldownsDAO extends PortalCooldownsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PortalCooldownsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `portal_cooldowns` (`player_id`, `world_id`, `reuse_time`, `count`) VALUES (?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `portal_cooldowns` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `world_id`, `reuse_time`, `count` FROM `portal_cooldowns` WHERE `player_id`=?";
    public static final String RESET_PORTAL_COOLDOWN_COUNT = "UPDATE `portal_cooldowns` SET `count` = ? WHERE `world_id` = ?";

    @Override
	public void loadPortalCooldowns(final Player player) {
		Connection con = null;
		FastMap<Integer, PortalCooldown> portalCoolDowns = new FastMap<Integer, PortalCooldown>();
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(SELECT_QUERY);

			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();

			while (rset.next()) {
				int worldId = rset.getInt("world_id");
				long reuseTime = rset.getLong("reuse_time");
                int count = rset.getInt("count");
				if (reuseTime > System.currentTimeMillis() || reuseTime == 0) {
					portalCoolDowns.put(worldId, new PortalCooldown(worldId, reuseTime, count));
				}
			}
			player.getPortalCooldownList().setPortalCoolDowns(portalCoolDowns);
			rset.close();
		} catch (SQLException e) {
			log.error("LoadPortalCooldowns", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}
	}

	@Override
	public void storePortalCooldowns(final Player player) {
		deletePortalCooldowns(player);
		Map<Integer, PortalCooldown> portalCoolDowns = player.getPortalCooldownList().getPortalCoolDowns();

		if (portalCoolDowns == null)
			return;

		for (Map.Entry<Integer, PortalCooldown> entry : portalCoolDowns.entrySet()) {
			final int worldId = entry.getKey();
			final long reuseTime = entry.getValue().getCooltime();
            final int count = entry.getValue().getCount();

			if (reuseTime < System.currentTimeMillis() && reuseTime != 0)
				continue;

			Connection con = null;

			PreparedStatement stmt = null;
			try {
				con = DatabaseFactory.getConnection();
				stmt = con.prepareStatement(INSERT_QUERY);

				stmt.setInt(1, player.getObjectId());
				stmt.setInt(2, worldId);
				stmt.setLong(3, reuseTime);
                stmt.setInt(4, count);
				stmt.execute();
			} catch (SQLException e) {
				log.error("storePortalCooldowns", e);
			} finally {
				DatabaseFactory.close(stmt, con);
			}
		}
	}

	private void deletePortalCooldowns(final Player player) {

		Connection con = null;
		PreparedStatement stmt = null;
		try {
			con = DatabaseFactory.getConnection();
			stmt = con.prepareStatement(DELETE_QUERY);

			stmt.setInt(1, player.getObjectId());
			stmt.execute();
		} catch (SQLException e) {
			log.error("deletePortalCooldowns", e);
		} finally {
			DatabaseFactory.close(stmt, con);
		}
	}

    @Override
    public void resetPortalCooldownCount(final int worldId, final int count) {
        DB.insertUpdate(RESET_PORTAL_COOLDOWN_COUNT, new IUStH() {

            @Override
            public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
                stmt.setInt(1, count);
                stmt.setInt(2, worldId);
                stmt.execute();
            }
        });
    }

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}
