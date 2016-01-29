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

import org.typezero.gameserver.model.gameobjects.HouseDecoration;
import org.typezero.gameserver.model.gameobjects.SummonedHouseNpc;
import org.typezero.gameserver.model.house.House;
import org.typezero.gameserver.model.team.legion.LegionEmblem;
import org.typezero.gameserver.model.team.legion.LegionMember;
import org.typezero.gameserver.model.templates.housing.BuildingType;
import org.typezero.gameserver.model.templates.housing.PartType;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.LegionService;
import com.mysql.jdbc.StringUtils;

/**
 * @author Rolandas
 */
public class SM_HOUSE_RENDER extends AionServerPacket {

	private House house;

	public SM_HOUSE_RENDER(House house) {
		this.house = house;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeD(0);
		writeD(house.getAddress().getId());

		int playerObjectId = house.getOwnerId();
		writeD(playerObjectId);
		writeD(BuildingType.PERSONAL_FIELD.getId());
		writeC(1); // unk

		writeD(house.getBuilding().getId());
		writeC(house.getHousingFlags()); // unk 2 or 3 without owner, 5 or 3 with owner

		int doorState = house.getSettingFlags() >> 8;
		writeC(doorState); // 1 - opened doors; 2 - open friends; 3 - closed doors

		int dataSize = 52;
		if (house.getButler() != null) {
			SummonedHouseNpc butler = (SummonedHouseNpc) house.getButler();
			if (!StringUtils.isNullOrEmpty(butler.getMasterName())) {
				dataSize -= (butler.getMasterName().length() + 1) * 2;
				writeS(butler.getMasterName()); // owner name
			}
		}

		// TODO: various messages, some crypted tags
		for (int i = 0; i < dataSize; i++)
			writeC(0);

		LegionMember member = LegionService.getInstance().getLegionMember(playerObjectId);
		writeD(member == null ? 0 : member.getLegion().getLegionId());

		 // show/hide owner name
		if (house.getSettingFlags() == 0) {
			writeC(playerObjectId == 0 ? 0 : 1);
		}
		else {
			writeC(house.getSettingFlags() & 0xF);
		}

		dataSize = 130;
		// TODO: Sign Notice message
		for (int i = 0; i < dataSize; i++)
			writeC(0);

		writePartData(house, PartType.ROOF, 0, true);
		writePartData(house, PartType.OUTWALL, 0, true);
		writePartData(house, PartType.FRAME, 0, true);
		writePartData(house, PartType.DOOR, 0, true);
		writePartData(house, PartType.GARDEN, 0, true);
		writePartData(house, PartType.FENCE, 0, true);

		for (int floor = 0; floor < 6; floor++) {
			writePartData(house, PartType.INWALL_ANY, floor, floor > 0);
		}

		for (int floor = 0; floor < 6; floor++) {
			writePartData(house, PartType.INFLOOR_ANY, floor, floor > 0);
		}

		writePartData(house, PartType.ADDON, 0, true);
		writeD(0);
		writeD(0);
		writeC(0);

		// Emblem and color
		if (member == null || member.getLegion().getLegionEmblem() == null) {
			writeC(0);
			writeC(0);
			writeD(0);
		}
		else {
			LegionEmblem emblem = member.getLegion().getLegionEmblem();
			writeC(emblem.getEmblemId());
			writeC(emblem.getEmblemType().getValue());
			writeC(emblem.isDefaultEmblem() ? 0x0 : 0xFF); // Alpha Channel
			writeC(emblem.getColor_r());
			writeC(emblem.getColor_g());
			writeC(emblem.getColor_b());
		}

	}

	private void writePartData(House house, PartType partType, int floor, boolean skipPersonal) {
		boolean isPersonal = house.getBuilding().getType() == BuildingType.PERSONAL_INS;
		HouseDecoration deco = house.getRenderPart(partType, floor);
		if (skipPersonal && isPersonal)
			writeD(0);
		else
			writeD(deco != null ? deco.getTemplate().getId() : 0);
	}

}
