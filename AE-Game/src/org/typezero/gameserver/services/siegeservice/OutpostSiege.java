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
package org.typezero.gameserver.services.siegeservice;

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.siege.OutpostLocation;
import org.typezero.gameserver.model.siege.SiegeModType;
import org.typezero.gameserver.model.siege.SiegeRace;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.SiegeService;
import org.typezero.gameserver.services.player.PlayerService;
import org.typezero.gameserver.skillengine.SkillEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;
import java.util.Map;

/**
 * @author SoulKeeper
 */
public class OutpostSiege extends Siege<OutpostLocation> {

	public OutpostSiege(OutpostLocation siegeLocation) {
		super(siegeLocation);
	}

	@Override
	protected void onSiegeStart() {
		SiegeService.getInstance().deSpawnNpcs(getSiegeLocationId());

		getSiegeLocation().setVulnerable(true);

		SiegeService.getInstance().spawnNpcs(getSiegeLocationId(), getSiegeLocation().getRace(), SiegeModType.SIEGE);
		initSiegeBoss();

		// TODO: Refactor me
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(getSiegeLocationId() == 2111 ? 1400317 : 1400318));
			}

		});

		broadcastUpdate(getSiegeLocation());
	}

	@Override
	protected void onSiegeFinish() {
		getSiegeLocation().setVulnerable(false);
		unregisterSiegeBossListeners();

		// TODO: Refactor messages
		if (isBossKilled())
			onCapture();
		else {
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(getSiegeLocationId() == 2111 ? 1400319 : 1400320));
				}

			});
		}

		broadcastUpdate(getSiegeLocation());
	}

	private void onCapture() {
		SiegeRaceCounter winnerCounter = getSiegeCounter().getWinnerRaceCounter();
		Map<Integer, Long> topPlayerDamages = winnerCounter.getPlayerDamageCounter();
		if (!topPlayerDamages.isEmpty()) {

			// prepare top player
			Integer topPlayer = topPlayerDamages.keySet().iterator().next();
			final String topPlayerName = PlayerService.getPlayerName(topPlayer);
			// Prepare message for sending to all players
			int messageId = getSiegeLocationId() == 2111 ? 1400324 : 1400323;
			final Race race = winnerCounter.getSiegeRace() == SiegeRace.ELYOS ? Race.ELYOS : Race.ASMODIANS;
			final AionServerPacket asp = new SM_SYSTEM_MESSAGE(messageId, race, topPlayerName);

			// send packet for all players
			World.getInstance().doOnAllPlayers(new Visitor<Player>() {

				@Override
				public void visit(Player player) {
					PacketSendUtility.sendPacket(player, asp);
					if (player.getRace().equals(race)) {
						SkillEngine.getInstance().applyEffectDirectly(race == Race.ELYOS ? 12120 : 12119, player, player, 0);
					}
				}

			});
		}
	}

	@Override
	public boolean isEndless() {
		return false;
	}

	@Override
	public void addAbyssPoints(Player player, int abysPoints) {
		// No need to control AP
	}

}
