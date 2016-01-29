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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author KID
 */
public abstract class ChatCommand {

	private String alias;
	private Byte level;
	static final String[] EMPTY_PARAMS = new String[] {};
	static final Logger log = LoggerFactory.getLogger(ChatCommand.class);

	public ChatCommand(String alias) {
		this.alias = alias;
	}

	public boolean run(Player player, String... params) {
		try {
			execute(player, params);
			return true;
		}
		catch (Exception e) {
			log.error("",e);
			onFail(player, e.getMessage());
			return false;
		}
	}

	public final String getAlias() {
		return alias;
	}

	public void setAccessLevel(Byte level) {
		this.level = level;
	}

	public final Byte getLevel() {
		return level;
	}

	abstract boolean checkLevel(Player player);

	abstract boolean process(Player player, String text);

	public abstract void execute(Player player, String... params);

	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, message);
	}
}
