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
import java.util.List;
import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.questEngine.handlers.models.Monster;
import org.typezero.gameserver.questEngine.model.QuestEnv;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.MathUtil;
import java.util.Set;


/**
 * @author MrPoke
 * reworked Bobobear
 */
public class MentorMonsterHunt extends MonsterHunt {

	private int menteMinLevel;
	private int menteMaxLevel;
	private QuestTemplate qt;
	/**
	 * @param questId
	 * @param startNpc
	 * @param endNpc
	 * @param monsters
	 */
	public MentorMonsterHunt(int questId, List<Integer> startNpcIds, List<Integer> endNpcIds, FastMap<Monster, Set<Integer>> monsters, int menteMinLevel, int menteMaxLevel) {
		super(questId, startNpcIds, endNpcIds, monsters, 0, 0, null, 0);
		this.menteMinLevel = menteMinLevel;
		this.menteMaxLevel = menteMaxLevel;
		this.qt = DataManager.QUEST_DATA.getQuestById(questId);
	}

	@Override
	public boolean onKillEvent(QuestEnv env) {
		Player player = env.getPlayer();
		QuestState qs = player.getQuestStateList().getQuestState(getQuestId());
		if (qs != null && qs.getStatus() == QuestStatus.START){
			switch(qt.getMentorType()){
				case MENTOR:
					if (player.isMentor()) {
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()) {
							if (member.getLevel() >= menteMinLevel && member.getLevel() <= menteMaxLevel
								&& MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE) {
								return super.onKillEvent(env);
							}
						}
					}
					break;
				case MENTE:
					if (player.isInGroup2()){
						PlayerGroup group = player.getPlayerGroup2();
						for (Player member : group.getMembers()){
							if (member.isMentor() && MathUtil.getDistance(player, member) < GroupConfig.GROUP_MAX_DISTANCE)
								return super.onKillEvent(env);
						}
					}
			}
		}
		return false;
	}
}
