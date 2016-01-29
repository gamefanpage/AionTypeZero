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
@InstanceID(300440000)
public class TerathDredgionInstance2 extends DredgionInstance2 {

	@Override
	public void onEnterInstance(Player player) {
		if (isInstanceStarted.compareAndSet(false, true)) {
			sp(730558, 415.034f, 174.004f, 433.940f, (byte) 0, 34, 720000);
			sp(730559, 572.038f, 185.252f, 433.940f, (byte) 0, 10, 720000);
			sendMsgByRace(1401424, Race.PC_ALL, 720000);
		if (Rnd.get(1, 100) < 21) {
			sp(219265, 476.63f, 312.16f, 402.89807f, (byte) 97, 720000, "5540A84BAD08498B96C315281F6418D0BD825175");
			}
			if (Rnd.get(1, 100) < 21) {
				sp(219266, 485.403f, 596.602f, 390.944f, (byte) 90, 720000);
			}
			if (Rnd.get(1, 100) < 21) {
				sp(219333, 486.26382f, 906.011f, 405.24463f, (byte) 90, 720000);
			}
			if (Rnd.get(1, 100) < 51) {
				switch(Rnd.get(2)) {
					case 0:
						spawn(219255, 421.89111f, 285.20471f, 409.7311f, (byte) 80);
						break;
					default:
						spawn(219255, 551.407f, 289.058f, 409.7311f, (byte) 80);
						break;
				}
			}
			int spawnTime = Rnd.get(10,15) * 60 * 1000 + 120000;
			sendMsgByRace(1401417, Race.PC_ALL, spawnTime);
			sp(219270, 484.664f, 314.207f, 403.715f, (byte) 30, spawnTime);
			startInstanceTask();
		}
		super.onEnterInstance(player);
	}

	private void onDieSurkan(Npc npc, Player mostPlayerDamage, int points) {
		Race race = mostPlayerDamage.getRace();
		captureRoom(race, npc.getNpcId() + 14 - 701454);
		for (Player player : instance.getPlayersInside()) {
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400199, new DescriptionId(race.equals(Race.ASMODIANS) ? 1800483 : 1800481), new DescriptionId(npc.getObjectTemplate().getNameId() * 2 + 1)));
		}
		if (++surkanKills == 5) {
			spawn(219264, 485.423f, 808.826f, 416.868f, (byte) 30);
			sendMsgByRace(1401416, Race.PC_ALL, 0);
		}
		getPlayerReward(mostPlayerDamage).captureZone();
		updateScore(mostPlayerDamage, npc, points, false);
		NpcActions.delete(npc);
	}

	@Override
	public void onDie(Npc npc) {
		Player mostPlayerDamage = npc.getAggroList().getMostPlayerDamage();
		if (mostPlayerDamage == null) {
			return;
		}
		Race race = mostPlayerDamage.getRace();
		switch (npc.getNpcId()) {
			case 701441:
			case 701442:
				onDieSurkan(npc, mostPlayerDamage, 400);
				return;
			case 701443:
			case 701451:
			case 701452:
			case 701453:
			case 701454:
				onDieSurkan(npc, mostPlayerDamage, 700);
				return;
			case 701448:
			case 701449:
				onDieSurkan(npc, mostPlayerDamage, 800);
				return;
			case 701450:
				onDieSurkan(npc, mostPlayerDamage, 900);
				return;
			case 701444:
			case 701445:
				onDieSurkan(npc, mostPlayerDamage, 1000);
				return;
			case 701446:
			case 701447:
				onDieSurkan(npc, mostPlayerDamage, 1100);
				return;
			case 730572:
				spawn(730566, 446.729f, 493.224f, 395.938f, (byte) 0, 12);
				NpcActions.delete(npc);
				return;
			case 730573:
				spawn(730567, 520.404f, 493.261f, 395.938f, (byte) 0, 133);
				NpcActions.delete(npc);
				return;
			case 730570:
				sendMsgByRace(1401418, race, 0);
				spawn(730560, 396.979f, 184.392f, 433.940f, (byte) 0, 42);
				break;
			case 730571:
				sendMsgByRace(1401418, race, 0);
				spawn(730561, 554.64f, 173.535f, 433.940f, (byte) 0, 9);
				break;
			case 219271:
				sendMsgByRace(1401419, Race.PC_ALL, 0);
				if (race.equals(Race.ASMODIANS)) {
					spawn(730563, 496.178f, 761.770f, 390.805f, (byte) 0, 186);
				}
				else {
					spawn(730562, 473.759f, 761.864f, 390.805f, (byte) 0, 33);
				}
				return;
			case 219270:
				updateScore(mostPlayerDamage, npc, 1000, false);
				return;
			case 219264:
				if (!dredgionReward.isRewarded()) {
					updateScore(mostPlayerDamage, npc, 1000, false);
					Race winningRace = dredgionReward.getWinningRaceByScore();
					stopInstance(winningRace);
				}
				return;
			case 701439:
				updateScore(mostPlayerDamage, npc, 100, false);
				NpcActions.delete(npc);
				return;
		}
		super.onDie(npc);
	}

	@Override
	protected void openFirstDoors() {
		openDoor(173);
		openDoor(4);
	}

}
