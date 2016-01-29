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

package zone.pvpZones;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.world.zone.ZoneName;
import org.typezero.gameserver.world.zone.handler.ZoneNameAnnotation;

/**
 * @author MrPoke
 */
@ZoneNameAnnotation(value = "LC1_PVP_SUB_C DC1_PVP_ZONE ARENA_301500000")
public class PvPAreaZone extends PvPZone {

	@Override
	protected void doTeleport(Player player, ZoneName zoneName) {
		if (zoneName == ZoneName.get("LC1_PVP_SUB_C")) {
			TeleportService2.teleportTo(player, 110010000, 1, 1470.3f, 1343.5f, 563.7f);
		}
		else if (zoneName == ZoneName.get("DC1_PVP_ZONE")) {
			TeleportService2.teleportTo(player, 120010000, 1, 1005.1f, 1528.9f, 222.1f);
		}
		else if (zoneName == ZoneName.get("ARENA_301500000")) {
			TeleportService2.teleportTo(player, 301500000, 1, 304.1779f, 265.082f, 97.5f);
		}
	}
}
