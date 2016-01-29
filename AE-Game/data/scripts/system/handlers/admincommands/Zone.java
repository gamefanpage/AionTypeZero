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

package admincommands;

import java.util.List;

import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.zone.ZoneType;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.zone.ZoneInstance;
import org.typezero.gameserver.world.zone.ZoneName;

/**
 * @author ATracer
 */
public class Zone extends AdminCommand {

	public Zone() {
		super("zone");
	}

	@Override
	public void execute(Player admin, String... params) {
		Creature target;
		if (admin.getTarget() == null || !(admin.getTarget() instanceof Creature))
			target = admin;
		else
			target = (Creature) admin.getTarget();
		if (params.length == 0) {
			List<ZoneInstance> zones = target.getPosition().getMapRegion().getZones(target);
			if (zones.isEmpty()) {
				PacketSendUtility.sendMessage(admin, target.getName() + " are out of any zone");
			}
			else {
				PacketSendUtility.sendMessage(admin, target.getName() + " are in zone: ");
				PacketSendUtility.sendMessage(admin, "Registered zones:");
				if (admin.isInsideZoneType(ZoneType.DAMAGE))
					PacketSendUtility.sendMessage(admin, "DAMAGE");
				if (admin.isInsideZoneType(ZoneType.FLY))
					PacketSendUtility.sendMessage(admin, "FLY");
				if (admin.isInsideZoneType(ZoneType.PVP))
					PacketSendUtility.sendMessage(admin, "PVP");
				if (admin.isInsideZoneType(ZoneType.SIEGE))
					PacketSendUtility.sendMessage(admin, "CASTLE");
				if (admin.isInsideZoneType(ZoneType.WATER))
					PacketSendUtility.sendMessage(admin, "WATER");
				for (ZoneInstance zone : zones) {
					PacketSendUtility.sendMessage(admin, zone.getAreaTemplate().getZoneName().name());
					PacketSendUtility.sendMessage(admin, "Fly: " + zone.canFly() + "; Glide: " + zone.canGlide());
					PacketSendUtility.sendMessage(admin, "Ride: " + zone.canRide() + "; Fly-ride: " + zone.canFlyRide());
					PacketSendUtility.sendMessage(admin, "Kisk: " + zone.canPutKisk() + "; Racall: " + zone.canRecall());
					PacketSendUtility.sendMessage(admin, "Same race duels: " + zone.isSameRaceDuelsAllowed() + "; Other race duels: " + zone.isOtherRaceDuelsAllowed());
					PacketSendUtility.sendMessage(admin, "PvP: " + zone.isPvpAllowed());
				}
			}
		}
		else if ("?".equalsIgnoreCase(params[0])) {
			onFail(admin, null);
		}
		else if ("refresh".equalsIgnoreCase(params[0])) {
			admin.revalidateZones();
		}
		else if ("inside".equalsIgnoreCase(params[0])) {
			try {
				ZoneName name = ZoneName.get(params[1]);
				PacketSendUtility.sendMessage(admin, "isInsideZone: " + admin.isInsideZone(name));
			}
			catch (Exception e) {
				PacketSendUtility.sendMessage(admin, "Zone name missing!");
				PacketSendUtility.sendMessage(admin, "Syntax: //zone inside <zone name> ");
			}
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //zone refresh | inside");
	}
}
