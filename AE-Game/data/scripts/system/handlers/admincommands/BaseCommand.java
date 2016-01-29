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

import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.base.BaseLocation;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.BaseService;
import org.typezero.gameserver.services.base.Base;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

import org.apache.commons.lang.math.NumberUtils;

@SuppressWarnings("rawtypes")
public class BaseCommand extends AdminCommand {

	private static final String COMMAND_LIST = "list";
	private static final String COMMAND_CAPTURE = "capture";
	private static final String COMMAND_ASSAULT = "assault";

	public BaseCommand() {
		super("base");
	}

	@Override
	public void execute(Player player, String... params) {

		if (params.length == 0) {
			showHelp(player);
			return;
		}

		if (COMMAND_LIST.equalsIgnoreCase(params[0])) {
			handleList(player, params);
		}
		else if (COMMAND_CAPTURE.equals(params[0])) {
			capture(player, params);
		}
		else if (COMMAND_ASSAULT.equals(params[0])) {
			assault(player, params);
		}
	}

	protected boolean isValidBaseLocationId(Player player, int baseId) {
		if (!BaseService.getInstance().getBaseLocations().keySet().contains(baseId)) {
			PacketSendUtility.sendMessage(player, "Id " + baseId + " is invalid");
			return false;
		}
		return true;
	}

	protected void handleList(Player player, String[] params) {
		if (params.length != 1) {
			showHelp(player);
			return;
		}

		for (BaseLocation base : BaseService.getInstance().getBaseLocations().values()) {
			PacketSendUtility.sendMessage(player, "Base:" + base.getId() + " belongs to " + base.getRace());
		}
	}

	protected void capture(Player player, String[] params) {
		if (params.length < 3 || !NumberUtils.isNumber(params[1])) {
			showHelp(player);
			return;
		}

		int baseId = NumberUtils.toInt(params[1]);
		if (!isValidBaseLocationId(player, baseId)) {
			return;
		}

		// check if params2 is race
		Race race = null;
		try {
			race = Race.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			//ignore
		}

		// check if can capture
		if (race == null) {
			PacketSendUtility.sendMessage(player, params[2] + " is not valid race");
			showHelp(player);
			return;
		}

		// capture
		Base base = BaseService.getInstance().getActiveBase(baseId);
		if (base != null) {
			BaseService.getInstance().capture(baseId, race);
		}
	}

	protected void assault(Player player, String[] params) {
		if (params.length < 3 || !NumberUtils.isNumber(params[1])) {
			showHelp(player);
			return;
		}

		int baseId = NumberUtils.toInt(params[1]);
		if (!isValidBaseLocationId(player, baseId)) {
			return;
		}

		// check if params2 is race
		Race race = null;
		try {
			race = Race.valueOf(params[2].toUpperCase());
		}
		catch (IllegalArgumentException e) {
			//ignore
		}

		// check if race is valid
		if (race == null) {
			PacketSendUtility.sendMessage(player, params[2] + " is not valid race");
			showHelp(player);
			return;
		}

		// assault
		Base base = BaseService.getInstance().getActiveBase(baseId);
		if (base != null) {
			if (base.isAttacked()) {
				PacketSendUtility.sendMessage(player, "Assault already started!");
			}
			else {
				base.spawnAttackers(race);
			}
		}
	}

	protected void showHelp(Player player) {
		PacketSendUtility.sendMessage(player, "AdminCommand //base Help\n"
				+ "//base list\n"
				+ "//base capture <Id> <Race (ELYOS,ASMODIANS,NPC)>\n"
				+ "//base assault <Id> <delaySec>");
	}

}
