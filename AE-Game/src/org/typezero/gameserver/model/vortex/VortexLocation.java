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

package org.typezero.gameserver.model.vortex;

import org.typezero.gameserver.controllers.RVController;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.vortex.HomePoint;
import org.typezero.gameserver.model.templates.vortex.ResurrectionPoint;
import org.typezero.gameserver.model.templates.vortex.StartPoint;
import org.typezero.gameserver.model.templates.vortex.VortexTemplate;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.vortexservice.DimensionalVortex;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.zone.InvasionZoneInstance;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.handler.ZoneHandler;
import java.util.ArrayList;
import java.util.List;
import javolution.util.FastMap;

/**
 * @author Source
 */
public class VortexLocation implements ZoneHandler {

	protected boolean isActive;
	protected DimensionalVortex activeVortex;
	protected RVController vortexController;
	protected VortexTemplate template;
	protected int id;
	protected Race offenceRace;
	protected Race defendsRace;
	protected List<InvasionZoneInstance> zones;
	protected FastMap<Integer, Player> players = new FastMap<Integer, Player>();
	protected FastMap<Integer, Kisk> kisks = new FastMap<Integer, Kisk>();
	private final List<VisibleObject> spawned = new ArrayList<VisibleObject>();
	protected HomePoint home;
	protected ResurrectionPoint resurrection;
	protected StartPoint start;

	public VortexLocation() {
	}

	public VortexLocation(VortexTemplate template) {
		this.template = template;
		this.id = template.getId();
		this.offenceRace = template.getInvadersRace();
		this.defendsRace = template.getDefendersRace();
		this.zones = new ArrayList<InvasionZoneInstance>();
		this.home = template.getHomePoint();
		this.resurrection = template.getResurrectionPoint();
		this.start = template.getStartPoint();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActiveVortex(DimensionalVortex vortex) {
		isActive = vortex != null;
		this.activeVortex = vortex;
	}

	public DimensionalVortex getActiveVortex() {
		return activeVortex;
	}

	public void setVortexController(RVController controller) {
		this.vortexController = controller;
	}

	public RVController getVortexController() {
		return vortexController;
	}

	public final VortexTemplate getTemplate() {
		return template;
	}

	public WorldPosition getHomePoint() {
		return home.getHomePoint();
	}

	public WorldPosition getResurrectionPoint() {
		return resurrection.getResurrectionPoint();
	}

	public WorldPosition getStartPoint() {
		return start.getStartPoint();
	}

	public int getId() {
		return id;
	}

	public Race getDefendersRace() {
		return defendsRace;
	}

	public Race getInvadersRace() {
		return offenceRace;
	}

	public int getHomeWorldId() {
		return home.getWorldId();
	}

	public int getInvasionWorldId() {
		return start.getWorldId();
	}

	public List<VisibleObject> getSpawned() {
		return spawned;
	}

	public FastMap<Integer, Player> getPlayers() {
		return players;
	}

	public FastMap<Integer, Kisk> getInvadersKisks() {
		return kisks;
	}

	public boolean isInvaderInside(int objId) {
		return isActive() && getVortexController().getPassedPlayers().containsKey(objId);
	}

	public boolean isInsideActiveVotrex(Player player) {
		return isActive() && isInsideLocation(player);
	}

	public void addZone(InvasionZoneInstance zone) {
		this.zones.add(zone);
		zone.addHandler(this);
	}

	public boolean isInsideLocation(Creature creature) {
		if (zones.isEmpty()) {
			return false;
		}
		for (int i = 0; i < zones.size(); i++) {
			if (zones.get(i).isInsideCreature(creature)) {
				return true;
			}
		}
		return false;
	}

	public List<InvasionZoneInstance> getZones() {
		return zones;
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		if (creature instanceof Kisk) {
			if (creature.getRace().equals(getInvadersRace())) {
				kisks.putEntry(creature.getObjectId(), (Kisk) creature);
			}
		}
		else if (creature instanceof Player) {
			Player player = (Player) creature;

//			if (player.isGM()) {
//				return;
//			}

			if (!players.containsKey(player.getObjectId())) {
				players.putEntry(player.getObjectId(), player);

				if (isActive()) {
					if (player.getRace().equals(getInvadersRace())) {
						if (getVortexController().getPassedPlayers().containsKey(player.getObjectId())
								&& !getActiveVortex().getInvaders().containsKey(player.getObjectId())) {
							getActiveVortex().addPlayer(player, true);
						}
					}
					else {
						getActiveVortex().updateDefenders(player);
					}
				}
			}
		}
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		if (!isInsideLocation(creature)) {
			if (creature instanceof Kisk) {
				kisks.remove(creature.getObjectId());
			}
			if (creature instanceof Player) {
				final Player player = (Player) creature;

//			if (player.isGM()) {
//				return;
//			}

				players.remove(player.getObjectId());

				if (isActive()) {
					if (player.getRace().equals(getInvadersRace())) {
						if (getVortexController().getPassedPlayers().containsKey(player.getObjectId())) {
							// You have left the battlefield.
							PacketSendUtility.sendPacket(player, new SM_SYSTEM_MESSAGE(904305));

							// start kick timer
							ThreadPoolManager.getInstance().schedule(new Runnable() {
								@Override
								public void run() {
									if (player.isOnline() && !isInsideActiveVotrex(player)) {
										getActiveVortex().kickPlayer(player, true);
									}
								}

							}, 10 * 1000);
						}
					}
					else {
						// start kick timer
						ThreadPoolManager.getInstance().schedule(new Runnable() {
							@Override
							public void run() {
								if (player.isOnline() && !isInsideActiveVotrex(player)) {
									getActiveVortex().kickPlayer(player, false);
								}
							}

						}, 10 * 1000);
					}
				}
			}
		}
	}

}
