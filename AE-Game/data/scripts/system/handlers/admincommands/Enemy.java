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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_MOTION;
import org.typezero.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Pan
 */
public class Enemy extends AdminCommand {

	public Enemy() {
		super("enemy");
	}

	@Override
	public void execute(Player player, String... params) {
		String help = "Syntax: //enemy < players | npcs | all | cancel >\n"
			+ "Players - You're enemy to Players of both factions.\n" + "Npcs - You're enemy to all Npcs and Monsters.\n"
			+ "All - You're enemy to Players of both factions and all Npcs.\n"
			+ "Cancel - Cancel all. Players and Npcs have default enmity to you.";

		if (params.length != 1) {
			onFail(player, null);
			return;
		}

		String output = "You now appear as enemy to " + params[0] + ".";

		int neutralType = player.getAdminNeutral();

		if (params[0].equals("all")) {
			player.setAdminEnmity(3);
			player.setAdminNeutral(0);
		}

		else if (params[0].equals("players")) {
			player.setAdminEnmity(2);
			if (neutralType > 1)
				player.setAdminNeutral(0);
		}

		else if (params[0].equals("npcs")) {
			player.setAdminEnmity(1);
			if (neutralType == 1 || neutralType == 3)
				player.setAdminNeutral(0);
		}

		else if (params[0].equals("cancel")) {
			player.setAdminEnmity(0);
			output = "You appear regular to both Players and Npcs.";
		}

		else if (params[0].equals("help")) {
			PacketSendUtility.sendMessage(player, help);
			return;
		}

		else {
			onFail(player, null);
			return;
		}

		PacketSendUtility.sendMessage(player, output);

		player.clearKnownlist();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		player.updateKnownlist();
	}

	@Override
	public void onFail(Player player, String message) {
		String syntax = "Syntax: //enemy < players | npcs | all | cancel >\nIf you're unsure about what you want to do, type //enemy help";
		PacketSendUtility.sendMessage(player, syntax);
	}
}
