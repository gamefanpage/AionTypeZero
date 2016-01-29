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

import javolution.util.FastMap;
import org.typezero.gameserver.controllers.observer.ActionObserver;
import org.typezero.gameserver.controllers.observer.ObserverType;
import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.TeleportAnimation;
import org.typezero.gameserver.model.gameobjects.HouseObject;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.templates.housing.HouseType;
import org.typezero.gameserver.model.templates.zone.ZoneInfo;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_HOUSE;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_RENDER;
import org.typezero.gameserver.network.aion.serverpackets.SM_HOUSE_UPDATE;
import org.typezero.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.MathUtil;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.ThreadPoolManager;
import org.typezero.gameserver.world.World;

import java.util.List;

/**
 * @author Rolandas
 */
public class HouseController extends VisibleObjectController<House> {

	FastMap<Integer, ActionObserver> observed = new FastMap<Integer, ActionObserver>().shared();

	@Override
	public void see(VisibleObject object) {
		Player p = (Player) object;
		ActionObserver observer = new ActionObserver(ObserverType.MOVE);
		p.getObserveController().addObserver(observer);
		observed.put(p.getObjectId(), observer);
		AionServerPacket packet;
		if (getOwner().isInInstance())
			packet = new SM_HOUSE_UPDATE(getOwner());
		else
			packet = new SM_HOUSE_RENDER(getOwner());
		PacketSendUtility.sendPacket(p, packet);

		spawnObjects();
	}

	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		Player p = (Player) object;
		ActionObserver observer = observed.remove(p.getObjectId());
		if (isOutOfRange) {
			observer.moved();
			if (!getOwner().isInInstance())
				PacketSendUtility.sendPacket(p, new SM_DELETE_HOUSE(getOwner().getAddress().getId()));
		}
		p.getObserveController().removeObserver(observer);
	}

	public void spawnObjects() {
		if (getOwner().getRegistry() != null) {
			for (HouseObject<?> obj : getOwner().getRegistry().getSpawnedObjects()) {
				obj.spawn();
			}
		}
	}

	/**
	 * Used for owner player only
	 */
	public void updateAppearance() {
		ThreadPoolManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				for (int playerId : observed.keySet()) {
					Player player = World.getInstance().findPlayer(playerId);
					if (player == null)
						continue;
					PacketSendUtility.sendPacket(player, new SM_HOUSE_UPDATE(getOwner()));
				}
			}
		});
	}

	public void broadcastAppearance() {
		ThreadPoolManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				for (int playerId : observed.keySet()) {
					Player player = World.getInstance().findPlayer(playerId);
					if (player == null)
						continue;
					PacketSendUtility.sendPacket(player, new SM_HOUSE_RENDER(getOwner()));
				}
			}
		});
	}

	public void kickVisitors(Player kicker, boolean kickFriends, boolean onSettingsChange) {
		List<ZoneInfo> zoneInfo = DataManager.ZONE_DATA.getZones().get(getOwner().getWorldId());
		for (ZoneInfo info : zoneInfo) {
			if (info.getZoneTemplate().getName().name().equals(getOwner().getName())) {
				for (Integer objId : this.observed.keySet()) {
					if (objId == getOwner().getOwnerId())
						continue;
					if (!kickFriends && kicker != null && kicker.getFriendList().getFriend(objId) != null)
						continue;
					Player visitor = World.getInstance().findPlayer(objId);
					if (visitor != null) {
						if (visitor.isInsideZone(info.getZoneTemplate().getName())) {
							moveOutside(visitor, onSettingsChange);
						}
					}
				}
				break;
			}
		}
		if (kicker != null) {
			if (!kickFriends) {
				PacketSendUtility.sendPacket(kicker, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OUT_WITHOUT_FRIENDS);
			}
			else {
				PacketSendUtility.sendPacket(kicker, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_ORDER_OUT_ALL);
			}
		}
	}

	private void moveOutside(Player player, boolean onSettingsChange) {
		if (getOwner().getHouseType() == HouseType.STUDIO) {
			float x = getOwner().getAddress().getExitX();
			float y = getOwner().getAddress().getExitY();
			float z = getOwner().getAddress().getExitZ();
			TeleportService2.teleportTo(player, getOwner().getAddress().getExitMapId(), 1, x, y, z, player.getHeading(), TeleportAnimation.BEAM_ANIMATION);
		}
		else {
			Npc sign = getOwner().getCurrentSign();
			double radian = Math.toRadians(MathUtil.convertHeadingToDegree(sign.getHeading()));
			float x = (float) (sign.getX() + (8 * Math.cos(radian)));
			float y = (float) (sign.getY() + (8 * Math.sin(radian)));
			TeleportService2.teleportTo(player, getOwner().getWorldId(), 1, x, y, player.getZ() + 1, player.getHeading(),
							TeleportAnimation.BEAM_ANIMATION);
		}
		if (onSettingsChange)
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_CHANGE_OWNER);
		else
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_HOUSING_REQUEST_OUT);
	}
}
