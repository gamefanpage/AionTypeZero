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

package ai.instance.empyreanCrucible;

import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.instance.handlers.InstanceHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 * @modified Luzien
 */
@AIName("empyreanrecordkeeper")
public class EmpyreanRecordKeeperAI2 extends NpcAI2 {

	@Override
	public void handleSpawned() {
		super.handleSpawned();
		int msg = 0;
		switch(getNpcId()) {
				case 799568:
					msg = 1111460;
					break;
				case 799569:
					msg = 1111461;
					break;
				case 205331:
					msg = 1111462;
					break;
				case 205338:
					msg = 1111463;
					break;
				case 205339:
					msg = 1111464;
					break;
				case 205340:
					msg = 1111465;
					break;
				case 205341:
					msg = 1111466;
					break;
				case 205342:
					msg = 1111467;
					break;
				case 205343:
					msg = 1111468;
					break;
				case 205344:
					msg = 1111469;
		}
		if (msg != 0) {
			NpcShoutsService.getInstance().sendMsg(getOwner(), msg, getObjectId(), 25, 1000);
		}
	}

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		InstanceHandler instanceHandler = getPosition().getWorldMapInstance().getInstanceHandler();
		if (dialogId == DialogAction.SETPRO1.id()) {
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			switch (getNpcId()) {
				case 799567:
					instanceHandler.onChangeStage(StageType.START_STAGE_1_ELEVATOR);
					break;
				case 799568:
					instanceHandler.onChangeStage(StageType.START_STAGE_2_ELEVATOR);
					break;
				case 799569:
					instanceHandler.onChangeStage(StageType.START_STAGE_3_ELEVATOR);
					break;
				case 205331:
					instanceHandler.onChangeStage(StageType.START_STAGE_4_ELEVATOR);
					break;
				case 205338: // teleport to stage 5
					instanceHandler.onChangeStage(StageType.START_STAGE_5);
					break;
				case 205332:
					instanceHandler.onChangeStage(StageType.START_STAGE_5_ROUND_1);
					break;
				case 205339: // teleport to stage 6
					instanceHandler.onChangeStage(StageType.START_STAGE_6);
					break;
				case 205333:
					instanceHandler.onChangeStage(StageType.START_STAGE_6_ROUND_1);
					break;
				case 205340: // teleport to stage 7
					instanceHandler.onChangeStage(StageType.START_STAGE_7);
					break;
				case 205334:
					instanceHandler.onChangeStage(StageType.START_STAGE_7_ROUND_1);
					break;
				case 205341: // teleport to stage 8
					instanceHandler.onChangeStage(StageType.START_STAGE_8);
					break;
				case 205335:
					instanceHandler.onChangeStage(StageType.START_STAGE_8_ROUND_1);
					break;
				case 205342: // teleport to stage 9
					instanceHandler.onChangeStage(StageType.START_STAGE_9);
					break;
				case 205336:
					instanceHandler.onChangeStage(StageType.START_STAGE_9_ROUND_1);
					break;
				case 205343: // teleport to stage 9
					instanceHandler.onChangeStage(StageType.START_STAGE_10);
					break;
				case 205337:
					instanceHandler.onChangeStage(StageType.START_STAGE_10_ROUND_1);
					break;
				case 205344: // get score
 					getPosition().getWorldMapInstance().getInstanceHandler().doReward(player);
					break;
			}
			AI2Actions.deleteOwner(this);
		}
		else if (dialogId == 10001 && getNpcId() == 799567) { //start with stage 7
			PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
			instanceHandler.onChangeStage(StageType.START_STAGE_7);
			AI2Actions.deleteOwner(this);
		}
		return true;
	}
}
