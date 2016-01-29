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

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.PlayerDAO;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.services.PunishmentService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.Util;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.world.World;

/**
 * @author nrg
 */
public class BanChar extends AdminCommand {

	public BanChar() {
		super("banchar");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 3) {
			sendInfo(admin, true);
			return;
		}

		int playerId = 0;
		String playerName = Util.convertName(params[0]);

		// First, try to find player in the World
		Player player = World.getInstance().findPlayer(playerName);
		if (player != null)
			playerId = player.getObjectId();

		// Second, try to get player Id from offline player from database
		if (playerId == 0)
			playerId = DAOManager.getDAO(PlayerDAO.class).getPlayerIdByName(playerName);

		// Third, fail
		if (playerId == 0) {
			PacketSendUtility.sendMessage(admin, "Player " + playerName + " was not found!");
			sendInfo(admin, true);
			return;
		}

		int dayCount = -1;
		try {
			dayCount = Integer.parseInt(params[1]);
		}
		catch (NumberFormatException e) {
			PacketSendUtility.sendMessage(admin, "Second parameter is not an int");
			sendInfo(admin, true);
			return;
		}

		if(dayCount < 0) {
			PacketSendUtility.sendMessage(admin, "Second parameter has to be a positive daycount or 0 for infinity");
			sendInfo(admin, true);;
			return;
		}

		String reason = Util.convertName(params[2]);
		for(int itr = 3; itr < params.length; itr++)
			reason += " "+params[itr];

		PacketSendUtility.sendMessage(admin, "Char " + playerName + " is now banned for the next "+dayCount+" days!");

		PunishmentService.banChar(playerId, dayCount, reason);
	}

	@Override
	public void onFail(Player player, String message) {
		sendInfo(player, false);
	}

	private void sendInfo(Player player, boolean withNote) {
		PacketSendUtility.sendMessage(player, "Syntax: //banChar <playername> <days>/0 (for permanent) <reason>");
		if(withNote)
		  PacketSendUtility.sendMessage(player, "Note: the current day is defined as a whole day even if it has just a few hours left!");
	}
}
