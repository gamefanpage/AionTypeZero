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
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import org.typezero.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET.PacketElementType;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * This server command is used for creating and sending custom packets from server to client. It's used in development
 * purpose.<br>
 * <b>command name: //fsc</b></br> <b>params:</b>
 * <ul>
 * <li>packet id (it's one byte) - maybe in dec format (for example 227), but may be also in hex format (for example
 * 0xE3)</li>
 * <li>package format string - string containing with letters: d (represents writeD()), h (represents writeH()), c
 * (represents writeC()), f (represents writeF()), e (represents write DF()), q (represents writeQ()), s (represents
 * writeS())</li>
 * <li>list of data - here goes all data for corresponding to proper format parts.</li>
 * </ul>
 * Example:<br>
 * //fsc 0xD8 cdds 8 50 80 someText - will send packet with id 0xD8 (subids will be added automaticaly) then will be
 * sent one byte - 8, later two ints -50 and 80 and at the end a String - someText
 *
 * @author Luno
 */
public class Fsc extends AdminCommand {

	public Fsc() {
		super("fsc");
	}

	@Override
	public void execute(Player player, String... params) {
		if (params.length < 3) {
			PacketSendUtility.sendMessage(player, "Incorrent number of params in //fsc command");
			return;
		}

		int id = Integer.decode(params[0]);
		String format = "";

		if (params.length > 1)
			format = params[1];

		SM_CUSTOM_PACKET packet = new SM_CUSTOM_PACKET(id);

		int i = 0;
		for (char c : format.toCharArray()) {
			packet.addElement(PacketElementType.getByCode(c), params[i + 2]);
			i++;
		}
		PacketSendUtility.sendPacket(player, packet);
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "Incorrent number of params in //fsc command");
	}
}
