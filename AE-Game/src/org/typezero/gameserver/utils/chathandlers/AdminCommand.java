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

package org.typezero.gameserver.utils.chathandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.configs.main.LoggingConfig;
import org.typezero.gameserver.model.gameobjects.Creature;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author synchro2
 */
public abstract class AdminCommand extends ChatCommand {

	static final Logger log = LoggerFactory.getLogger("ADMINAUDIT_LOG");

	public AdminCommand(String alias) {
		super(alias);
	}

	@Override
	public boolean checkLevel(Player player) {
		return player.getAccessLevel() >= getLevel();
	}

	@Override
	boolean process(Player player, String text) {

		if (!checkLevel(player)) {
			if (LoggingConfig.LOG_GMAUDIT)
				log.info("[ADMIN COMMAND] > [Player: " + player.getName() + "] has tried to use the command " + getAlias()
					+ " without having the rights");
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "[WARN] You need to have access level " + this.getLevel() + " or more to use " + getAlias());
				return true;
			}
			return false;
		}

		boolean success = false;
		if (text.length() == getAlias().length())
			success = this.run(player, EMPTY_PARAMS);
		else
			success = this.run(player, text.substring(getAlias().length() + 1).split(" "));

		if (LoggingConfig.LOG_GMAUDIT) {
			if (player.getTarget() != null && player.getTarget() instanceof Creature) {
				Creature target = (Creature) player.getTarget();
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "][Target : " + target.getName() + "]: " + text);
			}
			else
				log.info("[ADMIN COMMAND] > [Name: " + player.getName() + "]: " + text);
		}

		if (!success) {
			PacketSendUtility.sendMessage(player, "<You have failed to execute " + text + ">");
			return true;
		}
		else
			return success;
	}
}
