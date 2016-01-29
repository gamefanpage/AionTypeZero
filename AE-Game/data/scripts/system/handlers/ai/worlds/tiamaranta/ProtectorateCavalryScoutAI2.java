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

package ai.worlds.tiamaranta;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.ai2.NpcAI2;
import org.typezero.gameserver.ai2.manager.WalkManager;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.network.aion.serverpackets.SM_EMOTION;
import org.typezero.gameserver.services.NpcShoutsService;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author xTz
 */
@AIName("protectorate_cavalry_scout")
public class ProtectorateCavalryScoutAI2 extends NpcAI2 {
	private int size;

	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		spawnEventNpc();
	}

	private void spawnEventNpc() {
		size ++;
		int npcId = 0;
		switch(Rnd.get(1, 3)) {
			case 1:
				npcId = 799991;
				break;
			case 2:
				npcId = 799992;
				break;
			case 3:
				npcId = 799993;
				break;
		}
		int msg = 0;
		switch(Rnd.get(1, 3)) {
			case 1:
				msg = 340937;
				break;
			case 2:
				msg = 340955;
				break;
			case 3:
				msg = 0;
				break;
		}
		Npc npc = (Npc) spawn(npcId, 131.34761f, 2770.1194f, 293.92636f, (byte) 100);
		npc.getSpawn().setWalkerId("6000300001");
		WalkManager.startWalking((NpcAI2) npc.getAi2());
		npc.setState(1);
		PacketSendUtility.broadcastPacket(npc, new SM_EMOTION(npc, EmotionType.START_EMOTE2, 0, npc.getObjectId()));
		if (msg != 0) {
			NpcShoutsService.getInstance().sendMsg(npc, msg, npc.getObjectId(), 0, 2000);
		}

	}

	@Override
	protected void handleCreatureMoved(Creature creature) {
		if (creature instanceof Npc) {
			Npc npc = (Npc) creature;
			int npcId = npc.getNpcId();
			if (npcId == 799991 || npcId == 799992 || npcId == 799993) {
				int point = npc.getMoveController().getCurrentPoint();
				if (point == 4 && size < 2) {
					spawnEventNpc();
				}
				else if (point == 0) {
					npc.getMoveController().abortMove();
					npc.getSpawn().setWalkerId(null);
					WalkManager.stopWalking((NpcAI2) npc.getAi2());
					NpcActions.delete(npc);
					size --;
				}
			}
		}
		super.handleCreatureMoved(creature);
	}

}
