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
 * @author Sarynth, (edited by Pan)
 */
public class Neutral extends AdminCommand {

	public Neutral() {
		super("neutral");
	}

	@Override
	public void execute(Player admin, String... params) {
		String help = "Syntax: //neutral < players | npcs | all | cancel >\n"
			+ "Players - You're neutral to Players of both factions.\n" + "Npcs - You're neutral to all Npcs and Monsters.\n"
			+ "All - You're neutral to Players of both factions and all Npcs.\n"
			+ "Cancel - Cancel all. Players and Npcs have default enmity to you.";

		if (params.length != 1) {
			onFail(admin, null);
			return;
		}

		String output = "You now appear neutral to " + params[0] + ".";

		int enemyType = admin.getAdminEnmity();

		if (params[0].equals("all")) {
			admin.setAdminNeutral(3);
			admin.setAdminEnmity(0);
		}

		else if (params[0].equals("players")) {
			admin.setAdminNeutral(2);
			if (enemyType > 1)
				admin.setAdminEnmity(0);
		}

		else if (params[0].equals("npcs")) {
			admin.setAdminNeutral(1);
			if (enemyType == 1 || enemyType == 3)
				admin.setAdminEnmity(0);
		}

		else if (params[0].equals("cancel")) {
			admin.setAdminNeutral(0);
			output = "You appear regular to both Players and Npcs.";
		}

		else if (params[0].equals("help")) {
			PacketSendUtility.sendMessage(admin, help);
			return;
		}

		else {
			onFail(admin, null);
			return;
		}

		PacketSendUtility.sendMessage(admin, output);

		admin.clearKnownlist();
		PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
		PacketSendUtility.sendPacket(admin, new SM_MOTION(admin.getObjectId(), admin.getMotions().getActiveMotions()));
		admin.updateKnownlist();
	}

	@Override
	public void onFail(Player player, String message) {
		String syntax = "Syntax: //neutral < players | npcs | all | cancel >\n"
			+ "If you're unsure about what you want to do, type //neutral help";
		PacketSendUtility.sendMessage(player, syntax);
	}

}
