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

package org.typezero.gameserver.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.dao.ChallengeTasksDAO;
import org.typezero.gameserver.dao.LegionMemberDAO;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.dao.TownDAO;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.challenge.ChallengeQuest;
import org.typezero.gameserver.model.challenge.ChallengeTask;
import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.templates.challenge.ChallengeTaskTemplate;
import org.typezero.gameserver.model.templates.challenge.ChallengeType;
import org.typezero.gameserver.model.templates.challenge.ContributionReward;
import org.typezero.gameserver.model.town.Town;
import org.typezero.gameserver.network.aion.serverpackets.SM_CHALLENGE_LIST;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.mail.SystemMailService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;

/**
 * @author ViAl
 */
public class ChallengeTaskService {

	private static final Logger log = LoggerFactory.getLogger(ChallengeTaskService.class);
	private Map<Integer, Map<Integer, ChallengeTask>> cityTasks;
	private Map<Integer, Map<Integer, ChallengeTask>> legionTasks;

	private static class SingletonHolder {

		protected static final ChallengeTaskService instance = new ChallengeTaskService();
	}

	public static final ChallengeTaskService getInstance() {
		return SingletonHolder.instance;
	}

	private ChallengeTaskService() {
		cityTasks = new FastMap<Integer, Map<Integer, ChallengeTask>>().shared();
		legionTasks = new FastMap<Integer, Map<Integer, ChallengeTask>>().shared();
		log.info("ChallengeTaskService initialized.");
	}

	public void showTaskList(Player player, ChallengeType challengeType, int ownerId) {
		if (CustomConfig.CHALLENGE_TASKS_ENABLED) {
			int ownerLevel = 0;
			switch (challengeType) {
				case TOWN:
					ownerLevel = TownService.getInstance().getTownById(ownerId).getLevel();
					break;
				case LEGION:
					ownerLevel = player.getLegion().getLegionLevel();
					break;
			}
			List<ChallengeTask> availableTasks = buildTaskList(player, challengeType, ownerId, ownerLevel);
			PacketSendUtility.sendPacket(player, new SM_CHALLENGE_LIST(2, ownerId, challengeType, availableTasks));
			for (ChallengeTask task : availableTasks) {
				PacketSendUtility.sendPacket(player, new SM_CHALLENGE_LIST(7, ownerId, challengeType, task));
			}
		}
	}

	private List<ChallengeTask> buildTaskList(Player player, ChallengeType challengeType, int ownerId, int ownerLevel) {
		Map<Integer, Map<Integer, ChallengeTask>> taskMap = null;
		if (challengeType == ChallengeType.LEGION)
			taskMap = legionTasks;
		else if (challengeType == ChallengeType.TOWN)
			taskMap = cityTasks;
		int playerTownId = TownService.getInstance().getTownResidence(player);
		List<ChallengeTask> availableTasks = new ArrayList<ChallengeTask>();
		if (!taskMap.containsKey(ownerId)) {
			Map<Integer, ChallengeTask> tasks = DAOManager.getDAO(ChallengeTasksDAO.class).load(ownerId, challengeType);
			taskMap.put(ownerId, tasks);
		}
		for (ChallengeTask ct : taskMap.get(ownerId).values()) {
			if (ct.getTemplate().isRepeatable())
				availableTasks.add(ct);
			else if (!ct.isCompleted())
				availableTasks.add(ct);
		}
		for (ChallengeTaskTemplate template : DataManager.CHALLENGE_DATA.getTasks().values()) {
			if (template.getType() == challengeType && template.getRace() == player.getRace()) {
				if (!taskMap.get(ownerId).containsKey(template.getId())) {
					if (ownerLevel >= template.getMinLevel() && ownerLevel <= template.getMaxLevel()) {
						if (template.isTownResidence() && playerTownId != ownerId) {
							continue;
						}
						if (template.getPrevTask() == null) {
							ChallengeTask task = new ChallengeTask(ownerId, template);
							taskMap.get(ownerId).put(task.getTaskId(), task);
							DAOManager.getDAO(ChallengeTasksDAO.class).storeTask(task);
							availableTasks.add(task);
							continue;
						}
						else {
							int prevTaskId = template.getPrevTask();
							if (taskMap.get(ownerId).containsKey(prevTaskId)) {
								ChallengeTask prevTask = taskMap.get(ownerId).get(prevTaskId);
								if (prevTask.isCompleted()) {
									ChallengeTask task = new ChallengeTask(ownerId, template);
									taskMap.get(ownerId).put(task.getTaskId(), task);
									DAOManager.getDAO(ChallengeTasksDAO.class).storeTask(task);
									availableTasks.add(task);
								}
							}
						}
					}
				}
			}
		}
		return availableTasks;
	}

	public void onChallengeQuestFinish(Player player, int questId) {
		ChallengeTaskTemplate taskTemplate = DataManager.CHALLENGE_DATA.getTaskByQuestId(questId);
		switch (taskTemplate.getType()) {
			case TOWN:
				onCityTaskFinish(player, taskTemplate, questId);
				break;
			case LEGION:
				onLegionTaskFinish(player, taskTemplate, questId);
				break;
		}
	}

	private void onCityTaskFinish(Player player, ChallengeTaskTemplate taskTemplate, int questId) {
		int townId = TownService.getInstance().getTownIdByPosition(player);
		if (cityTasks.get(townId) == null) {
			buildTaskList(player, ChallengeType.TOWN, townId, TownService.getInstance().getTownById(townId).getLevel());
			if (cityTasks.get(townId) == null) {
				log.warn("Town not in CityTasks! TownId:" + townId + "; Player town residence:" + TownService.getInstance().getTownResidence(player));
				return;
			}
		}
		ChallengeTask task = cityTasks.get(townId).get(taskTemplate.getId());
		if(task == null || task.getQuests().get(questId) == null) {
			log.warn("Player "+player.getName()+" trying to finish city task in the city which haven't task with this id. Town id:"+townId+", task id:"+taskTemplate.getId()+", quest id:"+questId);
			return;
		}
		ChallengeQuest quest = task.getQuests().get(questId);
		if (quest.getCompleteCount() >= quest.getMaxRepeats())
			return;
		if (!task.isCompleted()) {
			task.updateCompleteTime();
			quest.increaseCompleteCount();
			DAOManager.getDAO(ChallengeTasksDAO.class).storeTask(task);
			Town town = TownService.getInstance().getTownById(townId);
			if (town != null) {
				int oldLevel = town.getLevel();
				town.increasePoints(quest.getScorePerQuest());
				if (task.isCompleted()) {
					switch (taskTemplate.getReward().getType()) {
						case POINT:
							town.increasePoints(taskTemplate.getReward().getValue());
							break;
						case SPAWN:
							// TODO
							break;
					}
					// PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401519, new DescriptionId(804307), 601));
				}
				if (town.getLevel() != oldLevel)
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401520, town.getId(), town.getLevel()));
				DAOManager.getDAO(TownDAO.class).store(town);
			}
		}
	}

	private void onLegionTaskFinish(Player player, ChallengeTaskTemplate taskTemplate, int questId) {
		/**
		 * Player could take challenge task and after that leave legion.
		 */
		if (player.getLegion() == null)
			return;
		int legionId = player.getLegion().getLegionId();
		/**
		 * If player took challenge task in one legion, then leave that legion and
		 * enter another.
		 */
		if (!legionTasks.containsKey(legionId))
			return;
		/**
		 * If player took challenge task in one legion, then leave that legion and
		 * enter another, and after that completed this task in new legion.
		 */
		if (legionTasks.get(legionId).get(taskTemplate.getId()) == null)
			return;
		ChallengeTask task = legionTasks.get(player.getLegion().getLegionId()).get(taskTemplate.getId());
		ChallengeQuest quest = task.getQuests().get(questId);
		if (quest.getCompleteCount() >= quest.getMaxRepeats())
			return;
		player.getLegionMember().increaseChallengeScore(quest.getScorePerQuest());
		if (!task.isCompleted()) {
			task.updateCompleteTime();
			quest.increaseCompleteCount();
			DAOManager.getDAO(ChallengeTasksDAO.class).storeTask(task);
			if (task.isCompleted()) {
				TreeMap<Integer, List<Integer>> winnersByPoints = new TreeMap<Integer, List<Integer>>();
				for (Integer memberObjId : player.getLegion().getLegionMembers()) {
					Player member = World.getInstance().findPlayer(memberObjId);
					if (member != null) {
						int score = member.getLegionMember().getChallengeScore();
						if (winnersByPoints.get(score) == null)
							winnersByPoints.put(score, new ArrayList<Integer>());
						winnersByPoints.get(score).add(member.getObjectId());
						member.getLegionMember().setChallengeScore(0);
						continue;
					}
					else {
						LegionMember legionMember = DAOManager.getDAO(LegionMemberDAO.class).loadLegionMember(memberObjId);
						int score = legionMember.getChallengeScore();
						if (winnersByPoints.get(score) == null)
							winnersByPoints.put(score, new ArrayList<Integer>());
						winnersByPoints.get(score).add(legionMember.getObjectId());
						legionMember.setChallengeScore(0);
						DAOManager.getDAO(LegionMemberDAO.class).storeLegionMember(memberObjId, legionMember);
					}
				}
				int rewardsAdded = 0, itemId, itemCount;
				for (Entry<Integer, List<Integer>> e : winnersByPoints.descendingMap().entrySet()) {
					for (int objectId : e.getValue()) {
						for (ContributionReward reward : taskTemplate.getContrib()) {
							if (rewardsAdded <= reward.getNumber()) {
								rewardsAdded++;
								itemId = reward.getRewardId();
								itemCount = reward.getItemCount();
								String recipientName = DAOManager.getDAO(PlayerDAO.class).loadPlayerCommonData(objectId).getName();
								SystemMailService.getInstance().sendMail("Legion reward", recipientName, "", "", itemId, itemCount, 0, LetterType.NORMAL);
								break;
							}
						}
					}
					e.getValue().clear();
				}
				winnersByPoints.clear();
				winnersByPoints = null;
			}
		}
	}

	public boolean canRaiseLegionLevel(int legionId, int legionLevel) {
		Map<Integer, ChallengeTask> tasks;
		if (legionTasks.containsKey(legionId)) {
			tasks = legionTasks.get(legionId);
		}
		else {
			tasks = DAOManager.getDAO(ChallengeTasksDAO.class).load(legionId, ChallengeType.LEGION);
		}
		for (ChallengeTask task : tasks.values()) {
			if (task.getTemplate().getMinLevel() == legionLevel && task.isCompleted())
				return true;
		}
		return false;
	}
}
