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

import org.typezero.gameserver.model.gameobjects.LetterType;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.services.mail.MailService;

/**
 * @author Aion Gates, xTz
 */
public class CM_SEND_MAIL extends AionClientPacket {

	private String recipientName;
	private String title;
	private String message;
	private int itemObjId;
	private int itemCount;
	private int kinahCount;
	private int idLetterType;

	public CM_SEND_MAIL(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		recipientName = readS();
		title = readS();
		message = readS();
		itemObjId = readD();
		itemCount = readD();
		readD();
		kinahCount = readD();
		readD();
		idLetterType = readC();
	}

	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		if (!player.isTrading() && kinahCount < 1000000000 && kinahCount > -1 && itemCount > -2) {
			MailService.getInstance().sendMail(player, recipientName, title, message, itemObjId, itemCount, kinahCount, LetterType.getLetterTypeById(idLetterType));
		}
	}
}
