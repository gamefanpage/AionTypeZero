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

import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.rift.RiftLocation;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.services.RiftService;
import org.typezero.gameserver.services.VortexService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Standard xml-based handling for the DAILY quests with onKillInZone events
 *
 * @author vlog, reworked bobobear
 */
public class KillInWorld extends QuestHandler {

	private final int questId;
	private final Set<Integer> startNpcs = new HashSet<Integer>();
	private final Set<Integer> endNpcs = new HashSet<Integer>();
	private final Set<Integer> worldIds = new HashSet<Integer>();
	private final int killAmount;
	private final int invasionWorldId;

	public KillInWorld(int questId, List<Integer> endNpcIds, List<Integer> startNpcIds, List<Integer> worldIds, int killAmount, int invasionWorld) {
		super(questId);
		if (startNpcIds != null) {
			this.startNpcs.addAll(startNpcIds);
			this.startNpcs.remove(0);
		}
		if (endNpcIds == null) {
			this.endNpcs.addAll(startNpcs);
		} else {
			this.endNpcs.addAll(endNpcIds);
			this.endNpcs.remove(0);
		}
		this.questId = questId;
		this.worldIds.addAll(worldIds);
		this.worldIds.remove(0);
		this.killAmount = killAmount;
		this.invasionWorldId = invasionWorld;
	}

	@Override
	public void register() {
		Iterator<Integer> iterator = startNpcs.iterator();
		while (iterator.hasNext()) {
			int startNpc = iterator.next();
			qe.registerQuestNpc(startNpc).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(startNpc).addOnTalkEvent(getQuestId());
		}
		iterator = endNpcs.iterator();
		while (iterator.hasNext()) {
			int endNpc = iterator.next();
			qe.registerQuestNpc(endNpc).addOnTalkEvent(getQuestId());
		}
		iterator = worldIds.iterator();
		while (iterator.hasNext()) {
			int worldId = iterator.next();
			qe.registerOnKillInWorld(worldId, questId);
		}
		if(invasionWorldId != 0)
			qe.registerOnEnterWorld(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (startNpcs.isEmpty() || startNpcs.contains(targetId)) {
				switch (dialog) {
					case QUEST_SELECT: {
						return sendQuestDialog(env, 4762);
					}
					case QUEST_ACCEPT_1: {
						return sendQuestStartDialog(env);
					}
					default: {
						return sendQuestStartDialog(env);
					}
				}
			}
		}
		else if (qs != null && qs.getStatus() == QuestStatus.REWARD) {
			if (endNpcs.contains(targetId)) {
				return sendQuestEndDialog(env);
			}
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

	@Override
	public boolean onKillInWorldEvent(QuestEnv env) {
		return defaultOnKillRankedEvent(env, 0, killAmount, true); // reward
	}
}
