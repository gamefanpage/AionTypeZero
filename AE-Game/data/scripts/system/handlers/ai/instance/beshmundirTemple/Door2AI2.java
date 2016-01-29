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

import ai.ActionItemNpcAI2;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.questEngine.model.QuestState;
import org.typezero.gameserver.questEngine.model.QuestStatus;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Gigi
 */
@AIName("door2")
public class Door2AI2 extends ActionItemNpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		QuestState qsneedasmo = player.getQuestStateList().getQuestState(30311);
		QuestState qsneedelyos = player.getQuestStateList().getQuestState(30211);
		if (player.getRace() == Race.ELYOS) {
			if (qsneedelyos != null && qsneedelyos.getStatus() != QuestStatus.NONE) {// TODO: Only one player in group has to																																		// have this quest
				super.handleDialogStart(player);
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
			}
		}
		else {
			if (qsneedasmo != null && qsneedasmo.getStatus() != QuestStatus.NONE) { // TODO: Only one player in group has to																																			// have this quest
				super.handleDialogStart(player);
			}
			else {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 27));
			}
		}
	}

	@Override
	protected void handleUseItemFinish(Player player) {
		AI2Actions.deleteOwner(this);
	}
}
