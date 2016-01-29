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

package org.typezero.gameserver.controllers;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.RequestResponseHandler;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceService;
import org.typezero.gameserver.model.team2.group.PlayerGroup;
import org.typezero.gameserver.model.team2.group.PlayerGroupService;
import org.typezero.gameserver.model.templates.spawns.SpawnTemplate;
import org.typezero.gameserver.model.vortex.VortexLocation;
import org.typezero.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.RiftService;
import org.typezero.gameserver.services.SerialKillerService;
import org.typezero.gameserver.services.VortexService;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.services.rift.RiftEnum;
import org.typezero.gameserver.services.rift.RiftInformer;
import org.typezero.gameserver.services.rift.RiftManager;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.audit.AuditLogger;
import javolution.util.FastMap;

/**
 * @author ATracer, Source
 */
public class RVController extends NpcController {

	private boolean isMaster = false;
	private boolean isVortex = false;
	protected FastMap<Integer, Player> passedPlayers = new FastMap<Integer, Player>();
	private SpawnTemplate slaveSpawnTemplate;
	private Npc slave;
	private Integer maxEntries;
	private Integer minLevel;
	private Integer maxLevel;
	private int usedEntries = 0;
	private boolean isAccepting;
	private RiftEnum riftTemplate;
	private int deSpawnedTime;

	/**
	 * Used to create master rifts or slave rifts (slave == null)
	 *
	 * @param slaveSpawnTemplate
	 */
	public RVController(Npc slave, RiftEnum riftTemplate) {
		this.riftTemplate = riftTemplate;
		this.isVortex = riftTemplate.isVortex();
		this.maxEntries = riftTemplate.getEntries();
		this.minLevel = riftTemplate.getMinLevel();
		this.maxLevel = riftTemplate.getMaxLevel();
		this.deSpawnedTime = ((int) (System.currentTimeMillis() / 1000)) + (isVortex
				? VortexService.getInstance().getDuration() * 3600
				: RiftService.getInstance().getDuration() * 3600);

		if (slave != null)// master rift should be created
		{
			this.slave = slave;
			this.slaveSpawnTemplate = slave.getSpawn();
			isMaster = true;
			isAccepting = true;
		}
	}

	@Override
	public void onDialogRequest(Player player) {
		if (!isMaster && !isAccepting) {
			return;
		}

		if (SerialKillerService.getInstance().isRestrictPortal(player)) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_CANNOT_USE_DIRECT_PORTAL_BY_SLAYER);
			return;
		}

		onRequest(player);
	}

	private void onRequest(Player player) {
		if (isVortex) {
			RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (onAccept(responder)) {
						if (responder.isInTeam()) {
							if (responder.getCurrentTeam() instanceof PlayerGroup) {
								PlayerGroupService.removePlayer(responder);
							}
							else {
								PlayerAllianceService.removePlayer(responder);
							}
						}

						VortexLocation loc = VortexService.getInstance().getLocationByRift(getOwner().getNpcId());
						TeleportService2.teleportTo(responder, loc.getStartPoint());

						// A Rift Portal battle has begun.
						PacketSendUtility.sendPacket(responder, new SM_SYSTEM_MESSAGE(1401454));

						// Update passed players count
						passedPlayers.put(responder.getObjectId(), responder);
						syncPassed(true);
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					onDeny(responder);
				}

			};

			boolean requested = player.getResponseRequester().putRequest(904304, responseHandler);
			if (requested) {
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(904304, getOwner().getObjectId(), 5));
			}
		}
		else {
			RequestResponseHandler responseHandler = new RequestResponseHandler(getOwner()) {
				@Override
				public void acceptRequest(Creature requester, Player responder) {
					if (onAccept(responder)) {
						int worldId = slaveSpawnTemplate.getWorldId();
						float x = slaveSpawnTemplate.getX();
						float y = slaveSpawnTemplate.getY();
						float z = slaveSpawnTemplate.getZ();

						TeleportService2.teleportTo(responder, worldId, x, y, z);
						// Update passed players count
						syncPassed(false);
					}
				}

				@Override
				public void denyRequest(Creature requester, Player responder) {
					onDeny(responder);
				}

			};

			boolean requested = player.getResponseRequester().putRequest(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, responseHandler);
			if (requested) {
				PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(SM_QUESTION_WINDOW.STR_ASK_PASS_BY_DIRECT_PORTAL, 0, 0));
			}
		}
	}

	private boolean onAccept(Player player) {
		if (!isAccepting) {
			return false;
		}

		if (!getOwner().isSpawned()) {
			return false;
		}

		if (player.getLevel() > getMaxLevel() || player.getLevel() < getMinLevel()) {
			AuditLogger.info(player, "Level restriction hack detected.");
			return false;
		}

		if (isVortex && getUsedEntries() >= getMaxEntries()) {
			return false;
		}

		return true;
	}

	private boolean onDeny(Player player) {
		return true;
	}

	@Override
	public void onDelete() {
		RiftInformer.sendRiftDespawn(getOwner().getWorldId(), getOwner().getObjectId());
		RiftManager.getSpawned().remove(getOwner());
		super.onDelete();
	}

	public boolean isMaster() {
		return isMaster;
	}

	public boolean isVortex() {
		return isVortex;
	}

	/**
	 * @return the maxEntries
	 */
	public Integer getMaxEntries() {
		return maxEntries;
	}

	/**
	 * @return the minLevel
	 */
	public Integer getMinLevel() {
		return minLevel;
	}

	/**
	 * @return the maxLevel
	 */
	public Integer getMaxLevel() {
		return maxLevel;
	}

	/**
	 * @return the riftTemplate
	 */
	public RiftEnum getRiftTemplate() {
		return riftTemplate;
	}

	/**
	 * @return slave rift
	 */
	public Npc getSlave() {
		return slave;
	}

	/**
	 * @return the usedEntries
	 */
	public int getUsedEntries() {
		return usedEntries;
	}

	public int getRemainTime() {
		return deSpawnedTime - (int) (System.currentTimeMillis() / 1000);
	}

	public FastMap<Integer, Player> getPassedPlayers() {
		return passedPlayers;
	}

	public void syncPassed(boolean invasion) {
		usedEntries = invasion ? passedPlayers.size() : ++usedEntries;
		RiftInformer.sendRiftInfo(getWorldsList(this));
	}

	private int[] getWorldsList(RVController controller) {
		int first = controller.getOwner().getWorldId();
		if (controller.isMaster()) {
			return new int[]{first, controller.slaveSpawnTemplate.getWorldId()};
		}
		return new int[]{first};
	}

}
