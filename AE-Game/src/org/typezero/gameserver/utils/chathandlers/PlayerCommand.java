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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author synchro2
 */
public abstract class PlayerCommand extends ChatCommand {

	public PlayerCommand(String alias) {
		super(alias);
	}

	@Override
	public boolean checkLevel(Player player) {
		return player.getAccessLevel() >= getLevel();
	}

	@Override
	boolean process(Player player, String text) {
		if (!checkLevel(player)) {
			PacketSendUtility.sendMessage(player, "\u0412\u044b \u043d\u0435 \u0438\u043c\u0435\u0435\u0442\u0435 \u043f\u0440\u0430\u0432 \u0434\u043b\u044f \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u044f \u044d\u0442\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b.");
			return true;
		}

		boolean success = false;
		if (text.length() == getAlias().length())
			success = this.run(player, EMPTY_PARAMS);
		else
			success = this.run(player, text.substring(getAlias().length() + 1).split(" "));

		return success;
	}
}
