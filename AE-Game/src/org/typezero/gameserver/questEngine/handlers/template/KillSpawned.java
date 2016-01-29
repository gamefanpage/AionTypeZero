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

package org.typezero.gameserver.questEngine.handlers.template;

import gnu.trove.list.array.TIntArrayList;
import javolution.util.FastMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.spawns.SpawnSearchResult;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.handlers.models.Monster;
import org.typezero.gameserver.questEngine.handlers.models.SpawnedMonster;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;

/**
 * @author vlog
 */
public class KillSpawned extends QuestHandler {

	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final FastMap<List<Integer>, SpawnedMonster> spawnedMonsters;
	private TIntArrayList spawnerObjects;

	public KillSpawned(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, FastMap<List<Integer>, SpawnedMonster> spawnedMonsters) {
		super(questId);
		this.questId = questId;
		this.startNpcs.addAll(startNpcIds);
		this.startNpcs.remove(0);
		if (endNpcIds == null) {
			this.endNpcs.addAll(startNpcs);
		} else {
			this.endNpcs.addAll(endNpcIds);
			this.endNpcs.remove(0);
		}
		this.spawnedMonsters = spawnedMonsters;
		this.spawnerObjects = new TIntArrayList();
		for (SpawnedMonster m : spawnedMonsters.values()) {
			spawnerObjects.add(m.getSpawnerObject());
		}
	}

	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		for (List<Integer> spawnedMonsterIds : spawnedMonsters.keySet()) {
			iterator = spawnedMonsterIds.iterator();
			while (iterator.hasNext()) {
				int spawnedMonsterId = iterator.next();
				qe.registerQuestNpc(spawnedMonsterId).addOnKillEvent(questId);
			}
		}
		iterator = endNpcs.iterator();
		while (iterator.hasNext()) {
			int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
		for (int i = 0; i < spawnerObjects.size(); i++) {
			qe.registerQuestNpc(spawnerObjects.get(i)).addOnTalkEvent(questId);
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			 if (spawnerObjects.contains(targetId)) {
				if (env.getDialog() == DialogAction.USE_OBJECT) {
					int monsterId = 0;
					for (SpawnedMonster m : spawnedMonsters.values()) {
						if(m.getSpawnerObject() == targetId) {
							monsterId = m.getNpcIds().get(0);
							break;
						}
					}

					SpawnSearchResult searchResult = DataManager.SPAWNS_DATA2.getFirstSpawnByNpcId(player.getWorldId(), targetId);
					QuestService.addNewSpawn(player.getWorldId(), player.getInstanceId(), monsterId, searchResult.getSpot().getX(), searchResult.getSpot().getY(),
						searchResult.getSpot().getZ(), searchResult.getSpot().getHeading());
					return true;
				}
			 }
			 else {
				 for (Monster mi : spawnedMonsters.values()) {
					 if (mi.getEndVar() > qs.getQuestVarById(mi.getVar())) {
					return false;
					 }
				 }

				if (endNpcs.contains(targetId)) {
					 if (env.getDialog() == DialogAction.QUEST_SELECT) {
						 return sendQuestDialog(env, 10002);
					 }
					 else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
						 return sendQuestDialog(env, 5);
					 }
				 }
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs != null && qs.getStatus() == QuestStatus.START) {
			for (SpawnedMonster m : spawnedMonsters.values()) {
				if (m.getNpcIds().contains(env.getTargetId())) {
					if (qs.getQuestVarById(m.getVar()) < m.getEndVar()) {
						qs.setQuestVarById(m.getVar(), qs.getQuestVarById(m.getVar()) + 1);
						 for (Monster mi : spawnedMonsters.values()) {
							if (qs.getQuestVarById(mi.getVar()) < mi.getEndVar()) {
								updateQuestStatus(env);
								return true;
							}
						}
						qs.setStatus(QuestStatus.REWARD);
						updateQuestStatus(env);
						return true;
					}
				}
			}
		}
		return false;
	}
}
