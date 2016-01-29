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

import javolution.util.FastMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.handlers.models.Monster;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.RiftService;
import org.typezero.gameserver.services.VortexService;

/**
 * @author MrPoke
 * @reworked vlog, Bobobear
 */
public class MonsterHunt extends QuestHandler {

	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final FastMap<Monster, Set<Integer>> monsters;
	private final int startDialog;
	private final int endDialog;
	private final Set<Integer> aggroNpcs = new HashSet<Integer>();
	private final int invasionWorldId;

	public MonsterHunt(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, FastMap<Monster, Set<Integer>> monsters,
		int startDialog, int endDialog, List<Integer> aggroNpcs, int invasionWorld) {
		super(questId);
		this.questId = questId;
		this.startNpcs.addAll(startNpcIds);
		this.startNpcs.remove(0);
		if (endNpcIds == null) {
			this.endNpcs.addAll(startNpcs);
		}
		else {
			this.endNpcs.addAll(endNpcIds);
			this.endNpcs.remove(0);
		}
		this.monsters = monsters;
		this.startDialog = startDialog;
		this.endDialog = endDialog;
		if (aggroNpcs != null) {
			this.aggroNpcs.addAll(aggroNpcs);
			this.aggroNpcs.remove(0);
		}
		this.invasionWorldId = invasionWorld;
	}

	@Override
	public void register() {
		for (Integer startNpc : startNpcs) {
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}

		for (Set<Integer> monsterIds : monsters.values()) {
			for (Integer monsterId : monsterIds)
				qe.registerQuestNpc(monsterId).addOnKillEvent(questId);
		}

		for (Integer endNpc : endNpcs)
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());

		for (Integer aggroNpc : aggroNpcs)
			qe.registerQuestNpc(aggroNpc).addOnAddAggroListEvent(getQuestId());

		if (invasionWorldId != 0)
			qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, startDialog != 0 ? startDialog : 1011);
				}
				else {
					return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START) {
			for (Monster mi : monsters.keySet()) {
				int endVar = mi.getEndVar();
				int varId = mi.getVar();
				int total = 0;
				do {
					int currentVar = qs.getQuestVarById(varId);
					total += currentVar << ((varId - mi.getVar()) * 6);
					endVar >>= 6;
					varId++;
				}
				while (endVar > 0);
				if (mi.getEndVar() > total) {
					return false;
				}
			}
			if (endNpcs.contains(targetId)) {
				if (env.getDialog() == DialogAction.QUEST_SELECT) {
					return sendQuestDialog(env, endDialog != 0 ? endDialog : 1352);
				}
				else if (env.getDialog() == DialogAction.SELECT_QUEST_REWARD) {
					qs.setStatus(QuestStatus.REWARD);
					updateQuestStatus(env);
					return sendQuestDialog(env, 5);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				if (!aggroNpcs.isEmpty()) {
					switch (env.getDialog()) {
						case QUEST_SELECT:
						case USE_OBJECT:
							return sendQuestDialog(env, 10002);
						case SELECT_QUEST_REWARD:
							return sendQuestDialog(env, 5);
						default:
							return sendQuestEndDialog(env);
					}
				}
				else
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
			for (Monster m : monsters.keySet()) {
				if (m.getNpcIds().contains(env.getTargetId())) {
					int endVar = m.getEndVar();
					int varId = m.getVar();
					int total = 0;
					do {
						int currentVar = qs.getQuestVarById(varId);
						total += currentVar << ((varId - m.getVar()) * 6);
						endVar >>= 6;
						varId++;
					}
					while (endVar > 0);
					total += 1;
					if (total <= m.getEndVar()) {
						if (!aggroNpcs.isEmpty()) {
							qs.setStatus(QuestStatus.REWARD);
						  updateQuestStatus(env);
						}
						else {
							for (int varsUsed = m.getVar(); varsUsed < varId; varsUsed++) {
								int value = total & 0x3F;
								total >>= 6;
								qs.setQuestVarById(varsUsed, value);
							}
                            if (total <= m.getEndVar() && m.getRewardVar())
                            {
                                qs.setStatus(QuestStatus.REWARD);
								if(m.getSetRewardVar() != 0)
									qs.setQuestVar(m.getSetRewardVar());
							}
							updateQuestStatus(env);
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean onAddAggroListEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			QuestService.startQuest(env);
			return true;
		}
		return false;
	}

	@Override
	public boolean onEnterWorldEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		VortexLocation vortexLoc = VortexService.getInstance().getLocationByWorld(invasionWorldId);
		if (player.getWorldId() == invasionWorldId) {
			if ((qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat())) {
				if ((vortexLoc != null && vortexLoc.isActive()) || (searchOpenRift()))
					return QuestService.startQuest(env);
			}
		}
		return false;
	}


	private boolean searchOpenRift() {
		for (RiftLocation loc : RiftService.getInstance().getRiftLocations().values()) {
			if (loc.getWorldId() == invasionWorldId && loc.isOpened()) {
				return true;
			}
		}
		return false;
	}
}
