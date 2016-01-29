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

import java.util.List;

import org.typezero.gameserver.model.DialogPage;
import org.typezero.gameserver.model.gameobjects.AionObject;
import org.typezero.gameserver.model.gameobjects.Npc;
import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.services.player.PlayerMailboxState;
import org.typezero.gameserver.world.MapRegion;
import org.typezero.gameserver.world.World;
import org.typezero.gameserver.world.zone.ZoneInstance;

/**
 * @author alexa026
 */
public class SM_DIALOG_WINDOW extends AionServerPacket {

	private int targetObjectId;
	private int dialogID;
	private int questId = 0;

	public SM_DIALOG_WINDOW(int targetObjectId, int dlgID) {
		this.targetObjectId = targetObjectId;
		this.dialogID = dlgID;
	}

	public SM_DIALOG_WINDOW(int targetObjectId, int dlgID, int questId) {
		this.targetObjectId = targetObjectId;
		this.dialogID = dlgID;
		this.questId = questId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		Player player = con.getActivePlayer();

		writeD(targetObjectId);
		writeH(dialogID);
		writeD(questId);
		writeH(0);
		if (this.dialogID == DialogPage.MAIL.id()) {
			AionObject object = World.getInstance().findVisibleObject(targetObjectId);
			if (object != null && object instanceof Npc) {
				Npc znpc = (Npc) object;
				if (znpc.getNpcId() == 798100 || znpc.getNpcId() == 798101) {
					player.getMailbox().mailBoxState = PlayerMailboxState.EXPRESS;
					writeH(2);
				}
				else
					player.getMailbox().mailBoxState = PlayerMailboxState.REGULAR;
			}
			else
				writeH(0);
		}
		else if (this.dialogID == DialogPage.HTML_PAGE_SHOW_TOWN_CHALLENGE_TASK.id()) {
			AionObject object = World.getInstance().findVisibleObject(targetObjectId);
			if (object != null && object instanceof Npc) {
				Npc npc = (Npc) object;
				if (npc.getNpcId() == 205770 || npc.getNpcId() == 730677 || npc.getNpcId() == 730679) {
					int townId = 0;
					MapRegion region = npc.getPosition().getMapRegion();
					if (region == null) {
						// some npc without region !!!
					}
					else {
						List<ZoneInstance> zones = region.getZones(npc);
						for (ZoneInstance zone : zones) {
							townId = zone.getTownId();
							if (townId > 0)
								break;
						}
						writeH(townId);
					}
				}
			}
		}
		else
			writeH(0);
	}

}
