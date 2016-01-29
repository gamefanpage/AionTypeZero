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

import org.typezero.gameserver.model.DescriptionId;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.Kisk;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLegionReward;
import org.typezero.gameserver.model.templates.siegelocation.SiegeLocationTemplate;
import org.typezero.gameserver.model.templates.siegelocation.SiegeReward;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.zone.ZoneInstance;

import java.util.List;

/**
 * @author Source
 */
public class FortressLocation extends SiegeLocation {

	protected List<SiegeReward> siegeRewards;
	protected List<SiegeLegionReward> siegeLegionRewards;
	protected boolean isUnderAssault;

	public FortressLocation() {
	}

	public FortressLocation(SiegeLocationTemplate template) {
		super(template);
		this.siegeRewards = template.getSiegeRewards() != null ? template.getSiegeRewards() : null;
		this.siegeLegionRewards = template.getSiegeLegionRewards() != null ? template.getSiegeLegionRewards() : null;
	}

	public List<SiegeReward> getReward() {
		return this.siegeRewards;
	}

	public List<SiegeLegionReward> getLegionReward() {
		return this.siegeLegionRewards;
	}

	/**
	 * @return isEnemy
	 */
	public boolean isEnemy(Creature creature) {
		return creature.getRace().getRaceId() != getRace().getRaceId();
	}

	/**
	 * @return isCanTeleport
	 */
	@Override
	public boolean isCanTeleport(Player player) {
		if (player == null)
			return canTeleport;
		return canTeleport && player.getRace().getRaceId() == getRace().getRaceId();
	}

	/**
	 * @return DescriptionId object with fortress name
	 */
	public DescriptionId getNameAsDescriptionId() {
		return new DescriptionId(template.getNameId());
	}

	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		super.onEnterZone(creature, zone);
		if (this.isVulnerable())
			creature.setInsideZoneType(ZoneType.SIEGE);
	}

	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		super.onLeaveZone(creature, zone);
		if (this.isVulnerable())
			creature.unsetInsideZoneType(ZoneType.SIEGE);
	}

	@Override
	public void clearLocation() {
		// TODO: not allow to place Kisk if siege will be soon
		for (Creature creature : getCreatures().values()) {
			if (isEnemy(creature)) {
				if (creature instanceof Kisk) {
					Kisk kisk = (Kisk) creature;
					kisk.getController().die();
				}
			}
		}

		for (Player player : getPlayers().values())
			if (isEnemy(player))
				TeleportService2.moveToBindLocation(player, true);
	}

}
