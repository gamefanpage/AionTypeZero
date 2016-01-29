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

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.team.legion.LegionHistory;
import org.typezero.gameserver.network.aion.AionClientPacket;
import org.typezero.gameserver.network.aion.AionConnection.State;
import org.typezero.gameserver.network.aion.serverpackets.SM_LEGION_TABS;
import org.typezero.gameserver.utils.PacketSendUtility;

/**
 * @author Simple, xTz
 */
public class CM_LEGION_TABS extends AionClientPacket {

	private static final Logger log = LoggerFactory.getLogger(CM_LEGION_TABS.class);

	private int page;
	private int tab;

	public CM_LEGION_TABS(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}

	@Override
	protected void readImpl() {
		page = readD();
		tab = readC();
	}

	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();

		if(activePlayer.getLegion() != null) {

			/**
			 * Max page is 16 for legion history
			 */
			if (page > 16)
				return;

			switch (tab) {
				/**
				 * History Tab
				 */
				case 0: // legion history
				case 2: // legion WH history
					Collection<LegionHistory> history = activePlayer.getLegion().getLegionHistoryByTabId(tab);
					/**
					 * If history size is less than page*8 return
					 */
					if (history.size() < page * 8)
						return;
					if (!history.isEmpty())
						PacketSendUtility.sendPacket(activePlayer, new SM_LEGION_TABS(history, page, tab));
					break;
				/**
				 * Reward Tab
				 */
				case 1:
					//TODO Reward Tab Page
					break;
			}
		}
		else
			log.warn("Player "+activePlayer.getName()+" was requested null legion");
	}
}
