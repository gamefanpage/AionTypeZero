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

package quest.pernon;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;


/**
 * @author zhkchi
 *
 */
public class _28830InteriorDecorator  extends QuestHandler {

	private static final int questId = 28830;
	private static final Set<Integer> butlers;

	static {
		butlers = new HashSet<Integer>();
		butlers.add(810022);
		butlers.add(810023);
		butlers.add(810024);
		butlers.add(810025);
		butlers.add(810026);
	}

	public _28830InteriorDecorator() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(830585).addOnQuestStart(questId);

		Iterator<Integer> iter = butlers.iterator();
		while (iter.hasNext()) {
			int butlerId = iter.next();
			qe.registerQuestNpc(butlerId).addOnTalkEvent(questId);
		}
		qe.registerQuestNpc(830651).addOnTalkEvent(questId);
		qe.registerQuestHouseItem(questId);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		final Player player = env.getPlayer();
		int targetId = env.getTargetId();
		DialogAction dialog = env.getDialog();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		House house = player.getActiveHouse();

		if(house == null){
			return false;
		}

		if (qs == null || qs.getStatus() == QuestStatus.NONE) {
			if (targetId == 830585) {
				switch (dialog) {
					case QUEST_SELECT:
						return sendQuestDialog(env, 1011);
					case QUEST_ACCEPT_1:
					case QUEST_ACCEPT_SIMPLE:
						return sendQuestStartDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.START && butlers.contains(targetId) && qs.getQuestVarById(0) == 0) {
			if (house.getButler().getNpcId() != targetId)
				return false;
			switch (dialog) {
				case USE_OBJECT:
					return sendQuestDialog(env, 1352);
				case SETPRO1:
					return defaultCloseDialog(env, 0, 1);
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD && targetId == 830651) {
			switch (dialog) {
				case USE_OBJECT:
					return sendQuestDialog(env, 2375);
				case SELECT_QUEST_REWARD:
					return sendQuestDialog(env, 5);
				case SELECTED_QUEST_NOREWARD:
					sendQuestEndDialog(env);
					return true;
			}
		}

		return false;
	}

	@Override
	public boolean onHouseItemUseEvent(QuestEnv env) {
		final Player player = env.getPlayer();
			QuestState qs = player.getQuestStateList().getQuestState(questId);
			if (qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 1) {
				changeQuestStep(env, 1, 1, true);
			}
		return false;
	}

}
