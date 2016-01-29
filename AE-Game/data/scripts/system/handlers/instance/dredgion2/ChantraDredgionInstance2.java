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

package instance.dredgion2;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.actions.NpcActions;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 *
 * @author xTz
 */
@InstanceID(300210000)
public class ChantraDredgionInstance2 extends DredgionInstance2 {

	@Override
	public void onEnterInstance(Player player) {
		if (isInstanceStarted.compareAndSet(false, true)) {
			sp(730311, 554.83081f, 173.87158f, 432.52448f, (byte) 0, 9, 720000);
			sp(730312, 397.11661f, 184.29782f, 432.80328f, (byte) 0, 42, 720000);
			if (Rnd.get(1, 100) < 21) {
				sp(216889, 484.1199f, 314.08817f, 403.7213f, (byte) 5, 720000);
			}
			if (Rnd.get(1, 100) < 21) {
				sp(216890, 499.52f,598.67f, 390.49f, (byte) 59, 720000);
			}
			if (Rnd.get(1, 100) < 21) {
				spawn(216887, 486.26382f, 909.48175f, 405.24463f, (byte) 90);
			}
			if (Rnd.get(1, 100) < 51) {
				switch(Rnd.get(2)) {
					case 0:
						spawn(216888, 416.3429f, 282.32785f, 409.7311f, (byte) 80);
						break;
					default:
						spawn(216888, 552.07446f, 289.058f, 409.7311f, (byte) 80);
						break;
				}
			}

			int spawnTime = Rnd.get(10,15) * 60 * 1000 + 120000;
			sendMsgByRace(1400633, Race.PC_ALL, spawnTime);
			sp(216941, 485.99f, 299.23f, 402.57f, (byte) 30, spawnTime);
			startInstanceTask();
		}
		super.onEnterInstance(player);
	}

	private void onDieSurkan(Npc npc, Player mostPlayerDamage, int points) {
		Race race = mostPlayerDamage.getRace();
		captureRoom(race, npc.getNpcId() + 14 - 700851);
		for (Player player : instance.getPlayersInside()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400199, new DescriptionId(race.equals(Race.ASMODIANS) ? 1800483 : 1800481), new DescriptionId(npc.getObjectTemplate().getNameId() * 2 + 1)));
		}
		getPlayerReward(mostPlayerDamage).captureZone();
		if (++surkanKills == 5) {
			spawn(216886, 485.33f, 832.26f, 416.64f, (byte) 55);
			sendMsgByRace(1400632, Race.PC_ALL, 0);
		}
		updateScore(mostPlayerDamage, npc, points, false);
		NpcActions.delete(npc);
	}

	@Override
	public void onDie(Npc npc) {
		switch (npc.getNpcId()) {
			case 730350: // Secondary Hatch teleporter
				sendMsgByRace(1400641, Race.PC_ALL, 0);
				spawn(730315, 415.07663f, 173.85265f, 432.53436f, (byte) 0, 34);
				NpcActions.delete(npc);
				return;
			case 730349: // Escape Hatch teleporter
				sendMsgByRace(1400631, Race.PC_ALL, 0);
				spawn(730314, 396.979f, 184.392f, 433.940f, (byte) 0, 42);
				NpcActions.delete(npc);
				return;
			case 730351:
				sendMsgByRace(1400226, Race.PC_ALL, 0);
				spawn(730345, 448.391998f, 493.641998f, 394.131989f, (byte) 90, 12);
				NpcActions.delete(npc);
				return;
			case 730352:
				sendMsgByRace(1400227, Race.PC_ALL, 0);
				spawn(730346, 520.875977f, 493.401001f, 394.433014f, (byte) 90, 133);
				NpcActions.delete(npc);
				return;
			case 216890:
			case 216889:
				return;
		}
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
		if (mostPlayerDamage == null) {
			return;
		}
		Race race = mostPlayerDamage.getRace();
		switch (npc.getNpcId()) {
			case 700838:
			case 700839:
				onDieSurkan(npc, mostPlayerDamage, 400);
				return;
			case 700840:
			case 700848:
			case 700849:
			case 700850:
			case 700851:
				onDieSurkan(npc, mostPlayerDamage, 700);
				return;
			case 700845:
			case 700846:
				onDieSurkan(npc, mostPlayerDamage, 800);
				return;
			case 700847:
				onDieSurkan(npc, mostPlayerDamage, 900);
				return;
			case 700841:
			case 700842:
				onDieSurkan(npc, mostPlayerDamage, 1000);
				return;
			case 700843:
			case 700844:
				onDieSurkan(npc, mostPlayerDamage, 1100);
				return;
			case 216882: // Captain's Cabin teleport
				sendMsgByRace(1400652, Race.PC_ALL, 0);
				if (race.equals(Race.ASMODIANS)) {
					spawn(730358, 496.178f, 761.770f, 390.805f, (byte) 0, 186);
				}
				else {
					spawn(730357, 473.759f, 761.864f, 390.805f, (byte) 0, 33);
				}
				break;
			case 700836:
				updateScore(mostPlayerDamage, npc, 100, false);
				NpcActions.delete(npc);
				return;
			case 216886:
				if (!dredgionReward.isRewarded()) {
					updateScore(mostPlayerDamage, npc, 1000, false);
					Race winningRace = dredgionReward.getWinningRaceByScore();
					stopInstance(winningRace);
				}
//				if (winningRace.equals(Race.ELYOS)) {
//					sendMsgByRace(1400230, Race.ELYOS, 0);
//				}
//				else {
//					sendMsgByRace(1400231, Race.ASMODIANS, 0);
//				}
				return;
			case 216941:
				updateScore(mostPlayerDamage, npc, 1000, false);
				return;
			case 216885:
				updateScore(mostPlayerDamage, npc, 500, false);
				return;
		}
		super.onDie(npc);
	}

	@Override
	protected void openFirstDoors() {
		openDoor(4);
		openDoor(173);
	}
}
