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

import org.typezero.gameserver.model.team.legion.Legion;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Simple
 */
public class SM_LEGION_INFO extends AionServerPacket {

	/** Legion information **/
	private Legion legion;

	/**
	 * This constructor will handle legion info
	 *
	 * @param legion
	 */
	public SM_LEGION_INFO(Legion legion) {
		this.legion = legion;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeS(legion.getLegionName());
		writeC(legion.getLegionLevel());
		writeD(legion.getLegionRank());
		writeH(legion.getDeputyPermission());
		writeH(legion.getCenturionPermission());
		writeH(legion.getLegionaryPermission());
		writeH(legion.getVolunteerPermission());
		writeQ(legion.getContributionPoints());
		writeD(0x00); // unk
		writeD(0x00); // unk
		writeD(0x00); // unk 3.0
		/** Get Announcements List From DB By Legion **/
		Map<Timestamp, String> announcementList = legion.getAnnouncementList().descendingMap();

		/** Show max 7 announcements **/
		int i = 0;
		for (Timestamp unixTime : announcementList.keySet()) {
			writeS(announcementList.get(unixTime));
			writeD((int) (unixTime.getTime() / 1000));
			i++;
			if (i >= 7)
				break;
		}
	}
}
