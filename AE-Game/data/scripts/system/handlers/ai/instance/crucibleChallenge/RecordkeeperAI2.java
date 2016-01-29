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

package ai.instance.crucibleChallenge;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AI2Actions;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.instance.handlers.InstanceHandler;
import org.typezero.gameserver.model.DialogAction;
import org.typezero.gameserver.model.instance.StageType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
@AIName("recordkeeper")
public class RecordkeeperAI2 extends NpcAI2 {

	@Override
	protected void handleDialogStart(Player player) {
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 1011));
	}

	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		int instanceId = getPosition().getInstanceId();
		InstanceHandler instanceHandler = getPosition().getWorldMapInstance().getInstanceHandler();
		if (dialogId == DialogAction.SETPRO1.id()) {
			switch (getNpcId()) {
				case 205668: // start stage 1
					instanceHandler.onChangeStage(StageType.START_STAGE_1_ROUND_1);
					break;
				case 205674: // move to stage 2
					TeleportService2.teleportTo(player, 300320000, instanceId, 1796.5513f, 306.9967f, 469.25f, (byte) 60);
					spawn(205683, 1821.5643f, 311.92484f, 469.4562f, (byte) 60);
					spawn(205669, 1784.4633f, 306.98645f, 469.25f, (byte) 0);
					break;
				case 205669: // start stage 2
					instanceHandler.onChangeStage(StageType.START_STAGE_2_ROUND_1);
					break;
				case 205675: // move to stage 3
					TeleportService2.teleportTo(player, 300320000, instanceId, 1324.433f, 1738.2279f, 316.476f, (byte) 70);
					spawn(205684, 1358.4021f, 1758.744f, 319.1873f, (byte) 70);
					spawn(205670, 1307.5472f, 1732.9865f, 316.0777f, (byte) 6);
					break;
				case 205670: // start stage 3
					instanceHandler.onChangeStage(StageType.START_STAGE_3_ROUND_1);
					break;
				case 205676: // movet to stage 4
					switch (Rnd.get(1, 2)) {
						case 1:
							TeleportService2.teleportTo(player, 300320000, instanceId, 1283.1246f, 791.6683f, 436.6403f, (byte) 60);
							spawn(205685, 1308.9664f, 796.20276f, 437.29678f, (byte) 60);
							spawn(205671, 1271.4222f, 791.36145f, 436.64017f, (byte) 0);
							break;
						case 2:
							TeleportService2.teleportTo(player, 300320000, instanceId, 1270.8877f, 237.93307f, 405.38028f, (byte) 60);
							spawn(205663, 1295.7217f, 242.15009f, 406.03677f, (byte) 60);
							spawn(205666, 1258.7214f, 237.85518f, 405.3968f, (byte) 0);
							break;
					}
					break;
				case 205666: // start stage 4
					instanceHandler.onChangeStage(StageType.START_ALTERNATIVE_STAGE_4_ROUND_1);
					break;
				case 205671:
					instanceHandler.onChangeStage(StageType.START_STAGE_4_ROUND_1);
					break;
				case 205667: // move to stage 5
				case 205677:
					TeleportService2.teleportTo(player, 300320000, instanceId, 357.98798f, 349.19116f, 96.09108f, (byte) 60);
					spawn(205686, 383.30933f, 354.07846f, 96.07846f, (byte) 60);
					spawn(205672, 346.52298f, 349.25586f, 96.0098f, (byte) 0);
					break;
				case 205672: // start stage 5
					instanceHandler.onChangeStage(StageType.START_STAGE_5_ROUND_1);
					break;
				case 205678: // move to stage 6
					TeleportService2.teleportTo(player, 300320000, instanceId, 1759.5004f, 1273.5414f, 389.11743f, (byte) 10);
					spawn(205687, 1747.3901f, 1250.201f, 389.11765f, (byte) 16);
					spawn(205673, 1767.1036f, 1288.4425f, 389.11728f, (byte) 76);
					break;
				case 205673: // start stage 6
					instanceHandler.onChangeStage(StageType.START_STAGE_6_ROUND_1);
					break;
				case 205679: // get score
					getPosition().getWorldMapInstance().getInstanceHandler().doReward(player);
					break;
			}
			if (getNpcId() != 205679) {
				AI2Actions.deleteOwner(this);
			}
		}
		PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(getObjectId(), 0));
		return true;
	}
}
