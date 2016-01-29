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
import com.aionemu.commons.database.ParamReadStH;
import org.typezero.gameserver.configs.main.RankingConfig;
import org.typezero.gameserver.dao.AbyssRankDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.AbyssRankingResult;
import org.typezero.gameserver.model.Gender;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.gameobjects.player.AbyssRank;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.abyss.AGPoint;
import org.typezero.gameserver.utils.stats.AbyssRankEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * @author ATracer, Divinity, nrg
 */
public class MySQL5AbyssRankDAO extends AbyssRankDAO {

	/**
	 * Logger for this class.
	 */
	private static final Logger log = LoggerFactory.getLogger(MySQL5AbyssRankDAO.class);
	public static final String SELECT_QUERY = "SELECT daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp, last_update FROM abyss_rank WHERE player_id = ?";
	public static final String INSERT_QUERY = "INSERT INTO abyss_rank (player_id, daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp, last_update) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	public static final String UPDATE_QUERY = "UPDATE abyss_rank SET  daily_ap = ?, daily_gp = ?, weekly_ap = ?, weekly_gp = ?, ap = ?, gp = ?, rank = ?, top_ranking = ?, daily_kill = ?, weekly_kill = ?, all_kill = ?, max_rank = ?, last_kill = ?, last_ap = ?, last_gp = ?, last_update = ? WHERE player_id = ?";
	public static final String SELECT_PLAYERS_RANKING = "SELECT abyss_rank.rank, abyss_rank.ap, abyss_rank.gp, abyss_rank.old_rank_pos, abyss_rank.rank_pos, players.name, legions.name, players.id, players.title_id, players.player_class, players.exp, players.gender FROM abyss_rank INNER JOIN players ON abyss_rank.player_id = players.id LEFT JOIN legion_members ON legion_members.player_id = players.id LEFT JOIN legions ON legions.id = legion_members.legion_id WHERE players.race = ? AND abyss_rank.gp > ? ORDER BY abyss_rank.gp DESC LIMIT 0, 300";
	public static final String SELECT_PLAYERS_RANKING_ACTIVE_ONLY = "SELECT abyss_rank.rank, abyss_rank.ap, abyss_rank.gp, abyss_rank.old_rank_pos, abyss_rank.rank_pos, players.name, legions.name, players.id, players.title_id, players.player_class, players.exp, players.gender FROM abyss_rank INNER JOIN players ON abyss_rank.player_id = players.id LEFT JOIN legion_members ON legion_members.player_id = players.id LEFT JOIN legions ON legions.id = legion_members.legion_id WHERE players.race = ? AND abyss_rank.gp > ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY abyss_rank.gp DESC LIMIT 0, 300";
	public static final String SELECT_LEGIONS_RANKING = "SELECT legions.id, legions.name, legions.contribution_points, legions.level as lvl, legions.old_rank_pos, legions.rank_pos FROM legions,legion_members,players WHERE players.race = ? AND legion_members.rank = 'BRIGADE_GENERAL' AND legion_members.player_id = players.id AND legion_members.legion_id = legions.id AND legions.contribution_points > 0 GROUP BY id ORDER BY legions.contribution_points DESC LIMIT 0,50";
	public static final String SELECT_AP_PLAYER = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND ap > ? ORDER by ap DESC";
    public static final String SELECT_AP_PLAYER_ACTIVE_ONLY = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND ap > ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY ap DESC";
    public static final String SELECT_GP_PLAYER = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND gp > ? ORDER by gp DESC";
    public static final String SELECT_GP_PLAYER_ACTIVE_ONLY = "SELECT player_id, ap, gp FROM abyss_rank, players WHERE abyss_rank.player_id = players.id AND players.race = ? AND gp > ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60 ORDER BY gp DESC";
    public static final String UPDATE_RANK = "UPDATE abyss_rank SET  rank = ?, top_ranking = ? WHERE player_id = ?";
	public static final String SELECT_LEGION_COUNT = "SELECT COUNT(player_id) as players FROM legion_members WHERE legion_id = ?";
	public static final String UPDATE_PLAYER_RANK_LIST = "UPDATE abyss_rank SET abyss_rank.old_rank_pos = abyss_rank.rank_pos, abyss_rank.rank_pos = @a:=@a+1 where player_id in (SELECT id FROM players where race = ?) order by gp desc" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 300" : "");
	public static final String UPDATE_PLAYER_RANK_LIST_ACTIVE_ONLY = "UPDATE abyss_rank SET abyss_rank.old_rank_pos = abyss_rank.rank_pos, abyss_rank.rank_pos = @a:=@a+1 where player_id in (SELECT id FROM players where race = ? AND UNIX_TIMESTAMP(CURDATE())-UNIX_TIMESTAMP(players.last_online) <= ? * 24 * 60 * 60) order by gp desc" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 300" : "");  //only 300 positions are relevant later, so we update them + some extra positions that can get into the toprankings
	public static final String UPDATE_LEGION_RANK_LIST = "UPDATE legions SET legions.old_rank_pos = legions.rank_pos, legions.rank_pos = @a:=@a+1 where id in (SELECT legion_id FROM legion_members, players where rank = 'BRIGADE_GENERAL' AND players.id = legion_members.player_id and players.race = ?) order by legions.contribution_points DESC" + (RankingConfig.TOP_RANKING_SMALL_CACHE ? " limit 75" : "");
	public static final String DELETE_QUERY = "DELETE FROM `abyss_rank` WHERE player_id=?"; // 3.5
    public static final String SELECT_ALL_GPRANK = "SELECT player_id FROM abyss_rank WHERE rank > ?";
    public static final String UPDATA_GPOINT = "UPDATE `abyss_rank` SET `gp` = ? WHERE `player_id` = ?";

    @Override
    public List<Integer> RankPlayers(final int rank) {
        List<Integer> players = new ArrayList<Integer>();
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DatabaseFactory.getConnection();
            stmt = con.prepareStatement(SELECT_ALL_GPRANK);
            stmt.setInt(1, rank);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int playerId = resultSet.getInt("player_id");
                if (!players.contains(playerId))
                    players.add(playerId);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(stmt, con);
        }
        return players;
    }

    @Override
    public void updataGloryPoint(final int playerId, final int gp) {
        Connection con = null;
        try {
            con = DatabaseFactory.getConnection();
            PreparedStatement stmt = con.prepareStatement(UPDATA_GPOINT);
            stmt.setInt(1, gp);
            stmt.setInt(2, playerId);
            stmt.execute();
            stmt.close();
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        finally {
            DatabaseFactory.close(con);
        }
    }

	@Override
	public AbyssRank loadAbyssRank(int playerId) {
		AbyssRank abyssRank = null;
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(SELECT_QUERY);

			stmt.setInt(1, playerId);

			ResultSet resultSet = stmt.executeQuery();

			if (resultSet.next()) {
				int daily_ap = resultSet.getInt("daily_ap");
				int daily_gp = resultSet.getInt("daily_gp");
				int weekly_ap = resultSet.getInt("weekly_ap");
				int weekly_gp = resultSet.getInt("weekly_gp");
				int ap = resultSet.getInt("ap");
				int gp = resultSet.getInt("gp");
				int rank = resultSet.getInt("rank");
				int top_ranking = resultSet.getInt("top_ranking");
				int daily_kill = resultSet.getInt("daily_kill");
				int weekly_kill = resultSet.getInt("weekly_kill");
				int all_kill = resultSet.getInt("all_kill");
				int max_rank = resultSet.getInt("max_rank");
				int last_kill = resultSet.getInt("last_kill");
				int last_ap = resultSet.getInt("last_ap");
				int last_gp = resultSet.getInt("last_gp");
				long last_update = resultSet.getLong("last_update");

				abyssRank = new AbyssRank(daily_ap, daily_gp, weekly_ap, weekly_gp, ap, gp, rank, top_ranking, daily_kill, weekly_kill, all_kill, max_rank, last_kill, last_ap, last_gp,last_update);
				abyssRank.setPersistentState(PersistentState.UPDATED);
			}
			else {
				abyssRank = new AbyssRank(0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, System.currentTimeMillis());
				abyssRank.setPersistentState(PersistentState.NEW);
			}

			resultSet.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("loadAbyssRank", e);
		} finally {
			DatabaseFactory.close(con);
		}
		return abyssRank;
	}

	@Override
	public void loadAbyssRank(final Player player) {
		AbyssRank rank = loadAbyssRank(player.getObjectId());
		player.setAbyssRank(rank);
	}

	@Override
	public boolean storeAbyssRank(Player player) {
		AbyssRank rank = player.getAbyssRank();
		boolean result = false;
		switch (rank.getPersistentState()) {
			case NEW:
				result = addRank(player.getObjectId(), rank);
				break;
			case UPDATE_REQUIRED:
				result = updateRank(player.getObjectId(), rank);
				break;
		}
		rank.setPersistentState(PersistentState.UPDATED);
		return result;
	}

	/**
	 * @param objectId
	 * @param rank
	 * @return
	 */
	private boolean addRank(final int objectId, final AbyssRank rank) {
		Connection con = null;

		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(INSERT_QUERY);

			stmt.setInt(1, objectId);
			stmt.setInt(2, rank.getDailyAP());
			stmt.setInt(3, rank.getDailyGP());
			stmt.setInt(4, rank.getWeeklyAP());
			stmt.setInt(5, rank.getWeeklyGP());
			stmt.setInt(6, rank.getAp());
			stmt.setInt(7, rank.getGp());
			stmt.setInt(8, rank.getRank().getId());
			stmt.setInt(9, rank.getTopRanking());
			stmt.setInt(10, rank.getDailyKill());
			stmt.setInt(11, rank.getWeeklyKill());
			stmt.setInt(12, rank.getAllKill());
			stmt.setInt(13, rank.getMaxRank());
			stmt.setInt(14, rank.getLastKill());
			stmt.setInt(15, rank.getLastAP());
			stmt.setInt(16, rank.getLastGP());
			stmt.setLong(17, rank.getLastUpdate());
			stmt.execute();
			stmt.close();

			return true;
		} catch (SQLException e) {
			log.error("addRank", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	/**
	 * @param objectId
	 * @param rank
	 * @return
	 */
	private boolean updateRank(final int objectId, final AbyssRank rank) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_QUERY);

			stmt.setInt(1, rank.getDailyAP());
			stmt.setInt(2, rank.getDailyGP());
			stmt.setInt(3, rank.getWeeklyAP());
			stmt.setInt(4, rank.getWeeklyGP());
			stmt.setInt(5, rank.getAp());
			stmt.setInt(6, rank.getGp());
			stmt.setInt(7, rank.getRank().getId());
			stmt.setInt(8, rank.getTopRanking());
			stmt.setInt(9, rank.getDailyKill());
			stmt.setInt(10, rank.getWeeklyKill());
			stmt.setInt(11, rank.getAllKill());
			stmt.setInt(12, rank.getMaxRank());
			stmt.setInt(13, rank.getLastKill());
			stmt.setInt(14, rank.getLastAP());
			stmt.setInt(15, rank.getLastGP());
			stmt.setLong(16, rank.getLastUpdate());
			stmt.setInt(17, objectId);
			stmt.execute();
			stmt.close();

			return true;
		} catch (SQLException e) {
			log.error("updateRank", e);

			return false;
		} finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public ArrayList<AbyssRankingResult> getAbyssRankingPlayers(final Race race, final int lowerApLimit, final int maxOfflineDays) {
		Connection con = null;
		final ArrayList<AbyssRankingResult> results = new ArrayList<AbyssRankingResult>();
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(maxOfflineDays > 0 ? SELECT_PLAYERS_RANKING_ACTIVE_ONLY : SELECT_PLAYERS_RANKING);

			stmt.setString(1, race.toString());

            stmt.setInt(2, lowerApLimit);
            if (maxOfflineDays > 0) {
                stmt.setInt(3, maxOfflineDays);
            }

			ResultSet resultSet = stmt.executeQuery();

			while (resultSet.next()) {
				String name = resultSet.getString("players.name");
				int playerAbyssRank = resultSet.getInt("abyss_rank.rank");
				int ap = resultSet.getInt("abyss_rank.ap");
				int gp = resultSet.getInt("abyss_rank.gp");
				int playerTitle = resultSet.getInt("players.title_id");
				int playerId = resultSet.getInt("players.id");
				String playerClassStr = resultSet.getString("players.player_class");
				int playerLevel = DataManager.PLAYER_EXPERIENCE_TABLE.getLevelForExp(resultSet.getLong("players.exp"));
				String playerLegion = resultSet.getString("legions.name");
				int oldRankPos = resultSet.getInt("old_rank_pos");
				int rankPos = resultSet.getInt("rank_pos");
                Gender gender = Gender.valueOf(resultSet.getString("players.gender"));
				PlayerClass playerClass = PlayerClass.getPlayerClassByString(playerClassStr);
				if (playerClass == null) {
					continue;
				}
				AbyssRankingResult rsl = new AbyssRankingResult(name, playerAbyssRank, playerId, ap, gp, playerTitle,
						playerClass, gender, playerLevel, playerLegion, oldRankPos, rankPos);
				results.add(rsl);
			}
			resultSet.close();
			stmt.close();
		} catch (SQLException e) {
			log.error("getAbyssRankingPlayers", e);
		} finally {
			DatabaseFactory.close(con);
		}
		return results;
	}

	@Override
	public ArrayList<AbyssRankingResult> getAbyssRankingLegions(final Race race) {
		final ArrayList<AbyssRankingResult> results = new ArrayList<AbyssRankingResult>();
		DB.select(SELECT_LEGIONS_RANKING, new ParamReadStH() {

			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				while (arg0.next()) {
					String name = arg0.getString("legions.name");
					int cp = arg0.getInt("legions.contribution_points");
					int legionId = arg0.getInt("legions.id");
					int legionLevel = arg0.getInt("lvl");
					int legionMembers = getLegionMembersCount(legionId);
					int oldRankPos = arg0.getInt("old_rank_pos");
					int rankPos = arg0.getInt("rank_pos");
					AbyssRankingResult rsl = new AbyssRankingResult(cp, name, legionId, legionLevel, legionMembers, oldRankPos, rankPos);
					results.add(rsl);
				}
			}

			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				arg0.setString(1, race.toString());
			}
		});
		return results;
	}

	private int getLegionMembersCount(final int legionId) {
		final int[] result = new int[1];
		DB.select(SELECT_LEGION_COUNT, new ParamReadStH() {
			@Override
			public void handleRead(ResultSet arg0) throws SQLException {
				while (arg0.next()) {
					result[0] += arg0.getInt("players");
				}
			}

			@Override
			public void setParams(PreparedStatement arg0) throws SQLException {
				arg0.setInt(1, legionId);
			}
		});
		return result[0];
	}

    @Override
    public void loadPlayersAp(final Race race, final int lowerApLimit, final int maxOfflineDays, final Map<Integer, AGPoint> results) {
        DB.select(maxOfflineDays > 0 ? SELECT_AP_PLAYER_ACTIVE_ONLY : SELECT_AP_PLAYER, new ParamReadStH() {

            @Override
            public void handleRead(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    int ap = rs.getInt("ap");
                    int gp = rs.getInt("gp");
                    if (!results.containsKey(playerId)) {
                        AGPoint agPoint = new AGPoint(ap, gp);
                        results.put(playerId, agPoint);
                    }
                }
            }

            @Override
            public void setParams(PreparedStatement ps) throws SQLException {
                ps.setString(1, race.toString());
                ps.setInt(2, lowerApLimit);
                if (maxOfflineDays > 0) {
                    ps.setInt(3, maxOfflineDays);
                }
            }
        });
    }

    @Override
    public void loadPlayersGp(final Race race, final int lowerApLimit, final int maxOfflineDays, final Map<Integer, AGPoint> results) {
        DB.select(maxOfflineDays > 0 ? SELECT_GP_PLAYER_ACTIVE_ONLY : SELECT_GP_PLAYER, new ParamReadStH() {

            @Override
            public void handleRead(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    int ap = rs.getInt("ap");
                    int gp = rs.getInt("gp");
                    if (!results.containsKey(playerId)) {
                        AGPoint agPoint = new AGPoint(ap, gp);
                        results.put(playerId, agPoint);
                    }
                }
            }

            @Override
            public void setParams(PreparedStatement ps) throws SQLException {
                ps.setString(1, race.toString());
                ps.setInt(2, lowerApLimit);
                if (maxOfflineDays > 0) {
                    ps.setInt(3, maxOfflineDays);
                }
            }
        });
    }

	@Override
	public void updateAbyssRank(int playerId, AbyssRankEnum rankEnum) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(UPDATE_RANK);

			stmt.setInt(1, rankEnum.getId());
			stmt.setInt(2, rankEnum.getQuota());
			stmt.setInt(3, playerId);

			stmt.execute();
			stmt.close();
		} catch (SQLException e) {
			log.error("updateAbyssRank", e);
		} finally {
			DatabaseFactory.close(con);
		}
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

	/* (non-Javadoc)
	 * @see org.typezero.gameserver.dao.AbyssRankDAO#updateRankList()
	 */
	@Override
	public void updateRankList(final int maxOfflineDays) {
		Connection con = null;
		try {
			con = DatabaseFactory.getConnection();
			PreparedStatement stmt = con.prepareStatement(maxOfflineDays > 0 ? UPDATE_PLAYER_RANK_LIST_ACTIVE_ONLY : UPDATE_PLAYER_RANK_LIST);
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ELYOS");
			if (maxOfflineDays > 0) {
				stmt.setInt(2, maxOfflineDays);
			}
			stmt.addBatch();
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ASMODIANS");
			if (maxOfflineDays > 0) {
				stmt.setInt(2, maxOfflineDays);
			}
			stmt.addBatch();
			stmt.executeBatch();
			stmt.close();
			stmt = con.prepareStatement(UPDATE_LEGION_RANK_LIST);
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ELYOS");
			stmt.addBatch();
			stmt.addBatch("SET @a:=0;");
			stmt.setString(1, "ASMODIANS");
			stmt.addBatch();
			stmt.executeBatch();
		} catch (SQLException e) {
			log.error("updateRank", e);
		} finally {
			DatabaseFactory.close(con);
		}
	}
}
