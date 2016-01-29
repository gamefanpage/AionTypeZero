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

import java.util.Iterator;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author Ben, Ritsu Smart Matching Enabled //announce anon This will work. as well as //announce a This will work.
 *         Both will match the "a" or "anon" to the "anonymous" flag.
 */
public class Announce extends AdminCommand {

	public Announce() {
		super("announce");
	}

	@Override
	public void execute(Player player, String... params) {
		String message;

		if (("anonymous").startsWith(params[0].toLowerCase())) {
			message = "\u041e\u0431\u044c\u044f\u0432\u043b\u0435\u043d\u0438\u0435: ";
		}
		else if (("name").startsWith(params[0].toLowerCase())) {
			message = player.getName() + ": ";
		}
		else {
			PacketSendUtility.sendMessage(player, "Syntax: //announce <anonymous|name> <message>");
			return;
		}

		// Add with space
		for (int i = 1; i < params.length - 1; i++)
			message += params[i] + " ";

		// Add the last without the end space
		message += params[params.length - 1];

		Iterator<Player> iter = World.getInstance().getPlayersIterator();

		while (iter.hasNext()) {
			PacketSendUtility.sendBrightYellowMessageOnCenter(iter.next(), message);
		}
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Syntax: //announce <anonymous|name> <message>");
	}
}
