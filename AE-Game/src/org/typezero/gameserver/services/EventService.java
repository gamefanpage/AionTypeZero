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

import org.typezero.gameserver.configs.main.EventsConfig;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Future;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.EventType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.event.EventTemplate;
import org.typezero.gameserver.model.templates.quest.XMLStartCondition;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;

/**
 * @author Rolandas
 */
public class EventService {

	Logger log = LoggerFactory.getLogger(EventService.class);

	private final int CHECK_TIME_PERIOD = 1000 * 60 * 5;

	private boolean isStarted = false;

	private Future<?> checkTask = null;

	private List<EventTemplate> activeEvents;

	TIntObjectHashMap<List<EventTemplate>> eventsForStartQuest = new TIntObjectHashMap<List<EventTemplate>>();

	TIntObjectHashMap<List<EventTemplate>> eventsForMaintainQuest = new TIntObjectHashMap<List<EventTemplate>>();

	private static class SingletonHolder {

		protected static final EventService instance = new EventService();
	}

	public static final EventService getInstance() {
		return SingletonHolder.instance;
	}

	private EventService() {
		activeEvents = Collections.synchronizedList(DataManager.EVENT_DATA.getActiveEvents());
		updateQuestMap();
	}

	/**
	 * This method is called just after player logged in to the game.<br>
	 * <br>
	 * <b><font color='red'>NOTICE: </font>This method must not be called from anywhere else.</b>
	 */
	public void onPlayerLogin(Player player) {
		List<Integer> activeStartQuests = new ArrayList<Integer>();
		List<Integer> activeMaintainQuests = new ArrayList<Integer>();
		TIntObjectHashMap<List<EventTemplate>> map1 = null;
		TIntObjectHashMap<List<EventTemplate>> map2 = null;

		synchronized (activeEvents) {
			for (EventTemplate et : activeEvents) {
				if (et.isActive()) {
					activeStartQuests.addAll(et.getStartableQuests());
					activeMaintainQuests.addAll(et.getMaintainableQuests());
				}
			}
			map1 = new TIntObjectHashMap<List<EventTemplate>>(eventsForStartQuest);
			map2 = new TIntObjectHashMap<List<EventTemplate>>(eventsForMaintainQuest);
		}

		StartOrMaintainQuests(player, activeStartQuests.listIterator(), map1, true);
		StartOrMaintainQuests(player, activeMaintainQuests.listIterator(), map2, false);

		activeStartQuests.clear();
		activeMaintainQuests.clear();
		map1.clear();
		map2.clear();
	}

	void StartOrMaintainQuests(Player player, ListIterator<Integer> questList,
		TIntObjectHashMap<List<EventTemplate>> templateMap, boolean start) {
		while (questList.hasNext()) {
			int questId = questList.next();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			QuestEnv cookie = new QuestEnv(null, player, questId, 0);
			QuestStatus status = qs == null ? QuestStatus.START : qs.getStatus();

			if (QuestService.checkLevelRequirement(questId, player.getCommonData().getLevel())) {
				QuestTemplate template = DataManager.QUEST_DATA.getQuestById(questId);
				if (template.getRacePermitted() != null) {
					if (template.getRacePermitted().ordinal() != player.getCommonData().getRace().ordinal())
						continue;
				}

				if (template.getClassPermitted().size() != 0) {
					if (!template.getClassPermitted().contains(player.getCommonData().getPlayerClass()))
						continue;
				}

				if (template.getGenderPermitted() != null) {
					if (template.getGenderPermitted().ordinal() != player.getGender().ordinal())
						continue;
				}

				int amountOfStartConditions = template.getXMLStartConditions().size();
				int fulfilledStartConditions = 0;
				if (amountOfStartConditions != 0) {
					for (XMLStartCondition startCondition : template.getXMLStartConditions()) {
						if (startCondition.check(player, false)) {
							fulfilledStartConditions++;
						}
					}
					if (fulfilledStartConditions < 1) {
						continue;
					}
				}

				if (qs != null) {
					if (qs.getCompleteTime() != null || status == QuestStatus.COMPLETE) {
						DateTime completed = null;
						if (qs.getCompleteTime() == null)
							completed = new DateTime(0);
						else
							completed = new DateTime(qs.getCompleteTime().getTime());

						if (templateMap.containsKey(questId)) {
							for (EventTemplate et : templateMap.get(questId)) {
								// recurring event, reset it
								if (et.getStartDate().isAfter(completed)) {
									if (start) {
										status = QuestStatus.START;
										qs.setQuestVar(0);
										qs.setCompleteCount(0);
										qs.setStatus(status);
									}
									break;
								}
							}
						}
					}
					// re-register quests
					if (status == QuestStatus.COMPLETE) {
						PacketSendUtility
							.sendPacket(player, new SM_QUEST_ACTION(questId, status, qs.getQuestVars().getQuestVars()));
					}
					else
						QuestService.startEventQuest(cookie, status);
				}
				else if (start) {
					QuestService.startEventQuest(cookie, status);
				}
			}
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void start() {
		if (isStarted)
			checkTask.cancel(false);

		isStarted = true;

		checkTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				checkEvents();
			}
		}, 0, CHECK_TIME_PERIOD);
	}

	public void stop() {
		if (isStarted)
			checkTask.cancel(false);
		checkTask = null;
		isStarted = false;
	}

	private void checkEvents() {
		List<EventTemplate> newEvents = new ArrayList<EventTemplate>();
		List<EventTemplate> allEvents = DataManager.EVENT_DATA.getAllEvents();

		for (EventTemplate et : allEvents) {
			if (et.isActive()) {
				newEvents.add(et);
				et.Start();
			}
		}

		synchronized (activeEvents) {
			for (EventTemplate et : activeEvents) {
				if (et.isExpired() || !DataManager.EVENT_DATA.Contains(et.getName())) {
					et.Stop();
				}
			}

			activeEvents.clear();
			eventsForStartQuest.clear();
			eventsForMaintainQuest.clear();
			activeEvents.addAll(newEvents);
			updateQuestMap();
		}

		newEvents.clear();
		allEvents.clear();
	}

	private void updateQuestMap() {
		for (EventTemplate et : activeEvents) {
			for (int qId : et.getStartableQuests()) {
				if (!eventsForStartQuest.containsKey(qId))
					eventsForStartQuest.put(qId, new ArrayList<EventTemplate>());
				eventsForStartQuest.get(qId).add(et);
			}
			for (int qId : et.getMaintainableQuests()) {
				if (!eventsForMaintainQuest.containsKey(qId))
					eventsForMaintainQuest.put(qId, new ArrayList<EventTemplate>());
				eventsForMaintainQuest.get(qId).add(et);
			}
		}
	}

	public boolean checkQuestIsActive(int questId) {
		synchronized (activeEvents) {
			if (eventsForStartQuest.containsKey(questId) || eventsForMaintainQuest.containsKey(questId))
				return true;
		}
		return false;
	}

	public EventType getEventType() {
		if (EventsConfig.ENABLE_EVENT_SERVICE) {
			for (EventTemplate et : activeEvents) {
				String theme = et.getTheme();
				if (theme != null) {
					EventType type = EventType.getEventType(theme);
					if (et.isActive() && !type.equals(EventType.NONE)) {
						return type;
					}
				}
			}
		}
		return EventType.NONE;
	}

	public List<EventTemplate> getActiveEvents() {
		return activeEvents;
	}
}
