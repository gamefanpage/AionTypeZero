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

import org.typezero.gameserver.model.gameobjects.player.Player;
import org.typezero.gameserver.model.gameobjects.player.PlayerCommonData;
import org.typezero.gameserver.model.stats.container.PlayerLifeStats;
import org.typezero.gameserver.model.team2.alliance.PlayerAllianceMember;
import org.typezero.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import org.typezero.gameserver.network.aion.AionConnection;
import org.typezero.gameserver.network.aion.AionServerPacket;
import org.typezero.gameserver.skillengine.model.Effect;
import org.typezero.gameserver.world.WorldPosition;

import java.util.List;

/**
 * @author Sarynth (Thx Rhys2002 for Packets)
 */
public class SM_ALLIANCE_MEMBER_INFO extends AionServerPacket {

	private Player player;
	private PlayerAllianceEvent event;
	private final int allianceId;
	private final int objectId;

	public SM_ALLIANCE_MEMBER_INFO(PlayerAllianceMember member, PlayerAllianceEvent event) {
		this.player = member.getObject();
		this.event = event;
		this.allianceId = member.getAllianceId();
		this.objectId = member.getObjectId();
	}

	@Override
	protected void writeImpl(AionConnection con) {
		PlayerCommonData pcd = player.getCommonData();
		WorldPosition wp = pcd.getPosition();

		/**
		 * Required so that when member is disconnected, and his playerAllianceGroup slot is changed, he will continue to
		 * appear as disconnected to the alliance.
		 */
		if (event == PlayerAllianceEvent.ENTER && !player.isOnline())
			event = PlayerAllianceEvent.ENTER_OFFLINE;

		writeD(allianceId);
		writeD(objectId);
		if (player.isOnline()) {
			PlayerLifeStats pls = player.getLifeStats();
			writeD(pls.getMaxHp());
			writeD(pls.getCurrentHp());
			writeD(pls.getMaxMp());
			writeD(pls.getCurrentMp());
			writeD(pls.getMaxFp());
			writeD(pls.getCurrentFp());
		}
		else {
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}

		writeD(0);//unk 3.5
		writeD(wp.getMapId());
		writeD(wp.getMapId());
		writeF(wp.getX());
		writeF(wp.getY());
		writeF(wp.getZ());
		writeC(pcd.getPlayerClass().getClassId());
		writeC(pcd.getGender().getGenderId());
		writeC(pcd.getLevel());
		writeC(this.event.getId());
		writeH(0x00); // channel 0x01?
		writeC(0x0);
		switch (this.event) {
			case LEAVE:
			case LEAVE_TIMEOUT:
			case BANNED:
			case MOVEMENT:
			case DISCONNECTED:
				break;

			case JOIN:
			case ENTER:
			case ENTER_OFFLINE:
			case UPDATE:
			case RECONNECT:
			case APPOINT_VICE_CAPTAIN: // Unused maybe...
			case DEMOTE_VICE_CAPTAIN:
			case APPOINT_CAPTAIN:
				writeS(pcd.getName());
				writeD(0x00); // unk
				writeD(0x00); // unk
				writeC(127); // TODO unk 4.5 slots ? can be 0x7F:127, 0x04:4, 0x01:1
				if (player.isOnline()) {
					List<Effect> abnormalEffects = player.getEffectController().getAbnormalEffects();
					writeH(abnormalEffects.size());
					for (Effect effect : abnormalEffects) {
						writeD(effect.getEffectorId());
						writeH(effect.getSkillId());
						writeC(effect.getSkillLevel());
                        if(effect.getTargetSlot()== 3) {
                            writeC(0); // unk ?
                        }
                        else {
                            writeC(effect.getTargetSlot()); // unk ?
                        }
						writeD(effect.getRemainingTime());
					}
				}
				else {
					writeH(0);
				}
				break;
			case MEMBER_GROUP_CHANGE:
				writeS(pcd.getName());
				break;
			default:
				break;
		}
	}

}
