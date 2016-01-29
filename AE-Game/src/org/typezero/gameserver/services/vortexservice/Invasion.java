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

package org.typezero.gameserver.services.vortexservice;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.team2.TeamType;
import org.typezero.gameserver.model.team2.alliance.PlayerAlliance;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.model.vortex.VortexStateType;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class Invasion extends DimensionalVortex<VortexLocation> {

	PlayerAlliance invAlliance, defAlliance;
	protected FastMap<Integer, Player> invaders = new FastMap<Integer, Player>();
	protected FastMap<Integer, Player> defenders = new FastMap<Integer, Player>();

	public Invasion(VortexLocation vortex) {
		super(vortex);
	}

	@Override
	public void startInvasion() {
		getVortexLocation().setActiveVortex(this);
		despawn();
		spawn(VortexStateType.INVASION);
		initRiftGenerator();
		updateAlliance();
	}

	@Override
	public void stopInvasion() {
		getVortexLocation().setActiveVortex(null);
		unregisterSiegeBossListeners();
		for (Kisk kisk : getVortexLocation().getInvadersKisks().values()) {
			kisk.getController().die();
		}
		for (Player invader : invaders.values()) {
			if (invader.isOnline()) {
				kickPlayer(invader, true);
			}
		}
		despawn();
		spawn(VortexStateType.PEACE);
	}

	@Override
	public void addPlayer(Player player, boolean isInvader) {
		FastMap<Integer, Player> list = isInvader ? invaders : defenders;
		PlayerAlliance alliance = isInvader ? invAlliance : defAlliance;

		if (alliance != null && alliance.size() > 0) {
			PlayerAllianceService.addPlayer(alliance, player);
		}
		else if (!list.isEmpty()) {
			Player first = null;

			for (Player firstOne : list.values()) {
				if (firstOne.isInGroup2()) {
					PlayerGroupService.removePlayer(firstOne);
				}
				else if (firstOne.isInAlliance2()) {
					PlayerAllianceService.removePlayer(firstOne);
				}
				first = firstOne;
			}

			if (first.getObjectId() != player.getObjectId()) {
				if (isInvader) {
					invAlliance = PlayerAllianceService.createAlliance(first, player, TeamType.ALLIANCE_OFFENCE);
				}
				else {
					defAlliance = PlayerAllianceService.createAlliance(first, player, TeamType.ALLIANCE_DEFENCE);
				}
			}
			else {
				kickPlayer(player, isInvader);
			}
		}
		list.putEntry(player.getObjectId(), player);
	}

	@Override
	public void kickPlayer(Player player, boolean isInvader) {
		FastMap<Integer, Player> list = isInvader ? invaders : defenders;
		PlayerAlliance alliance = isInvader ? invAlliance : defAlliance;

		list.remove(player.getObjectId());

		if (alliance != null && alliance.hasMember(player.getObjectId())) {
			if (player.isOnline()) {
				PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(isInvader ? 1401452 : 1401476));
			}
			PlayerAllianceService.removePlayer(player);
			if (alliance.size() == 0) {
				if (isInvader) {
					invAlliance = null;
				}
				else {
					defAlliance = null;
				}
			}
		}

		if (isInvader && player.isOnline()
				&& player.getWorldId() == getVortexLocation().getInvasionWorldId()) {
			// You will be returned to where you entered.
			PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(1401474));
			TeleportService2.teleportTo(player, getVortexLocation().getHomePoint());
		}

		getVortexLocation().getVortexController().getPassedPlayers().remove(player.getObjectId());
		getVortexLocation().getVortexController().syncPassed(true);
	}

	@Override
	public void updateDefenders(Player defender) {
		if (defenders.containsKey(defender.getObjectId())) {
			return;
		}

		if (defAlliance == null || !defAlliance.isFull()) {
			RequestResponseHandler responseHandler = new RequestResponseHandler(defender) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (responder.isInGroup2()) {
						PlayerGroupService.removePlayer(responder);
					}
					else if (responder.isInAlliance2()) {
						PlayerAllianceService.removePlayer(responder);
					}

					if (defAlliance == null || !defAlliance.isFull()) {
						addPlayer(responder, false);
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					// do nothing
				}

			};

			boolean requested = defender.getResponseRequester().putRequest(904306, responseHandler);
			if (requested) {
				PacketSendUtility.sendPacket(defender, new SM_QUESTION_WINDOW(904306, 0, 0));
			}
		}
	}

	@Override
	public void updateInvaders(Player invader) {
		if (invaders.containsKey(invader.getObjectId())) {
			return;
		}

		addPlayer(invader, true);
	}

	private void updateAlliance() {
		for (Player player : getVortexLocation().getPlayers().values()) {
			if (player.getRace().equals(getVortexLocation().getDefendersRace())) {
				updateDefenders(player);
			}
		}
	}

	@Override
	public FastMap<Integer, Player> getInvaders() {
		return invaders;
	}

	@Override
	public FastMap<Integer, Player> getDefenders() {
		return defenders;
	}

}
