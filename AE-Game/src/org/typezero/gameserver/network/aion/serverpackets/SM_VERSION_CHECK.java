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


import org.typezero.gameserver.GameServer;
import org.typezero.gameserver.configs.main.EventsConfig;
import org.typezero.gameserver.configs.main.GSConfig;
import org.typezero.gameserver.configs.main.MembershipConfig;
import org.typezero.gameserver.configs.network.IPConfig;
import org.typezero.gameserver.configs.network.NetworkConfig;
import org.typezero.gameserver.model.Race;
import org.typezero.gameserver.network.NetworkController;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.ChatService;

/**
 * @author -Nemesiss- CC fix
 * @modified by Novo, cura
 */
public class SM_VERSION_CHECK extends AionServerPacket {
	/**
	 * Aion Client version
	 */
	private int version;
	/**
	 * Number of characters can be created
	 */
	private int characterLimitCount;

	/**
	 * Related to the character creation mode
	 */
	private final int characterFactionsMode;
	private final int characterCreateMode;

	/**
	 * @param chatService
	 */
	public SM_VERSION_CHECK(int version) {
		this.version = version;

		if (MembershipConfig.CHARACTER_ADDITIONAL_ENABLE != 10 && MembershipConfig.CHARACTER_ADDITIONAL_COUNT > GSConfig.CHARACTER_LIMIT_COUNT) {
			characterLimitCount = MembershipConfig.CHARACTER_ADDITIONAL_COUNT;
		}
		else {
			characterLimitCount = GSConfig.CHARACTER_LIMIT_COUNT;
		}
		
		characterLimitCount *= NetworkController.getInstance().getServerCount();

		if (GSConfig.CHARACTER_CREATION_MODE < 0 || GSConfig.CHARACTER_CREATION_MODE > 2)
			characterFactionsMode = 0;
		else
			characterFactionsMode = GSConfig.CHARACTER_CREATION_MODE;

		if (GSConfig.CHARACTER_FACTION_LIMITATION_MODE < 0 || GSConfig.CHARACTER_FACTION_LIMITATION_MODE > 3)
			characterCreateMode = 0;
		else
			characterCreateMode = GSConfig.CHARACTER_FACTION_LIMITATION_MODE * 0x04;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		//aion 3.0 = 194
		//aion 3.5 = 196
		//aion 4.0 = 201
		//aion 4.5 = 203
		if(version < 207) {
			//Send wrong client version
			writeC(0x02);
			return;
		}
		writeC(0x00);
		writeC(NetworkConfig.GAMESERVER_ID);
		writeD(150213);// start year month day
		writeD(150210);// start year month day
		writeD(0x00);// spacing
		writeD(150210);// year month day
		writeD(1424939049);// start server time in mili
		writeC(0x00);// unk
		writeC(GSConfig.SERVER_COUNTRY_CODE);// country code;
		writeC(0x00);// unk

		int serverMode = (characterLimitCount * 0x10) | characterFactionsMode;

		if (GSConfig.ENABLE_RATIO_LIMITATION) {
			if (GameServer.getCountFor(Race.ELYOS) + GameServer.getCountFor(Race.ASMODIANS) > GSConfig.RATIO_HIGH_PLAYER_COUNT_DISABLING)
				writeC(serverMode | 0x0C);
			else if (GameServer.getRatiosFor(Race.ELYOS) > GSConfig.RATIO_MIN_VALUE)
				writeC(serverMode | 0x04);
			else if (GameServer.getRatiosFor(Race.ASMODIANS) > GSConfig.RATIO_MIN_VALUE)
				writeC(serverMode | 0x08);
			else
				writeC(serverMode);
		}
		else {
			writeC(serverMode | characterCreateMode);
		}

		writeD((int) (System.currentTimeMillis() / 1000));
        writeH(350); //4.7
        writeH(257); //4.7
        writeH(2561); //4.7
        writeH(13061); //4.7
        writeH(257); //4.7
        writeH(2); //4.7
        writeC(GSConfig.CHARACTER_REENTRY_TIME);// 20sec
        switch(EventsConfig.ENABLE_DECOR)
        {
        case 01:
					writeC(0x01); //CHRISTMAS_DECOR
					break;
        case 02:
					writeC(0x02); //HALLOWEEN_DECOR
					break;
        case 03:
					writeC(0x08); //BRAXCAFE_DECOR
					break;
        case 04:
					writeC(0x04); //VALENTINE_DECOR
					break;
        default:
					writeC(EventsConfig.ENABLE_DECOR); //no decorations
					break;
        }
        writeC(0);//4.5
        writeC(0);//4.5
        writeH(0);//4.7
        writeD(-10800);//4.8 //
        writeD(1653700612);//4.8
        writeC(2);//4.5
        writeD(0);// 4.3
        writeD(0);// 4.5
        writeC(0);//4.7
        writeH(3000);//4.7
        writeH(1);//4.7
        writeC(0);//4.7
        writeC(1);//4.7.5
        writeD(0);// 4.5
        writeH(0x01); //its loop size
        writeD(0x00); //unk4.8
        writeD(0x00); //unk4.8
        writeD(0x00); //unk4.8
        writeD(0x00); //unk4.8

        writeD(1000); //unk4.8
        writeD(150); //unk4.8 //
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8
        writeD(1000); //unk4.8

        writeC(0x00); //unk4.8
        writeH(0x01); ////its loop size
		//for... chat servers?
		{
			writeC(0x00);//spacer
			// if the correct ip is not sent it will not work
			writeB(IPConfig.getDefaultAddress());
			writeH(ChatService.getPort());
		}
	}
}
