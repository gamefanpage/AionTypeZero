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

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerEmotionListDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.emotion.Emotion;
import org.typezero.gameserver.model.gameobjects.player.emotion.EmotionList;

/**
 * @author Mr. Poke
 */
public class MySQL5PlayerEmotionListDAO extends PlayerEmotionListDAO {

	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(PlayerEmotionListDAO.class);
	public static final String INSERT_QUERY = "INSERT INTO `player_emotions` (`player_id`, `emotion`, `remaining`) VALUES (?,?,?)";
	public static final String SELECT_QUERY = "SELECT `emotion`, `remaining` FROM `player_emotions` WHERE `player_id`=?";
	public static final String DELETE_QUERY = "DELETE FROM `player_emotions` WHERE `player_id`=? AND `emotion`=?";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	@Override
	public void loadEmotions(Player player) {
		EmotionList emotions = new EmotionList(player);
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, player.getObjectId());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int emotionId = rset.getInt("emotion");
				int remaining = rset.getInt("remaining");
				emotions.add(emotionId, remaining, false);
			}
			rset.close();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not restore emotionId for playerObjId: " + player.getObjectId() + " from DB: " + e.getMessage(),e);
		}
		finally {
			DatabaseFactory.close(con);
		}
		player.setEmotions(emotions);
	}

	@Override
	public void insertEmotion(Player player, Emotion emotion) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, player.getObjectId());
			stmt.setInt(2, emotion.getId());
			stmt.setInt(3, emotion.getExpireTime());
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not store emotionId for player " + player.getObjectId() + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public void deleteEmotion(int playerId, int emotionId) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(DELETE_QUERY);
			stmt.setInt(1, playerId);
			stmt.setInt(2, emotionId);
			stmt.execute();
			stmt.close();
		}
		catch (Exception e) {
			log.error("Could not delete title for player " + playerId + " from DB: " + e.getMessage(), e);
		}
		finally {
			DatabaseFactory.close(con);
		}
	}
}
