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

import org.typezero.gameserver.configs.administration.CommandsConfig;
import org.typezero.gameserver.configs.main.CustomConfig;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author -Evilwizard-, Wakizashi World Channel, only for GM/Admins
 */
public class Wc extends AdminCommand {

	public Wc() {
		super("wc");
	}

	@Override
	public void execute(Player admin, String... params) {
		int i = 1;
		boolean check = true;
		Race adminRace = admin.getRace();

		if (params.length < 2) {
			PacketSendUtility.sendMessage(admin, "syntax : //wc <ELY | ASM | ALL | default> <message>");
			return;
		}

		StringBuilder sbMessage;
		if (params[0].equals("ELY")) {
			sbMessage = new StringBuilder("[World-Elyos]" + admin.getName() + ": ");
			adminRace = Race.ELYOS;
		}
		else if (params[0].equals("ASM")) {
			sbMessage = new StringBuilder("[World-Asmodian]" + admin.getName() + ": ");
			adminRace = Race.ASMODIANS;
		}
		else if (params[0].equals("ALL"))
			sbMessage = new StringBuilder("[World-All]" + admin.getName() + ": ");
		else {
			check = false;
			if (adminRace == Race.ELYOS)
				sbMessage = new StringBuilder("[World-Elyos]" + admin.getName() + ": ");
			else
				sbMessage = new StringBuilder("[World-Asmodian]" + admin.getName() + ": ");
		}

		for (String s : params)
			if (i++ != 1 && (check))
				sbMessage.append(s + " ");

		String message = sbMessage.toString().trim();
		int messageLenght = message.length();

		final String sMessage = message.substring(0, CustomConfig.MAX_CHAT_TEXT_LENGHT > messageLenght ? messageLenght : CustomConfig.MAX_CHAT_TEXT_LENGHT);
		final boolean toAll = params[0].equals("ALL");
		final Race race = adminRace;

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				if (toAll || player.getRace() == race || player.getAccessLevel() >= CommandsConfig.WC) {
					PacketSendUtility.sendMessage(player, sMessage);
				}
			}
		});
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax : //wc <ELY | ASM | ALL | default> <message>");
	}
}
