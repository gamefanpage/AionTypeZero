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

import com.aionemu.commons.database.DB;
import com.aionemu.commons.database.IUStH;
import com.aionemu.commons.database.ParamReadStH;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dao.PlayerPunishmentsDAO;
import org.typezero.gameserver.model.account.CharacterBanInfo;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.PunishmentService.PunishmentType;

/**
 * @author lord_rex, Cura, nrg
 */
public class MySQL5PlayerPunishmentsDAO extends PlayerPunishmentsDAO {

	public static final String SELECT_QUERY = "SELECT `player_id`, `start_time`, `duration`, `reason` FROM `player_punishments` WHERE `player_id`=? AND `punishment_type`=?";
	public static final String UPDATE_QUERY = "UPDATE `player_punishments` SET `duration`=? WHERE `player_id`=? AND `punishment_type`=?";
	public static final String REPLACE_QUERY = "REPLACE INTO `player_punishments` VALUES (?,?,?,?,?)";
	public static final String DELETE_QUERY = "DELETE FROM `player_punishments` WHERE `player_id`=? AND `punishment_type`=?";

	@Override
	public void loadPlayerPunishments(final Player player, final PunishmentType punishmentType) {
		DB.select(SELECT_QUERY, new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setInt(1, player.getObjectId());
				ps.setString(2, punishmentType.toString());
			}

			@Override
			public void handleRead(ResultSet rs) throws SQLException {
				while (rs.next()) {
					if (punishmentType == PunishmentType.PRISON) {
						player.setPrisonTimer(rs.getLong("duration")*1000);
					}
					else if (punishmentType == PunishmentType.GATHER) {
						player.setGatherableTimer(rs.getLong("duration")*1000);
					}
				}
			}
		});
	}

	@Override
	public void storePlayerPunishments(final Player player, final PunishmentType punishmentType) {
		DB.insertUpdate(UPDATE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				if (punishmentType == PunishmentType.PRISON) {
					ps.setLong(1, player.getPrisonTimer()/1000);
				}
				else if (punishmentType == PunishmentType.GATHER) {
					ps.setLong(1, (player.getGatherableTimer() - (System.currentTimeMillis() - player.getStopGatherable()))/1000);
				}
				ps.setInt(2, player.getObjectId());
				ps.setString(3, punishmentType.toString());
				ps.execute();
			}
		});
	}

	@Override
	public void punishPlayer(final int playerId, final PunishmentType punishmentType, final long duration, final String reason) {
		DB.insertUpdate(REPLACE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
				ps.setString(2, punishmentType.toString());
				ps.setLong(3, System.currentTimeMillis()/1000);
				ps.setLong(4, duration);
				ps.setString(5, reason);
				ps.execute();
			}
		});
	}

	@Override
	public void punishPlayer(final Player player, final PunishmentType punishmentType, final String reason) {
		if(punishmentType == PunishmentType.PRISON)
			punishPlayer(player.getObjectId(), punishmentType, player.getPrisonTimer()/1000, reason);
		else if(punishmentType == PunishmentType.GATHER)
			punishPlayer(player.getObjectId(), punishmentType, player.getGatherableTimer()/1000, reason);
	}

	@Override
	public void unpunishPlayer(final int playerId, final PunishmentType punishmentType) {
		DB.insertUpdate(DELETE_QUERY, new IUStH() {

			@Override
			public void handleInsertUpdate(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
				ps.setString(2, punishmentType.toString());
				ps.execute();
			}
		});
	}

	@Override
	public CharacterBanInfo getCharBanInfo(final int playerId) {
		final CharacterBanInfo[] charBan = new CharacterBanInfo[1];
		DB.select(SELECT_QUERY, new ParamReadStH() {

			@Override
			public void setParams(PreparedStatement ps) throws SQLException {
				ps.setInt(1, playerId);
				ps.setString(2, PunishmentType.CHARBAN.toString());
			}

			@Override
			public void handleRead(ResultSet rs) throws SQLException {
				while (rs.next()) {
					charBan[0] = new CharacterBanInfo(playerId, rs.getLong("start_time"), rs.getLong("duration"), rs.getString("reason"));
				}
			}
		});
		return charBan[0];
	}

	@Override
	public boolean supports(String s, int i, int i1) {
		return MySQL5DAOUtils.supports(s, i, i1);
	}
}
