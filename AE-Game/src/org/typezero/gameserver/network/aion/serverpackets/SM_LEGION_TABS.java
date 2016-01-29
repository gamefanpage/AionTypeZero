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

import org.typezero.gameserver.model.team.legion.LegionHistory;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import java.util.Collection;

/**
	 * @author Simple, KID, xTz
 */
public class SM_LEGION_TABS extends AionServerPacket {

	private int page;
	private Collection<LegionHistory> legionHistory;
	private int tabId;

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int tabId) {
		this.legionHistory = legionHistory;
		this.page = 0;
		this.tabId = tabId;
	}

	public SM_LEGION_TABS(Collection<LegionHistory> legionHistory, int page, int tabId) {
		this.legionHistory = legionHistory;
		this.page = page;
		this.tabId = tabId;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		int size = legionHistory.size();
		/**
		 * If history size is less than page*8 return
		 */
		if (size < (page * 8))
			return;

		// TODO: Formula's could use a refactor
		int hisSize = size - (page * 8);
		if(size > (page + 1) * 8)
			hisSize = 8;

		writeD(size);
		writeD(page); // current page
		writeD(hisSize);

		int i = 0;
		for (LegionHistory history : legionHistory) {
			if (i >= (page * 8) && i <= (8 + (page * 8))) {
				writeD((int) (history.getTime().getTime() / 1000));
				writeC(history.getLegionHistoryType().getHistoryId());
				writeC(0); // unk
				writeS(history.getName(), 64);
				writeH(0); // separator
				writeS(history.getDescription(), 64);
				writeD(0);
			}
			i++;
			if (i >= (8 + (page * 8)))
				break;
		}
		writeC(tabId);
		writeC(0);
	}

}
