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
package org.typezero.gameserver.model.siege;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import org.typezero.gameserver.model.templates.siegelocation.SiegeReward;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOVE;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.WorldPosition;
import org.typezero.gameserver.world.zone.ZoneInstance;
import java.util.List;

/**
 * @author Source
 */
public class SourceLocation extends SiegeLocation {

	protected List<SiegeReward> siegeRewards;
	private boolean status;

	public SourceLocation() {
	}

	public SourceLocation(SiegeLocationTemplate template) {
		super(template);
		this.siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
	}

	public List<SiegeReward> getReward() {
		return this.siegeRewards;
	}

	public boolean isPreparations() {
		return status;
	}

	public void setPreparation(boolean status) {
		this.status = status;
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		super.onEnterZone(creature, zone);
		if (isVulnerable())
			creature.setInsideZoneType(ZoneType.SIEGE);
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		super.onLeaveZone(creature, zone);
		if (isVulnerable())
			creature.unsetInsideZoneType(ZoneType.SIEGE);
	}

	/*
	 * TODO: move to datapack
	 */
	public WorldPosition getEntryPosition() {
		WorldPosition pos = new WorldPosition();
		pos.setMapId(getWorldId());
		switch (getLocationId()) {
			case 4011:
				pos.setXYZH(332.14316f, 854.36053f, 313.98f, (byte) 77);
				break;
			case 4021:
				pos.setXYZH(2353.9065f, 378.1945f, 237.8031f, (byte) 113);
				break;
			case 4031:
				pos.setXYZH(879.23627f, 2712.4644f, 254.25073f, (byte) 85);
				break;
			case 4041:
				pos.setXYZH(2901.2354f, 2365.0383f, 339.1469f, (byte) 39);
				break;
		}

		return pos;
	}

	@Override
	public void clearLocation() {
		for (Player player : getPlayers().values()) {
			WorldPosition pos = getEntryPosition();
			World.getInstance().updatePosition(player, pos.getX(), pos.getY(), pos.getZ(), player.getHeading());
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_MOVE(player));
		}
	}

}
