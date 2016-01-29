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
 */

package org.typezero.gameserver.services.player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.serverpackets.SM_SECURITY_TOKEN_REQUEST_STATUS;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * Created by Magenik on 9/08/2015.
 */
public class PlayerSecurityTokenService {

	private final Logger log = LoggerFactory.getLogger(PlayerSecurityTokenService.class);
	String token;

	public String MD5(String md5) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return token = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			log.warn("[SecurityToken] Error to generate token for player!");
		}
		return null;
	}

	public void generateToken(Player player) {
		if (player == null) {
			log.warn("[SecurityToken] Player don't exist O.o");
			return;
		}

		if (!"".equals(player.getPlayerAccount().getSecurityToken())) {
			log.warn("[SecurityToken] Player with already exist token should'nt get another one!");
			return;
		}

		MD5(player.getName() + "GH58" + player.getRace().toString() + "8HHGZTU");

		player.getPlayerAccount().setSecurityToken(token);
		sendToken(player, player.getPlayerAccount().getSecurityToken());
	}

	public void sendToken(Player player, String token) {
		if (player == null) {
			return;
		}

		PacketSendUtility.sendPacket(player, new SM_SECURITY_TOKEN_REQUEST_STATUS(token));

	}

	public static final PlayerSecurityTokenService getInstance() {
		return SingletonHolder.instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {

		protected static final PlayerSecurityTokenService instance = new PlayerSecurityTokenService();
	}
}
