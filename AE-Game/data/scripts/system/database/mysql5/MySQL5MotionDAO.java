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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MotionDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerEmotionListDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.motion.Motion;
import org.typezero.gameserver.model.gameobjects.player.motion.MotionList;

/**
 * @author MrPoke
 *
 */
public class MySQL5MotionDAO extends MotionDAO {

	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(PlayerEmotionListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_motions` (`player_id`, `motion_id`, `active`,  `time`) VALUES (?,?,?,?)";
	public static final String SELECT_QUERY = "SELECT `motion_id`, `active`, `time` FROM `player_motions` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_motions` WHERE `player_id`=? AND `motion_id`=?";
	public static final String UPDATE_QUERY = "UPDATE `player_motions` SET `active`=? WHERE `player_id`=? AND `motion_id`=?";

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}

	@Override
	public void loadMotionList(Player player) {
		Connection con = null;
		MotionList motions = new MotionList(player);
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int motionId = rset.getInt("motion_id");
				int time = rset.getInt("time");
				boolean isActive = rset.getBoolean("active");
				motions.add(new Motion(motionId, time, isActive), false);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore motions for playerObjId: " +player.getObjectId() + " from DB: " + e.getMessage(),
				e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		player.setMotions(motions);
	}

	@Override
	public boolean storeMotion(int objectId, Motion motion) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, motion.getId());
			stmt.setBoolean(3, motion.isActive());
			stmt.setInt(4, motion.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not store motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean deleteMotion(int objectId, int motionId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, objectId);
			stmt.setInt(2, motionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not delete motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}

	@Override
	public boolean updateMotion(int objectId, Motion motion) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);
			stmt.setBoolean(1, motion.isActive());
			stmt.setInt(2, objectId);
			stmt.setInt(3, motion.getId());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not store motion for player " + objectId + " from DB: " + e.getMessage(), e);
			return false;
		}
		finally {
			DatabaseFactory.close(con);
		}
		return true;
	}
}
