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

package ai.instance.tallocsHollow;

import ai.SummonerAI2;
import org.typezero.gameserver.ai2.AIName;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.Summon;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.summons.SummonMode;
import org.typezero.gameserver.model.summons.UnsummonType;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAY_MOVIE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.summons.SummonsService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.WorldMapInstance;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 *
 * @author xTz
 */
@AIName("queenmosqua")
public class QueenMosquaAI2 extends SummonerAI2 {
	private boolean isHome = true;

	@Override
	protected void handleCreatureAggro(Creature creature) {
		super.handleCreatureAggro(creature);
		if (isHome) {
			isHome = false;
			getPosition().getWorldMapInstance().getDoors().get(7).setOpen(false);
		}
	}

	@Override
	protected void handleBackHome() {
		isHome = true;
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);
		super.handleBackHome();
	}

	@Override
	protected void handleDied() {
		super.handleDied();
		WorldMapInstance instance = getPosition().getWorldMapInstance();
		getPosition().getWorldMapInstance().getDoors().get(7).setOpen(true);

		Npc npc = instance.getNpc(700738);
		if (npc != null) {
			SpawnTemplate template = npc.getSpawn();
			spawn(700739, template.getX(), template.getY(), template.getZ(), template.getHeading(), 11);
			npc.getKnownList().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1400476));
					Summon summon = player.getSummon();
					if (summon != null) {
						if (summon.getNpcId() == 799500 || summon.getNpcId() == 799501) {
							SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
							PacketSendUtility.sendPacket(player, new SM_PLAY_MOVIE(0, 435));
						}
					}
				}
			});
			npc.getController().onDelete();
		}
	}

}
