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

import java.util.Arrays;

import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.configs.administration.CommandsConfig;
import org.typezero.gameserver.model.PlayerClass;
import org.typezero.gameserver.model.gameobjects.VisibleObject;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_TITLE_INFO;
import org.typezero.gameserver.services.abyss.AbyssPointsService;
import org.typezero.gameserver.utils.PacketSendUtility;
import org.typezero.gameserver.utils.chathandlers.AdminCommand;

/**
 * @author Nemiroff, ATracer, IceReaper Date: 11.12.2009
 * @author Sarynth - Added AP
 */
public class Set extends AdminCommand {

	public Set() {
		super("set");
	}

	@Override
	public void execute(Player admin, String... params) {
		Player target = null;
		VisibleObject creature = admin.getTarget();

		if (admin.getTarget() instanceof Player) {
			target = (Player) creature;
		}

		if (target == null) {
			PacketSendUtility.sendMessage(admin, "You should select a target first!");
			return;
		}

		if (params.length < 2) {
			PacketSendUtility.sendMessage(admin, "You should enter second params!");
			return;
		}
		String paramValue = params[1];

		if (params[0].equals("class")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			byte newClass;
			try {
				newClass = Byte.parseByte(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			PlayerClass oldClass = target.getPlayerClass();
			setClass(target, oldClass, newClass);
		}
		else if (params[0].equals("exp")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			long exp;
			try {
				exp = Long.parseLong(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			target.getCommonData().setExp(exp);
			PacketSendUtility.sendMessage(admin, "Set exp of target to " + paramValue);
		}
		else if (params[0].equals("gp")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int gloryPoint;
			try {
				gloryPoint = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

            AbyssPointsService.setAGp(target, 0, gloryPoint);
			if (target == admin) {
				PacketSendUtility.sendMessage(admin, "Set your Glory Points to " + gloryPoint + ".");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Set " + target.getName() + " Glory Points to " + gloryPoint + ".");
				PacketSendUtility.sendMessage(target, "Admin set your Glory Points to " + gloryPoint + ".");
			}
		}
		else if (params[0].equals("ap")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int ap;
			try {
				ap = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			AbyssPointsService.setAGp(target, ap, 0);
			if (target == admin) {
				PacketSendUtility.sendMessage(admin, "Set your Abyss Points to " + ap + ".");
			}
			else {
				PacketSendUtility.sendMessage(admin, "Set " + target.getName() + " Abyss Points to " + ap + ".");
				PacketSendUtility.sendMessage(target, "Admin set your Abyss Points to " + ap + ".");
			}
		}
		else if (params[0].equals("level")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int level;
			try {
				level = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			Player player = target;

			if (level <= GSConfig.PLAYER_MAX_LEVEL)
				player.getCommonData().setLevel(level);

			PacketSendUtility.sendMessage(admin, "Set " + player.getCommonData().getName() + " level to " + level);
		}
		else if (params[0].equals("title")) {
			if (admin.getAccessLevel() < CommandsConfig.SET) {
				PacketSendUtility.sendMessage(admin, "You dont have enough rights to execute this command");
				return;
			}

			int titleId;
			try {
				titleId = Integer.parseInt(paramValue);
			}
			catch (NumberFormatException e) {
				PacketSendUtility.sendMessage(admin, "You should enter valid second params!");
				return;
			}

			Player player = target;
			if (titleId <= 160)
				setTitle(player, titleId);
			PacketSendUtility.sendMessage(admin, "Set " + player.getCommonData().getName() + " title to " + titleId);

		}
	}

	private void setTitle(Player player, int value) {
		PacketSendUtility.sendPacket(player, new SM_TITLE_INFO(value));
		PacketSendUtility.broadcastPacket(player, (new SM_TITLE_INFO(player, value)));
		player.getCommonData().setTitleId(value);
	}

	private void setClass(Player player, PlayerClass oldClass, byte value) {
		PlayerClass playerClass = PlayerClass.getPlayerClassById(value);
		int level = player.getLevel();
		if (level < 9) {
			PacketSendUtility.sendMessage(player, "You can only switch class after reach level 9");
			return;
		}
		if (Arrays.asList(1, 2, 4, 5, 7, 8, 10, 11, 13, 14, 16).contains(oldClass.ordinal())) {
			PacketSendUtility.sendMessage(player, "You already switched class");
			return;
		}
		int newClassId = playerClass.ordinal();
		switch (oldClass.ordinal()) {
			case 0:
				if (newClassId == 1 || newClassId == 2)
					break;
			case 3:
				if (newClassId == 4 || newClassId == 5)
					break;
			case 6:
				if (newClassId == 7 || newClassId == 8)
					break;
			case 9:
				if (newClassId == 10 || newClassId == 11)
					break;
			case 12:
				if (newClassId == 13 || newClassId == 14)
					break;
			case 15:
				if (newClassId == 16)
					break;
			default:
				PacketSendUtility.sendMessage(player, "Invalid class switch chosen");
				return;
		}
		player.getCommonData().setPlayerClass(playerClass);
		player.getController().upgradePlayer();
		PacketSendUtility.sendMessage(player, "You have successfuly switched class");
	}

	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //set <class|exp|ap|gp|level|title>");
	}
}
