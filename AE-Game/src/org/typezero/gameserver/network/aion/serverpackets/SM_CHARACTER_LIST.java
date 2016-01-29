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

package org.typezero.gameserver.network.aion.serverpackets;


import com.aionemu.commons.database.dao.DAOManager;
import org.typezero.gameserver.dao.MailDAO;
import org.typezero.gameserver.model.account.Account;
import org.typezero.gameserver.model.account.CharacterBanInfo;
import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.PlayerInfo;
import org.typezero.gameserver.services.BrokerService;
import org.typezero.gameserver.services.player.PlayerService;
import java.util.Iterator;

/**
 * In this packet Server is sending Character List to client.
 *
 * @author Nemesiss, AEJTester
 */
public class SM_CHARACTER_LIST extends PlayerInfo {

	/**
	 * PlayOk2 - we dont care...
	 */
	private final int playOk2;

	/**
	 * Constructs new <tt>SM_CHARACTER_LIST </tt> packet
	 */
	public SM_CHARACTER_LIST(int playOk2) {
		this.playOk2 = playOk2;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    protected void writeImpl(AionConnection con) {
        writeD(playOk2);
        Account account = con.getAccount();
        writeC(account.size()); // characters count

        Iterator<PlayerAccountData> iter = account.getSortedAccountsList().iterator();

        while (iter.hasNext()) {
            PlayerAccountData playerData = (PlayerAccountData) iter.next();
            PlayerCommonData pcd = playerData.getPlayerCommonData();
            CharacterBanInfo cbi = playerData.getCharBanInfo();
            Player player = PlayerService.getPlayer(pcd.getPlayerObjId(), account);
            writePlayerInfo(playerData);

            writeH(player.getPlayerSettings().getDisplay());
            writeH(player.getPlayerSettings().getDeny());
            writeD(pcd.getMailboxLetters());
            writeD(DAOManager.getDAO(MailDAO.class).haveUnread(pcd.getPlayerObjId()) ? 1 : 0); // mail
            writeQ(0); //unk
            writeQ(BrokerService.getInstance().getCollectedMoney(pcd));
            writeD(0); // Unk 4.7
            writeD(0); // Unk 4.7
            writeD(0); // Unk 4.7
            writeD(0); // Unk 4.7
            writeD(0); // Unk 4.7

            if (cbi != null && cbi.getEnd() > System.currentTimeMillis() / 1000) {
                // client wants int so let's hope we do not reach long limit with timestamp while this server is used :P
                writeD((int) cbi.getStart()); // startPunishDate
                writeD((int) cbi.getEnd()); // endPunishDate
                writeS(cbi.getReason());
            }
            else {
                writeD(0);
                writeD(0);
                writeH(0);
            }
        }
    }
}
