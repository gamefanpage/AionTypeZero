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

package org.typezero.gameserver.network.aion.clientpackets;

import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.configs.main.SecurityConfig;
import org.typezero.gameserver.dao.PlayerPasskeyDAO;
import org.typezero.gameserver.model.account.CharacterPasskey;
import org.typezero.gameserver.model.account.CharacterPasskey.ConnectType;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_CHARACTER_SELECT;
import org.typezero.gameserver.network.aion.serverpackets.SM_DELETE_CHARACTER;
import org.typezero.gameserver.network.loginserver.LoginServer;
import org.typezero.gameserver.services.player.PlayerEnterWorldService;
import org.typezero.gameserver.services.player.PlayerService;

/**
 * @author ginho1
 */
public class CM_CHARACTER_PASSKEY extends AionClientPacket {

	private int type;
	private String passkey;
	private String newPasskey;

	public CM_CHARACTER_PASSKEY(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		type = readH(); // 0:new, 2:update, 3:input
		try {
			passkey = new String(readB(32), "UTF-16le");
			if (type == 2)
				newPasskey = new String(readB(32), "UTF-16le");
		}
		catch (Exception e) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		AionConnection client = getConnection();
		CharacterPasskey chaPasskey = client.getAccount().getCharacterPasskey();

		switch (type) {
			case 0:
				chaPasskey.setIsPass(false);
				chaPasskey.setWrongCount(0);
				DAOManager.getDAO(PlayerPasskeyDAO.class).insertPlayerPasskey(client.getAccount().getId(), passkey);
				client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				break;
			case 2:
				boolean isSuccess = DAOManager.getDAO(PlayerPasskeyDAO.class).updatePlayerPasskey(client.getAccount().getId(),
					passkey, newPasskey);

				chaPasskey.setIsPass(false);
				if (isSuccess) {
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				else {
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				break;
			case 3:
				boolean isPass = DAOManager.getDAO(PlayerPasskeyDAO.class).checkPlayerPasskey(client.getAccount().getId(),
					passkey);

				if (isPass) {
					chaPasskey.setIsPass(true);
					chaPasskey.setWrongCount(0);
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));

					if (chaPasskey.getConnectType() == ConnectType.ENTER)
						PlayerEnterWorldService.startEnterWorld(chaPasskey.getObjectId(), client);
					else if (chaPasskey.getConnectType() == ConnectType.DELETE) {
						PlayerAccountData playerAccData = client.getAccount().getPlayerAccountData(chaPasskey.getObjectId());

						PlayerService.deletePlayer(playerAccData);
						client.sendPacket(new SM_DELETE_CHARACTER(chaPasskey.getObjectId(), playerAccData
							.getDeletionTimeInSeconds()));
					}
				}
				else {
					chaPasskey.setIsPass(false);
					chaPasskey.setWrongCount(chaPasskey.getWrongCount() + 1);
					checkBlock(client.getAccount().getId(), chaPasskey.getWrongCount());
					client.sendPacket(new SM_CHARACTER_SELECT(2, type, chaPasskey.getWrongCount()));
				}
				break;
		}
	}

	/**
	 * @param accountId
	 * @param wrongCount
	 */
	private void checkBlock(int accountId, int wrongCount) {
		if (wrongCount >= SecurityConfig.PASSKEY_WRONG_MAXCOUNT) {
			// TODO : Change the account to be blocked
			LoginServer.getInstance().sendBanPacket((byte) 2, accountId, "", 60 * 8, 0);
		}
	}
}
