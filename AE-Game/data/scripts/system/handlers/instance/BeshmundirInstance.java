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

package instance;

import static org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE.*;

import com.aionemu.commons.utils.Rnd;
import org.typezero.gameserver.instance.handlers.GeneralInstanceHandler;
import org.typezero.gameserver.instance.handlers.InstanceID;
import org.typezero.gameserver.model.EmotionType;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.StaticDoor;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.*;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.Map;

/**
 * @author Gigi, nrg, oslo0322, xTz
 * TODO: Hard-/normal mode
 * TODO: AI for each boss
 * see http://raouooble.com/Beshmundir_Temple_Guide.html
 * see http://gameguide.na.aiononline.com/aion/Beshmundir+Temple+Walkthrough%3A+Hard+Mode
 *
 */
@InstanceID(300170000)
public class BeshmundirInstance extends GeneralInstanceHandler {

	private int macunbello = 0;
	private int kills;
	Npc npcMacunbello = null;
	private Map<Integer,StaticDoor> doors;

	@Override
	public void onDie(Npc npc) {
		switch(npc.getNpcId()) {
			case 216175: // Pahraza
				if (Rnd.get(100) > 10) {
					spawn(216764, 1437.2672f, 1579.4656f, 305.82492f, (byte) 97);
					sendMsg("Mystery Box spawned");
				}
				break;
			case 216179:
			case 216181:
			case 216177:
				int chance = Rnd.get(100);

				if (chance > 90) {
					switch (npc.getNpcId()) {
						case 216179:
							spawn(216764, 1625.5829f, 1493.408f, 329.94492f, (byte) 67);
							break;
						case 216181:
							spawn(216764, 1633.7206f, 1429.6768f, 305.83493f, (byte) 59);
							break;
						case 216177:
							spawn(216764, 1500.8236f, 1586.5652f, 329.94492f, (byte) 88);
							break;
					}
					sendMsg("Congratulation: Mystery Box spawned!\nChance: " + chance);
				}
				else {
					sendMsg("Chance: " + chance);
					switch (npc.getObjectTemplate().getTemplateId()) {
						case 216179: // Narma
							spawn(216173, 1546.5916f, 1471.214f, 300.33008f, (byte) 84);
							sendMsg("Gatekeeper Rhapsharr spawned");
							break;
						case 216181: // Kramaka
							spawn(216171, 1403.51f, 1475.79f, 307.793f, (byte) 98);
							sendMsg("Gatekeeper Kutarrun spawned");
							break;
						case 216177: // Dinata
							spawn(216170, 1499.78f, 1507.1f, 300.33f, (byte) 0);
							sendMsg("Gatekeeper Darfall spawned");
					}
				}
				break;
			case 216583:
				spawn(799518, 936.0029f, 441.51712f, 220.5029f, (byte) 28);
				break;
			case 216584:
				spawn(799519, 791.0439f, 439.79608f, 220.3506f, (byte) 28);
				break;
			case 216585:
				spawn(799520, 820.70624f, 278.828f, 220.19385f, (byte) 55);
				break;
			case 216586:
				if (macunbello < 12) {
					npcMacunbello = (Npc)spawn(216735, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // strongest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19046, npcMacunbello, npcMacunbello, 0);
				}
				else if (macunbello < 14) {
					npcMacunbello = (Npc)spawn(216734, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th strongest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19047, npcMacunbello, npcMacunbello, 0);
				}
				else if (macunbello < 21) {
					npcMacunbello = (Npc)spawn(216737, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // 2th weakest macunbello
					SkillEngine.getInstance().applyEffectDirectly(19048, npcMacunbello, npcMacunbello, 0);
				}
				else {
					spawn(216245, 981.015015f, 134.373001f, 241.755005f, (byte) 30); // weakest macunbello
				}
				macunbello = 0;
				sendPacket(new SM_QUEST_ACTION(0, 0));
				openDoor(467);
				break;
			case 799342:
				sendPacket(new SM_PLAY_MOVIE(0, 447));
				break;
			case 216238:
				openDoor(470);
				spawn(216159, 1357.0598f, 388.6637f, 249.26372f, (byte) 90);
				break;
			case 216246:
				openDoor(473);
				break;
			case 216739:
			case 216740:
				kills ++;
				if (kills < 10) {
					sendMsg(1400465);
				}
				else if (kills == 10) {
					sendMsg(1400470);
					spawn(216158, 1356.5719f, 147.76418f, 246.27373f, (byte) 91);
				}
				break;
			case 216158:
				openDoor(471);
				break;
			case 216263:
				// this is a safety Mechanism
				// super boss
				spawn(216264, 558.306f, 1369.02f, 224.795f, (byte) 70);
				// gate
				sendMsg(1400480);
				spawn(730275, 1611.1266f, 1604.6935f, 311.00503f, (byte) (byte) 0, 426);
				break;
			case 216250:  // Dorakiki the Bold
				sendMsg(1400471);
				spawn(216527, 1161.859985f, 1213.859985f, 284.057007f, (byte) 110); // Lupukin: cat trader
				break;
			case 216206:
			case 216207:
			case 216208:
			case 216209:
			case 216210:
			case 216211:
			case 216212:
			case 216213:
				macunbello ++;
				switch (macunbello) {
					case 12:
						sendMsg(1400466);
						break;
					case 14:
						sendMsg(1400467);
						break;
					case 21:
						sendMsg(1400468);
						break;
				}
				break;
		}
	}

	private void sendMsg(final String str) { // to do system message
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendMessage(player, str);
			}

		});
	}

	private void sendPacket(final AionServerPacket packet) {
		instance.doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, packet);
			}

		});
	}

	@Override
	public void onPlayMovieEnd(Player player, int movieId) {
		switch (movieId) {
			case 443:
				PacketSendUtility.sendPacket(player, STR_MSG_IDCatacombs_BigOrb_Spawn);
				break;
		}
	}

	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		doors = instance.getDoors();
		doors.get(535).setOpen(true);
	}

	private void openDoor(int doorId){
		StaticDoor door = doors.get(doorId);
		if (door != null)
			door.setOpen(true);
	}

	@Override
	public void onInstanceDestroy() {
		doors.clear();
	}

	@Override
	public boolean onDie(final Player player, Creature lastAttacker) {
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0
				: lastAttacker.getObjectId()), true);

		PacketSendUtility.sendPacket(player, new SM_DIE(player.haveSelfRezEffect(), player.haveSelfRezItem(), 0, 8));
		return true;
	}
}
