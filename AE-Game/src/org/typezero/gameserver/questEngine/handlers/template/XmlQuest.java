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
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.questEngine.handlers.models.Monster;
import org.typezero.gameserver.questEngine.handlers.models.XmlQuestData;
import org.typezero.gameserver.questEngine.handlers.models.xmlQuest.events.OnKillEvent;
import org.typezero.gameserver.questEngine.handlers.models.xmlQuest.events.OnTalkEvent;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;

import java.util.Iterator;

/**
 * @author Mr. Poke, modified Bobobear
 */
public class XmlQuest extends QuestHandler {

	private final XmlQuestData xmlQuestData;

	public XmlQuest(XmlQuestData xmlQuestData) {
		super(xmlQuestData.getId());
		this.xmlQuestData = xmlQuestData;
	}

	@Override
	public void register() {
		if (xmlQuestData.getStartNpcId() != null) {
			qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnQuestStart(getQuestId());
			qe.registerQuestNpc(xmlQuestData.getStartNpcId()).addOnTalkEvent(getQuestId());
		}
		if (xmlQuestData.getEndNpcId() != null)
			qe.registerQuestNpc(xmlQuestData.getEndNpcId()).addOnTalkEvent(getQuestId());

		for (OnTalkEvent talkEvent : xmlQuestData.getOnTalkEvent())
			for (int npcId : talkEvent.getIds())
				qe.registerQuestNpc(npcId).addOnTalkEvent(getQuestId());

		for (OnKillEvent killEvent : xmlQuestData.getOnKillEvent()) {
			for (Monster monster : killEvent.getMonsters()) {
				Iterator<Integer> iterator = monster.getNpcIds().iterator();
				while (iterator.hasNext()) {
					int monsterId = iterator.next();
					qe.registerQuestNpc(monsterId).addOnKillEvent(getQuestId());
				}
			}
		}
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		env.setQuestId(getQuestId());
		for (OnTalkEvent talkEvent : xmlQuestData.getOnTalkEvent()) {
			if (talkEvent.operate(env))
				return true;
		}

		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs == null || qs.getStatus() == QuestStatus.NONE || qs.canRepeat()) {
			if (targetId == xmlQuestData.getStartNpcId()) {
				if (env.getDialog() == DialogAction.QUEST_SELECT)
					return sendQuestDialog(env, 1011);
				else
					return sendQuestStartDialog(env);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD && targetId == xmlQuestData.getEndNpcId()) {
			return sendQuestEndDialog(env);
		}
		return false;
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		env.setQuestId(getQuestId());
		for (OnKillEvent killEvent : xmlQuestData.getOnKillEvent()) {
			if (killEvent.operate(env))
				return true;
		}
		return false;
	}
}
