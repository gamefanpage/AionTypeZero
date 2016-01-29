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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.templates.world.WeatherTable;
import org.typezero.gameserver.model.templates.zone.ZoneClassName;
import org.typezero.gameserver.services.WeatherService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.WorldMapType;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * Admin command allowing to change weathers of the world.
 *
 * @author Kwazar
 */
public class Weather extends AdminCommand {

	public Weather() {
		super("weather");
	}

	@Override
	public void execute(Player admin, String... params) {
		String regionName = null;

		if (params.length == 0) {
			int weatherCode = -1;
			List<ZoneInstance> zones = admin.getActiveRegion().getZones(admin);
			for (ZoneInstance regionZone : zones) {
				if (regionZone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER) {
					int weatherZoneId = DataManager.ZONE_DATA.getWeatherZoneId(regionZone.getZoneTemplate());
					weatherCode = WeatherService.getInstance().getWeatherCode(admin.getWorldId(), weatherZoneId);
					regionName = regionZone.getZoneTemplate().getXmlName();
					break;
				}
			}
			if (weatherCode == -1)
				PacketSendUtility.sendMessage(admin, "No weather.");
			else
				PacketSendUtility.sendMessage(admin, "Weather code for region " + regionName + " is " + weatherCode);
			return;
		}

		if (params.length > 2) {
			onFail(admin, null);
			return;
		}

		int weatherType = -1;
		regionName = new String(params[0]);

		if (params.length == 2) {
			try {
				weatherType = Integer.parseInt(params[1]);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "weather type parameter need to be an integer [0-12].");
				return;
			}
		}

		if (regionName.equals("reset")) {
			WeatherService.getInstance().resetWeather();
			return;
		}

		// Retrieving regionId by name
		WorldMapType region = null;
		for (WorldMapType worldMapType : WorldMapType.values()) {
			if (worldMapType.name().toLowerCase().equals(regionName.toLowerCase())) {
				region = worldMapType;
				break;
			}
		}

		if (region != null) {
			if (weatherType > -1 && weatherType < 13) {
				WeatherTable table = DataManager.MAP_WEATHER_DATA.getWeather(region.getId());
				if (table == null || table.getZoneCount() == 0) {
					PacketSendUtility.sendMessage(admin, "Region has no weather defined");
					return;
				}
				/*
				 * if (table.getWeatherCount() < weatherType) { PacketSendUtility.sendMessage(admin,
				 * "Region has no such weather value; max is=" + table.getWeatherCount()); return; }
				 */
				WeatherService.getInstance().changeRegionWeather(region.getId(), weatherType);
			}
			else {
				PacketSendUtility.sendMessage(admin, "Weather type must be between 0 and 12");
				return;
			}
		}
		else {
			PacketSendUtility.sendMessage(admin, "Region " + regionName + " not found");
			return;
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //weather <regionName(poeta, ishalgen, etc ...)> <value(0->12)> OR //weather reset");
	}

}
