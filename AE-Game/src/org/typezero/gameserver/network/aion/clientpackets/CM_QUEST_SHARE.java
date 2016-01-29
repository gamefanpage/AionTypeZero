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

package org.typezero.gameserver.network.aion.clientpackets;

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.QuestTemplate;
import org.typezero.gameserver.model.templates.quest.QuestTarget;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.configs.main.GroupConfig;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.services.QuestService;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;


/**
 * @author ginho1
 */
public class CM_QUEST_SHARE extends AionClientPacket {
	public int questId;

	public CM_QUEST_SHARE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		this.questId = readD();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player player  = this.getConnection().getActivePlayer();

		if (player == null)
			return;

		QuestTemplate questTemplate = DataManager.QUEST_DATA.getQuestById(questId);

		if (questTemplate == null || questTemplate.isCannotShare())
			return;

		QuestState questState = player.getQuestStateList().getQuestState(this.questId);

		if ((questState == null) || (questState.getStatus() == QuestStatus.COMPLETE))
			return;

		if (player.isInGroup2()) {
			for (Player member : player.getPlayerGroup2().getOnlineMembers()) {
				if (player == member)
					continue;

					if (!MathUtil.isIn3dRange(member, player, GroupConfig.GROUP_MAX_DISTANCE)) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100000, member.getName()));
						continue;
				}

				if(questTemplate.getTarget().equals(QuestTarget.ALLIANCE)) {
					PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1100005, player.getName()));
					continue;
				}

				if(!questTemplate.isRepeatable()) {
					if(member.getQuestStateList().getQuestState(questId) != null)
						if(member.getQuestStateList().getQuestState(questId).getStatus() != null && member.getQuestStateList().getQuestState(questId).getStatus() != QuestStatus.NONE)
							continue;
				}
				else {
					if(member.getQuestStateList().getQuestState(questId) != null)
						if(member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.START || member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.REWARD)
							continue;
				}

				if(member.isInFlyingState()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100003, member.getName()));
					continue;
				}

				if (!QuestService.checkLevelRequirement(this.questId, member.getLevel())) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100003, member.getName()));
					PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1100003, player.getName()));
					continue;
				}
				PacketSendUtility.sendPacket(member, new SM_QUEST_ACTION(this.questId, member.getObjectId(), true));
			}
		}

		else if(player.isInAlliance2()) {
			for (Player member : player.getPlayerAllianceGroup2().getOnlineMembers()) {
				if (player == member)
					continue;
					if (!MathUtil.isIn3dRange(member, player, GroupConfig.GROUP_MAX_DISTANCE)) {
						PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100000, member.getName()));
						continue;
				}

				if(!questTemplate.isRepeatable()) {
					if(member.getQuestStateList().getQuestState(questId).getStatus() != null && member.getQuestStateList().getQuestState(questId).getStatus() != QuestStatus.NONE)
							continue;
				}

				else {
						if(member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.START || member.getQuestStateList().getQuestState(questId).getStatus() == QuestStatus.REWARD)
							continue;
				}

				if(member.isInFlyingState()) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100003, member.getName()));
					continue;
				}

				if (!QuestService.checkLevelRequirement(this.questId, member.getLevel())) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100003, member.getName()));
					PacketSendUtility.sendPacket(member, new SM_SYSTEM_MESSAGE(1100003, player.getName()));
					continue;
				}
				PacketSendUtility.sendPacket(member, new SM_QUEST_ACTION(this.questId, member.getObjectId(), true));
			}
		}
		else {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1100000));
				return;
		}
	}
}
