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
import org.typezero.gameserver.network.aion.serverpackets.SM_GAME_TIME;
import org.typezero.gameserver.spawnengine.TemporarySpawnEngine;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;
import org.typezero.gameserver.utils.gametime.GameTimeManager;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.knownlist.Visitor;

/**
 * @author Pan
 */
public class Time extends AdminCommand {

	public Time() {
		super("time");
	}

	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 1) {
			onFail(admin, null);
			return;
		}

		// Getting current hour and minutes
		int time = GameTimeManager.getGameTime().getHour();
		int min = GameTimeManager.getGameTime().getMinute();
		int hour;

		// If the given param is one of these four, get the correct hour...
		if (params[0].equals("night")) {
			hour = 22;
		}
		else if (params[0].equals("dusk")) {
			hour = 18;
		}
		else if (params[0].equals("day")) {
			hour = 9;
		}
		else if (params[0].equals("dawn")) {
			hour = 4;
		}
		else {
			// If not, check if the param is a number (hour)...
			try {
				hour = Integer.parseInt(params[0]);
			}
			catch (NumberFormatException e) {
				onFail(admin, null);
				return;
			}

			// A day have only 24 hours!
			if (hour < 0 || hour > 23) {
				onFail(admin, null);
				PacketSendUtility.sendMessage(admin, "A day have only 24 hours!\n" + "Min value : 0 - Max value : 23");
				return;
			}
		}

		// Calculating new time in minutes...
		time = hour - time;
		time = GameTimeManager.getGameTime().getTime() + (60 * time) - min;

		// Reloading the time, restarting the clock...
		GameTimeManager.reloadTime(time);

		// Checking the new daytime
		GameTimeManager.getGameTime().calculateDayTime();

		World.getInstance().doOnAllPlayers(new Visitor<Player>() {

			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_GAME_TIME());
			}
		});
		TemporarySpawnEngine.spawnAll();

		PacketSendUtility.sendMessage(admin, "You changed the time to " + params[0].toString() + ".");
	}

	@Override
	public void onFail(Player player, String message) {
		String syntax = "Syntax: //time < dawn | day | dusk | night | desired hour (number) >";
		PacketSendUtility.sendMessage(player, syntax);
	}

}
