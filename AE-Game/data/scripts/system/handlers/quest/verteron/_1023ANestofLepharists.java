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

package quest.verteron;

import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.questEngine.handlers.QuestHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author Mr. Poke
 * @modified Dune11
 * @reworked vlog
 */
public class _1023ANestofLepharists extends QuestHandler {

	private final static int questId = 1023;

	public _1023ANestofLepharists() {
		super(questId);
	}

	@Override
	public void register() {
		qe.registerQuestNpc(203098).addOnTalkEvent(questId);
		qe.registerQuestNpc(203183).addOnTalkEvent(questId);
		qe.registerOnEnterZone(ZoneName.get("MYSTERIOUS_SHIPWRECK_210030000"), questId);
		qe.registerOnEnterZoneMissionEnd(questId);
		qe.registerOnLevelUp(questId);
	}

	@Override
	public boolean onZoneMissionEndEvent(QuestEnv env) {
		return defaultOnZoneMissionEndEvent(env, 1013);
	}

	@Override
	public boolean onLvlUpEvent(QuestEnv env) {
		int[] quests = { 1130, 1013 };
		return defaultOnLvlUpEvent(env, quests, true);
	}

	@Override
	public boolean onDialogEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null)
			return false;

		int var = qs.getQuestVarById(0);
		int targetId = 0;
		if (env.getVisibleObject() instanceof Npc)
			targetId = ((Npc) env.getVisibleObject()).getNpcId();

		if (qs.getStatus() == QuestStatus.START) {
			if (targetId == 203098) // Spatalos
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 0)
							return sendQuestDialog(env, 1011);
					case SELECT_ACTION_1012:
						return sendQuestDialog(env, 1012);
					case SELECT_ACTION_1013:
						return sendQuestDialog(env, 1013);
					case SETPRO1:
						return defaultCloseDialog(env, 0, 1); // 1
				}
			}
			else if (targetId == 203183) // Khidia
			{
				switch (env.getDialog()) {
					case QUEST_SELECT:
						if (var == 1)
							return sendQuestDialog(env, 1352);
						else if (var == 3)
							return sendQuestDialog(env, 1693);
						else if (var == 4)
							return sendQuestDialog(env, 2034);
					case SELECT_ACTION_1353:
						return sendQuestDialog(env, 1353);
					case SETPRO2:
						if (var == 1) {
							playQuestMovie(env, 30);
							return defaultCloseDialog(env, 1, 2); // 2
						}
					case SELECT_ACTION_1694:
						return sendQuestDialog(env, 1694);
					case SETPRO3:
						if (var == 3)
							return defaultCloseDialog(env, 3, 4); // 4
					case CHECK_USER_HAS_QUEST_ITEM:
						if (var == 4)
							return checkQuestItems(env, 4, 4, false, 2120, 2035);
					case FINISH_DIALOG:
						return sendQuestDialog(env, 10);
					case SETPRO4:
						//return defaultCloseDialog(env, 4, 5, true, false); // 5 + reward
								qs.setQuestVar(5);
								qs.setStatus(QuestStatus.REWARD);
								updateQuestStatus(env);
								return sendQuestSelectionDialog(env);
				}
			}
		}
		else if (qs.getStatus() == QuestStatus.REWARD) {
			if (targetId == 203098) // Spatalos
			{
				if (env.getDialog() == DialogAction.USE_OBJECT)
					return sendQuestDialog(env, 2375);
				else
					return sendQuestEndDialog(env);
			}
		}
		return false;
	}

	@Override
	public boolean onEnterZoneEvent(QuestEnv env, ZoneName zoneName) {
		if (zoneName != ZoneName.get("MYSTERIOUS_SHIPWRECK_210030000"))
			return false;
		final Player player = env.getPlayer();
		if (player == null)
			return false;
		final QuestState qs = player.getQuestStateList().getQuestState(questId);
		if (qs == null || qs.getQuestVars().getQuestVars() != 2)
			return false;

		if (qs.getQuestVars().getVarById(0) == 2) {
			playQuestMovie(env, 23);
			qs.setQuestVarById(0, 3); // 3
			updateQuestStatus(env);
			return true;
		}
		return false;
	}
}
