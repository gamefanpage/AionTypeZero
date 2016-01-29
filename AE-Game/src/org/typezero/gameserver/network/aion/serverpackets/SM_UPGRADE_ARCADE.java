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

import org.typezero.gameserver.dataholders.DataManager;
import org.typezero.gameserver.model.templates.arcadeupgrade.ArcadeTab;
import org.typezero.gameserver.model.templates.arcadeupgrade.ArcadeTabItemList;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import java.util.List;

/**
 * @author Raziel
 */
public class SM_UPGRADE_ARCADE extends AionServerPacket {

	private int action;
	private int showicon = 1;

	public SM_UPGRADE_ARCADE(boolean showicon) {
		this.action = 0;
		this.showicon = showicon? 1 : 0;
	}

	public SM_UPGRADE_ARCADE() {
		this.action = 1;
	}

	public SM_UPGRADE_ARCADE(int action) {
		this.action = action;
	}

	@Override
	protected void writeImpl(AionConnection con) {
		writeC(action);

		switch(action)
		{
			case 0://show icon
				writeD(showicon);
			break;
			case 1: //show start upgrade arcade info
				writeD(64519);//SessionId
				writeD(0);//frenzy meter
				writeD(1);
				writeD(4);
				writeD(6);
				writeD(8);
				writeD(8);//max upgrade
				writeH(272);
				writeS("success_weapon01");
				writeS("success_weapon01");
				writeS("success_weapon01");
				writeS("success_weapon02");
				writeS("success_weapon02");
				writeS("success_weapon03");
				writeS("success_weapon03");
				writeS("success_weapon04");
			break;
			case 3: //try result
				writeC(1);//1 success - 0 fail
				writeD(8);//frenzyPoints
			break;
			case 4: //try result
				writeD(2);//upgradeLevel
			break;
			case 5: //show fail
				writeD(1);//upgradeLevel
				writeC(0);//canResume? 1 yes - 0 no
				writeD(0);//needed Arcade Token
				writeD(0);//unk
			break;
			case 6: //show reward icon
				writeD(188052654);//templateId
				writeD(1);//itemCount
				writeD(0);//unk
			break;
			case 10: //show reward list
				List<ArcadeTab> arcadeTabs = DataManager.ARCADE_UPGRADE_DATA.getArcadeTabs();

				for (ArcadeTab arcadetab : arcadeTabs){
					writeC(arcadetab.getArcadeTabItems().size());
				}

				for (ArcadeTab arcadetab : arcadeTabs){
					for (ArcadeTabItemList arcadetabitem : arcadetab.getArcadeTabItems()){
						writeD(arcadetabitem.getId());
						writeD(arcadetabitem.getUncheckedcount());
						writeD(0);
						writeD(arcadetabitem.getCheckedcount());
						writeD(0);
					}
				}
			break;
		}
	}
}
