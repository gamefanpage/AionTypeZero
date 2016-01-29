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

package ai.instance.beshmundirTemple;

import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import org.typezero.gameserver.questEngine.QuestEngine;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

import java.util.List;

/**
 *
 * @author Gigi
 */
@AIName("bigorb")
public class BigOrbAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		if (!isSpawned(730276)) { //Portal isn't spawned
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
		}
		else { //Portal is already spawned
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 10));
		}
	}

	@Override
	public boolean onDialogSelect(final Player player, int dialogId, int questId, int extendedRewardIndex) {
		List<Integer> relatedQuests = QuestEngine.getInstance().getQuestNpc(getOwner().getNpcId()).getOnTalkEvent();

		if (dialogId == DialogAction.SETPRO1.id()) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			spawn(730276, 1604.6683f, 1606.5886f, 306.8665f, (byte) 90);

			if (!relatedQuests.isEmpty())
			{
				for (int questId2 : relatedQuests)
				{
					if(questId2 == 30211 || questId2 == 30213 || questId2 == 30311 || questId2 == 30313)
					{
						QuestState qs = player.getQuestStateList().getQuestState(questId2);
						if(qs != null && qs.getStatus() == QuestStatus.START && qs.getQuestVarById(0) == 0)
						{
							player.getInventory().decreaseByItemId(182209614, 1);
							player.getInventory().decreaseByItemId(182209617, 1);
							qs.setStatus(QuestStatus.REWARD);
							PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(questId2, qs.getStatus(), qs.getQuestVars().getQuestVars()));
						}
					}
				}
			}
		}
		else if (dialogId == DialogAction.QUEST_SELECT.id())
		{
			if(player.getInventory().getItemCountByItemId(182209614) > 0 || player.getInventory().getItemCountByItemId(182209617) > 0)
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
			else
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
		}
		return true;
	}

	private boolean isSpawned(int npcId) {
		return !getPosition().getWorldMapInstance().getNpcs(npcId).isEmpty();
	}
}
