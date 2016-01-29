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
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import org.typezero.gameserver.dao.ChallengeTasksDAO;
import org.typezero.gameserver.dao.MySQL5DAOUtils;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.challenge.ChallengeQuest;
import org.typezero.gameserver.model.challenge.ChallengeTask;
import org.typezero.gameserver.model.gameobjects.PersistentState;
import org.typezero.gameserver.model.templates.challenge.ChallengeQuestTemplate;
import org.typezero.gameserver.model.templates.challenge.ChallengeType;

/**
 * @author ViAl
 */
public class MySQL5ChallengeTasksDAO extends ChallengeTasksDAO {

	private static final Logger log = LoggerFactory.getLogger(MySQL5ChallengeTasksDAO.class);

	private static final String SELECT_QUERY = "SELECT * FROM `challenge_tasks` WHERE `owner_id` = ? AND `owner_type` = ?";
	private static final String INSERT_QUERY = "INSERT INTO `challenge_tasks` (`task_id`, `quest_id`, `owner_id`, `owner_type`, `complete_count`, `complete_time`) VALUES (?, ?, ?, ?, ?, ?);";
	private static final String UPDATE_QUERY = "UPDATE `challenge_tasks` SET `complete_count` = ?, `complete_time`= ? WHERE `task_id` = ? AND `quest_id` = ? AND `owner_id` = ?";

	@Override
	public Map<Integer, ChallengeTask> load(int ownerId, ChallengeType type) {
		FastMap<Integer, ChallengeTask> tasks = new FastMap<Integer, ChallengeTask>().shared();
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SELECT_QUERY);
			stmt.setInt(1, ownerId);
			stmt.setString(2, type.toString());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				int taskId = rset.getInt("task_id");
				int questId = rset.getInt("quest_id");
				int completeCount = rset.getInt("complete_count");
				Timestamp date = rset.getTimestamp("complete_time");
				ChallengeQuestTemplate template = DataManager.CHALLENGE_DATA.getQuestByQuestId(questId);
				ChallengeQuest quest = new ChallengeQuest(template, completeCount);
				quest.setPersistentState(PersistentState.UPDATED);
				if (!tasks.containsKey(taskId)) {
					Map<Integer, ChallengeQuest> quests = new HashMap<Integer, ChallengeQuest>(2);
					quests.put(quest.getQuestId(), quest);
					ChallengeTask task = new ChallengeTask(taskId, ownerId, quests, date);
					tasks.put(taskId, task);
				}
				else {
					tasks.get(taskId).getQuests().put(questId, quest);
				}
			}
			rset.close();
			stmt.close();
		}
		catch (SQLException e) {
			log.error("Error while loading challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
		return tasks;
	}

	@Override
	public void storeTask(ChallengeTask task) {
		for (ChallengeQuest quest : task.getQuests().values()) {
			switch (quest.getPersistentState()) {
				case NEW:
					insertQuestEntry(task, quest);
					break;
				case UPDATE_REQUIRED:
					updateQuestEntry(task, quest);
					break;
			}
		}
	}

	private void insertQuestEntry(ChallengeTask task, ChallengeQuest quest) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(INSERT_QUERY);
			stmt.setInt(1, task.getTaskId());
			stmt.setInt(2, quest.getQuestId());
			stmt.setInt(3, task.getOwnerId());
			stmt.setString(4, task.getTemplate().getType().toString());
			stmt.setInt(5, quest.getCompleteCount());
			stmt.setTimestamp(6, task.getCompleteTime());
			stmt.executeUpdate();
			stmt.close();
			quest.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e) {
			log.error("Error while inserting challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	private void updateQuestEntry(ChallengeTask task, ChallengeQuest quest) {
		Connection conn = null;
		try {
			conn = DatabaseFactory.getConnection();
			PreparedStatement stmt = conn.prepareStatement(UPDATE_QUERY);
			stmt.setInt(1, quest.getCompleteCount());
			stmt.setTimestamp(2, task.getCompleteTime());
			stmt.setInt(3, task.getTaskId());
			stmt.setInt(4, quest.getQuestId());
			stmt.setInt(5, task.getOwnerId());
			stmt.executeUpdate();
			stmt.close();
			quest.setPersistentState(PersistentState.UPDATED);
		}
		catch (SQLException e) {
			log.error("Error while updating challenge task. " + e);
		}
		finally {
			DatabaseFactory.close(conn);
		}
	}

	@Override
	public boolean supports(String databaseName, int majorVersion, int minorVersion) {
		return MySQL5DAOUtils.supports(databaseName, majorVersion, minorVersion);
	}

}
