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

package org.typezero.gameserver.network.aion;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.typezero.gameserver.model.account.PlayerAccountData;
import org.typezero.gameserver.model.gameobjects.Item;
import org.typezero.gameserver.model.gameobjects.player.PlayerAppearance;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.items.GodStone;
import org.typezero.gameserver.model.items.ItemSlot;
import org.typezero.gameserver.model.templates.item.ItemTemplate;

/**
 * @author AEJTester
 * @author Nemesiss
 * @author Niato
 */
public abstract class PlayerInfo extends AionServerPacket {

	private static Logger log = LoggerFactory.getLogger(PlayerInfo.class);

	protected PlayerInfo() {
	}

	protected void writePlayerInfo(PlayerAccountData accPlData) {
		PlayerCommonData pbd = accPlData.getPlayerCommonData();
		final int raceId = pbd.getRace().getRaceId();
		final int genderId = pbd.getGender().getGenderId();
		final PlayerAppearance playerAppearance = accPlData.getAppereance();
		writeD(pbd.getPlayerObjId());
		writeS(pbd.getName(), 52);
		writeD(genderId);
		writeD(raceId);
		writeD(pbd.getPlayerClass().getClassId());
		writeD(playerAppearance.getVoice());
		writeD(playerAppearance.getSkinRGB());
		writeD(playerAppearance.getHairRGB());
		writeD(playerAppearance.getEyeRGB());
		writeD(playerAppearance.getLipRGB());
		writeC(playerAppearance.getFace());
		writeC(playerAppearance.getHair());
		writeC(playerAppearance.getDeco());
		writeC(playerAppearance.getTattoo());
		writeC(playerAppearance.getFaceContour());
		writeC(playerAppearance.getExpression());
		writeC(4);// always 4 o0
		writeC(playerAppearance.getJawLine());
		writeC(playerAppearance.getForehead());
		writeC(playerAppearance.getEyeHeight());
		writeC(playerAppearance.getEyeSpace());
		writeC(playerAppearance.getEyeWidth());
		writeC(playerAppearance.getEyeSize());
		writeC(playerAppearance.getEyeShape());
		writeC(playerAppearance.getEyeAngle());
		writeC(playerAppearance.getBrowHeight());
		writeC(playerAppearance.getBrowAngle());
		writeC(playerAppearance.getBrowShape());
		writeC(playerAppearance.getNose());
		writeC(playerAppearance.getNoseBridge());
		writeC(playerAppearance.getNoseWidth());
		writeC(playerAppearance.getNoseTip());
		writeC(playerAppearance.getCheek());
		writeC(playerAppearance.getLipHeight());
		writeC(playerAppearance.getMouthSize());
		writeC(playerAppearance.getLipSize());
		writeC(playerAppearance.getSmile());
		writeC(playerAppearance.getLipShape());
		writeC(playerAppearance.getJawHeigh());
		writeC(playerAppearance.getChinJut());
		writeC(playerAppearance.getEarShape());
		writeC(playerAppearance.getHeadSize());
		// 1.5.x 0x00, shoulderSize, armLength, legLength (BYTE) after HeadSize

		writeC(playerAppearance.getNeck());
		writeC(playerAppearance.getNeckLength());
		writeC(playerAppearance.getShoulderSize()); // shoulderSize

		writeC(playerAppearance.getTorso());
		writeC(playerAppearance.getChest());
		writeC(playerAppearance.getWaist());
		writeC(playerAppearance.getHips());
		writeC(playerAppearance.getArmThickness());
		writeC(playerAppearance.getHandSize());
		writeC(playerAppearance.getLegThicnkess());
		writeC(playerAppearance.getFootSize());
		writeC(playerAppearance.getFacialRate());
		writeC(0x00); // 0x00
		writeC(playerAppearance.getArmLength()); // armLength
		writeC(playerAppearance.getLegLength()); // legLength
		writeC(playerAppearance.getShoulders());
		writeC(playerAppearance.getFaceShape());
		writeC(0x00); // always 0 may be acessLevel
		writeC(0x00); // always 0 - unk
		writeC(0x00);
		writeF(playerAppearance.getHeight());
		int raceSex = 100000 + raceId * 2 + genderId;
		writeD(raceSex);
		writeD(pbd.getPosition().getMapId());// mapid for preloading map
		writeF(pbd.getPosition().getX());
		writeF(pbd.getPosition().getY());
		writeF(pbd.getPosition().getZ());
		writeD(pbd.getPosition().getHeading());
		writeH(pbd.getLevel());// lvl confirmed
		writeH(0); //unk 2.5
        //writeC(0); // 4.6
        //writeC(64); // 4.6
		writeD(pbd.getTitleId());
		if (accPlData.isLegionMember()) {
			writeD(accPlData.getLegion().getLegionId());
			writeS(accPlData.getLegion().getLegionName(), 82);
		}
		else {
			writeB(new byte[86]);
		}

		writeH(accPlData.isLegionMember() ? 0x01 : 0x00);//is in legion?
		writeD((int)pbd.getLastOnline().getTime());//last online

		int itemsDataSize = 0;
		// TODO figure out this part when fully equipped
		List<Item> items = accPlData.getEquipment();

		for (Item item : items) {
			if(itemsDataSize >= 247)// 247 .4.8
				break;

			ItemTemplate itemTemplate = item.getItemTemplate();
			if (itemTemplate == null) {
				log.warn("Missing item. PlayerId: " + pbd.getPlayerObjId() + " ItemId: " + item.getObjectId());
				continue;
			}

                        long slot = item.getEquipmentSlot();
			if(itemTemplate.isArmor() || itemTemplate.isWeapon()) {
				if (slot <= ItemSlot.PANTS.getSlotIdMask()) {
					writeC(slot == 2 || slot == 64 || slot == 256 ? 2 : 1);
					writeD(item.getItemSkinTemplate().getTemplateId());
					GodStone godStone = item.getGodStone();
					writeD(godStone != null ? godStone.getItemId() : 0);
					writeD(item.getItemColor());
					itemsDataSize += 13;
				}
			}
		}

		byte[] stupidNc = new byte[247 - itemsDataSize];// 247 .4.8
		writeB(stupidNc);
        writeB(new byte[53]);//53 4.8
		writeD(accPlData.getDeletionTimeInSeconds());
	}
}
