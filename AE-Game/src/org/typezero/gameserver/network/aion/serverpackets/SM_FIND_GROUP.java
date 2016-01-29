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

import java.util.Collection;

import org.typezero.gameserver.model.gameobjects.FindGroup;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;

/**
 * @author cura, MrPoke
 */
public class SM_FIND_GROUP extends AionServerPacket {

	private int action;
	private int lastUpdate;
	private Collection<FindGroup> findGroups;
	private int groupSize;
	private int unk;

	public SM_FIND_GROUP(int action, int lastUpdate, Collection<FindGroup> findGroups) {
		this.lastUpdate = lastUpdate;
		this.action = action;
		this.findGroups = findGroups;
		this.groupSize = findGroups.size();
	}

	public SM_FIND_GROUP(int action, int lastUpdate, int unk) {
		this.action = action;
		this.lastUpdate = lastUpdate;
		this.unk = unk;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		switch (action) {
			case 0x00:
			case 0x02:
				writeC(action); // type 0:Recruit Group Members
				writeH(groupSize); // groupSize
				writeH(groupSize); // groupSize
				writeD(lastUpdate); // objId?
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId()); // player object id
					writeD(findGroup.getUnk()); // unk (0 or 65557)
					writeC(findGroup.getGroupType()); // 0:group, 1:alliance
					writeS(findGroup.getMessage()); // text
					writeS(findGroup.getName()); // writer name
					writeC(findGroup.getSize()); // members count
					writeC(findGroup.getMinLevel()); // members																																																					// level
					writeC(findGroup.getMaxLevel()); // members																																																						// level
					writeD(findGroup.getLastUpdate()); // objId?
				}
				break;
			case 0x01:
			case 0x03:
				writeC(0x01); // type 1:Recruit delete
				writeD(lastUpdate); // player object id
				writeD(unk); // unk (0 or 65557)
				break;
			case 0x04:
			case 0x06:
				writeC(action); // type 4:Apply for Group
				writeH(groupSize); // groupSize
				writeH(groupSize); // groupSize
				writeD(lastUpdate); // objId?
				for (FindGroup findGroup : findGroups) {
					writeD(findGroup.getObjectId()); // player object id
					writeC(findGroup.getGroupType()); // 0:group, 1:alliance
					writeS(findGroup.getMessage()); // text
					writeS(findGroup.getName()); // writer name
					writeC(findGroup.getClassId()); // player class id
					writeC(findGroup.getMinLevel()); // player level
					writeD(findGroup.getLastUpdate()); // objId?
				}
				break;
			case 0x05:
				writeC(0x05); // type 5:Apply delete
				writeD(lastUpdate); // player object id
				break;
		}
	}
}
