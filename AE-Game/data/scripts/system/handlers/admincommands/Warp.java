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

import org.typezero.gameserver.configs.main.GeoDataConfig;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.teleport.TeleportService2;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.geo.GeoService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Source
 */
public class Warp extends AdminCommand {

	public Warp() {
		super("warp");
	}

	@Override
	public void execute(Player player, String... params) {
		String locS, first, last;
		float xF, yF, zF;
		locS = "";
		int mapL = 0;
		int layerI = -1;

		if(params.length < 5) {
			onFail(player, "");
			return;
		}

		first = params[0];
		xF = Float.parseFloat(params[1]);
		yF = Float.parseFloat(params[2]);
		zF = Float.parseFloat(params[3]);
		last = params[4];

		Pattern f = Pattern.compile("\\[pos:([^;]+);\\s*+(\\d{9})");
		Pattern l = Pattern.compile("(\\d)\\]");
		Matcher fm = f.matcher(first);
		Matcher lm = l.matcher(last);

		if (fm.find()) {
			locS = fm.group(1);
			mapL = Integer.parseInt(fm.group(2));
		}
		if (lm.find())
			layerI = Integer.parseInt(lm.group(1));

		zF = GeoService.getInstance().getZ(mapL, xF, yF);
		PacketSendUtility.sendMessage(player, "MapId (" + mapL + ")\n" + "x:" + xF + " y:" + yF + " z:" + zF + " l("
			+ layerI + ")");

		if (mapL == 400010000)
			PacketSendUtility.sendMessage(player, "Sorry you can't warp at abyss");
		else {
			TeleportService2.teleportTo(player, mapL, xF, yF, zF);
			PacketSendUtility.sendMessage(player, "You have successfully warp -> " + locS);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		if (!GeoDataConfig.GEO_ENABLE) {
			PacketSendUtility.sendMessage(player, "You must turn on geo in config to use this command!");
			return;
		}
		PacketSendUtility.sendMessage(player, "syntax //warp <@link>");
	}

}
