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
import com.aionemu.commons.database.ParamReadStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerEffectsDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.skillengine.model.Effect;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * @author ATracer
 */
public class MySQL5PlayerEffectsDAO extends PlayerEffectsDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5PlayerEffectsDAO.class);

	public static final String INSERT_QUERY = "INSERT INTO `player_effects` (`player_id`, `skill_id`, `skill_lvl`, `current_time`, `end_time`) VALUES (?,?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_effects` WHERE `player_id`=?";
	public static final String SELECT_QUERY = "SELECT `skill_id`, `skill_lvl`, `current_time`, `end_time` FROM `player_effects` WHERE `player_id`=?";

	private static final Predicate<Effect> insertableEffectsPredicate = new Predicate<Effect>() {
		@Override
		public boolean apply(@Nullable Effect input) {
			return input != null && input.getRemainingTime() > 28000;
		}
	};

	@Override
	public void loadPlayerEffects(final Player player) {
		DB.select(SELECT_QUERY, new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, player.getObjectId());
			}

			@Override
			public void handleRead(ResultSet rset) throws SQLException {
				while (rset.next()) {
					int skillId = rset.getInt("skill_id");
					int skillLvl = rset.getInt("skill_lvl");
					int remainingTime = rset.getInt("current_time");
					long endTime = rset.getLong("end_time");

					if (remainingTime > 0)
						player.getEffectController().addSavedEffect(skillId, skillLvl, remainingTime, endTime);
				}
			}
		});
		player.getEffectController().broadCastEffects();
	}

	@Override
	public void storePlayerEffects(final Player player) {
		deletePlayerEffects(player);

		Iterator<Effect> iterator = player.getEffectController().iterator();
		iterator = Iterators.filter(iterator, insertableEffectsPredicate);

		if(!iterator.hasNext()){
			return;
		}

		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = DatabaseFactory.getConnection();
			con.setAutoCommit(false);
			ps = con.prepareStatement(INSERT_QUERY);

			while (iterator.hasNext()) {
				Effect effect = iterator.next();
				ps.setInt(1, player.getObjectId());
				ps.setInt(2, effect.getSkillId());
				ps.setInt(3, effect.getSkillLevel());
				ps.setInt(4, effect.getRemainingTime());
				ps.setLong(5, effect.getEndTime());
				ps.addBatch();
			}

			ps.executeBatch();
			con.commit();
		} catch (SQLException e) {
			log.error("Exception while saving effects of player " + player.getObjectId(), e);
		} finally {
			DatabaseFactory.close(ps, con);
		}
	}

	private void deletePlayerEffects(final Player player) {
		DB.insertUpdate(DELETE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement stmt) throws SQLException {
				stmt.setInt(1, player.getObjectId());
				stmt.execute();
			}
		});
	}

	@Override
	public boolean supports(String arg0, int arg1, int arg2) {
		return MySQL5DAOUtils.supports(arg0, arg1, arg2);
	}
}